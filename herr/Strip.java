import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.nio.file.*;
import java.util.zip.*;
import java.io.*;

public class Strip {
    public static void main(String[] a) throws Exception {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(a[0]))) {
            out.setLevel(9);
            java.util.Set<String> seen = new java.util.HashSet<>();
            for (int k = 1; k < a.length; k++) {
                try (ZipFile z = new ZipFile(a[k])) {
                    var en = z.entries();
                    while (en.hasMoreElements()) {
                        ZipEntry e = en.nextElement();
                        String n = e.getName();
                        if (!n.endsWith(".class") || n.contains("module-info") || n.startsWith("META-INF/")) continue;
                        if (!seen.add(n)) continue;
                        byte[] b = z.getInputStream(e).readAllBytes();
                        byte[] r;
                        try {
                            ClassFile cf = ClassFile.of(ClassFile.StackMapsOption.DROP_STACK_MAPS);
                            ClassModel m = cf.parse(b);
                            if ((m.flags().flagsMask() & 0x0001) == 0 && !n.contains("$")) { /* keep package-private too */ }
                            r = cf.transformClass(m, (cb, ce) -> {
                                if (ce instanceof MethodModel mm) {
                                    if ((mm.flags().flagsMask() & 0x0002) != 0) return; // private
                                    cb.transformMethod(mm, (mb, me) -> {
                                        if (me instanceof CodeModel) return;
                                        mb.with(me);
                                    });
                                } else if (ce instanceof FieldModel fm) {
                                    if ((fm.flags().flagsMask() & 0x0002) != 0) return;
                                    cb.with(fm);
                                } else if (ce instanceof SourceFileAttribute || ce instanceof SourceDebugExtensionAttribute) {
                                } else cb.with(ce);
                            });
                            // bajar la versión a Java 21 (65) para poder leerlo con javac 21
                            r[6] = 0; r[7] = 65;
                        } catch (Throwable t) { System.err.println("skip " + n + " " + t); continue; }
                        out.putNextEntry(new ZipEntry(n)); out.write(r); out.closeEntry();
                    }
                }
            }
        }
    }
}
