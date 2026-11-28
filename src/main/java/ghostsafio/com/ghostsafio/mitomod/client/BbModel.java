package com.ghostsafio.mitomod.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class BbModel {
   public static final int FULL_BRIGHT = 15728880;
   private final List<BbModel.Bone> bones = new ArrayList<>();
   private final Map<String, Integer> nameIndex = new HashMap<>();
   private final Map<String, BbModel.Clip> clips = new HashMap<>();

   public static BbModel load(ResourceManager manager, ResourceLocation id) throws IOException {
      try (
         InputStream in = manager.open(id);
         Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      ) {
         return new BbModel(JsonParser.parseReader(reader).getAsJsonObject());
      }
   }

   public int boneCount() {
      return this.bones.size();
   }

   public int indexOf(String name) {
      Integer i = this.nameIndex.get(name);
      return i == null ? -1 : i;
   }

   public BbModel.Clip clip(String name) {
      return this.clips.get(name);
   }

   private BbModel(JsonObject root) {
      float texW = root.get("texW").getAsFloat();
      float texH = root.get("texH").getAsFloat();

      for (JsonElement be : root.getAsJsonArray("bones")) {
         JsonObject bo = be.getAsJsonObject();
         BbModel.Bone b = new BbModel.Bone();
         b.name = bo.get("name").getAsString();
         b.parent = bo.get("parent").getAsInt();
         float[] pivot = floats(bo.getAsJsonArray("pivot"));
         float[] rot = floats(bo.getAsJsonArray("rot"));
         b.px = pivot[0];
         b.py = pivot[1];
         b.pz = pivot[2];
         b.rx = rot[0];
         b.ry = rot[1];
         b.rz = rot[2];

         for (JsonElement ce : bo.getAsJsonArray("cubes")) {
            this.addCube(b, ce.getAsJsonObject(), texW, texH);
         }

         int index = this.bones.size();
         this.bones.add(b);
         this.nameIndex.put(b.name, index);
         if (b.parent >= 0) {
            this.bones.get(b.parent).children.add(index);
         }
      }

      JsonObject anims = root.getAsJsonObject("anims");

      for (Entry<String, JsonElement> ae : anims.entrySet()) {
         JsonObject ao = ae.getValue().getAsJsonObject();
         BbModel.Clip clip = new BbModel.Clip();
         clip.length = ao.get("length").getAsFloat();
         clip.loop = ao.get("loop").getAsBoolean();

         for (Entry<String, JsonElement> be : ao.getAsJsonObject("bones").entrySet()) {
            JsonObject channels = be.getValue().getAsJsonObject();
            BbModel.Track[] tracks = new BbModel.Track[]{track(channels, "rotation"), track(channels, "position"), track(channels, "scale")};
            clip.bones.put(be.getKey(), tracks);
         }

         this.clips.put(ae.getKey(), clip);
      }
   }

   private static BbModel.Track track(JsonObject channels, String name) {
      if (!channels.has(name)) {
         return null;
      }

      JsonArray arr = channels.getAsJsonArray(name);
      int n = arr.size();
      BbModel.Track tr = new BbModel.Track();
      tr.t = new float[n];
      tr.v = new float[n][3];
      tr.smooth = new boolean[n];

      for (int i = 0; i < n; i++) {
         JsonArray k = arr.get(i).getAsJsonArray();
         tr.t[i] = k.get(0).getAsFloat();
         tr.v[i][0] = k.get(1).getAsFloat();
         tr.v[i][1] = k.get(2).getAsFloat();
         tr.v[i][2] = k.get(3).getAsFloat();
         tr.smooth[i] = k.get(4).getAsInt() == 1;
      }

      return tr;
   }

   private static float[] floats(JsonArray a) {
      float[] r = new float[a.size()];

      for (int i = 0; i < r.length; i++) {
         r[i] = a.get(i).getAsFloat();
      }

      return r;
   }

   private void addCube(BbModel.Bone bone, JsonObject c, float texW, float texH) {
      float[] f = floats(c.getAsJsonArray("f"));
      float[] t = floats(c.getAsJsonArray("t"));
      float[] o = floats(c.getAsJsonArray("o"));
      Quaternionf rq = null;
      if (c.has("r")) {
         float[] r = floats(c.getAsJsonArray("r"));
         rq = new Quaternionf().rotationZYX((float)Math.toRadians(r[2]), (float)Math.toRadians(r[1]), (float)Math.toRadians(r[0]));
      }

      boolean pilot = c.has("p");
      JsonObject uvs = c.getAsJsonObject("uv");

      for (String key : new String[]{"n", "e", "s", "w", "u", "d"}) {
         if (uvs.has(key)) {
            float[] uv = floats(uvs.getAsJsonArray(key));
            float[][] corners = corners(key, f, t);
            float[] nrm = normal(key);
            BbModel.Quad q = new BbModel.Quad();

            for (int i = 0; i < 4; i++) {
               Vector3f p = new Vector3f(corners[i][0], corners[i][1], corners[i][2]);
               if (rq != null) {
                  p.sub(o[0], o[1], o[2]);
                  rq.transform(p);
                  p.add(o[0], o[1], o[2]);
               }

               q.x[i] = p.x / 16.0F;
               q.y[i] = p.y / 16.0F;
               q.z[i] = p.z / 16.0F;
            }

            q.u[0] = uv[0] / texW;
            q.v[0] = uv[1] / texH;
            q.u[1] = uv[0] / texW;
            q.v[1] = uv[3] / texH;
            q.u[2] = uv[2] / texW;
            q.v[2] = uv[3] / texH;
            q.u[3] = uv[2] / texW;
            q.v[3] = uv[1] / texH;
            Vector3f n = new Vector3f(nrm[0], nrm[1], nrm[2]);
            if (rq != null) {
               rq.transform(n);
            }

            q.nx = n.x;
            q.ny = n.y;
            q.nz = n.z;
            (pilot ? bone.pilotQuads : bone.quads).add(q);
         }
      }
   }

   private static float[][] corners(String face, float[] f, float[] t) {
      float x0 = f[0];
      float y0 = f[1];
      float z0 = f[2];
      float x1 = t[0];
      float y1 = t[1];
      float z1 = t[2];
      switch (face) {
         case "d":
            return new float[][]{{x0, y0, z1}, {x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}};
         case "u":
            return new float[][]{{x0, y1, z0}, {x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}};
         case "n":
            return new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
         case "s":
            return new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
         case "w":
            return new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
         default:
            return new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
      }
   }

   private static float[] normal(String face) {
      switch (face) {
         case "d":
            return new float[]{0.0F, -1.0F, 0.0F};
         case "u":
            return new float[]{0.0F, 1.0F, 0.0F};
         case "n":
            return new float[]{0.0F, 0.0F, -1.0F};
         case "s":
            return new float[]{0.0F, 0.0F, 1.0F};
         case "w":
            return new float[]{-1.0F, 0.0F, 0.0F};
         default:
            return new float[]{1.0F, 0.0F, 0.0F};
      }
   }

   public void applyClip(BbModel.Clip clip, float time, BbModel.Pose pose) {
      if (clip != null) {
         float t;
         if (clip.loop && clip.length > 0.0F) {
            t = time % clip.length;
         } else {
            t = Math.min(time, clip.length);
         }

         for (Entry<String, BbModel.Track[]> e : clip.bones.entrySet()) {
            int idx = this.indexOf(e.getKey());
            if (idx >= 0) {
               BbModel.Track[] tr = e.getValue();
               if (tr[0] != null) {
                  sample(tr[0], t, pose.rot[idx]);
               }

               if (tr[1] != null) {
                  sample(tr[1], t, pose.pos[idx]);
               }

               if (tr[2] != null) {
                  sample(tr[2], t, pose.scl[idx]);
               }
            }
         }
      }
   }

   private static void sample(BbModel.Track tr, float t, float[] out) {
      int n = tr.t.length;
      if (n != 1 && !(t <= tr.t[0])) {
         if (t >= tr.t[n - 1]) {
            System.arraycopy(tr.v[n - 1], 0, out, 0, 3);
         } else {
            int i = 0;

            while (i < n - 2 && t >= tr.t[i + 1]) {
               i++;
            }

            float a = (t - tr.t[i]) / (tr.t[i + 1] - tr.t[i]);
            boolean smooth = tr.smooth[i] || tr.smooth[i + 1];

            for (int k = 0; k < 3; k++) {
               float p1 = tr.v[i][k];
               float p2 = tr.v[i + 1][k];
               if (!smooth) {
                  out[k] = p1 + (p2 - p1) * a;
               } else {
                  float p0 = i > 0 ? tr.v[i - 1][k] : p1;
                  float p3 = i + 2 < n ? tr.v[i + 2][k] : p2;
                  out[k] = 0.5F
                     * (2.0F * p1 + (-p0 + p2) * a + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * a * a + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * a * a * a);
               }
            }
         }
      } else {
         System.arraycopy(tr.v[0], 0, out, 0, 3);
      }
   }

   public void render(PoseStack ps, VertexConsumer vc, int light, int overlay, BbModel.Pose pose, boolean showDummyPilot) {
      for (int i = 0; i < this.bones.size(); i++) {
         if (this.bones.get(i).parent < 0) {
            this.renderBone(i, ps, vc, light, overlay, pose, showDummyPilot);
         }
      }
   }

   private void renderBone(int i, PoseStack ps, VertexConsumer vc, int light, int overlay, BbModel.Pose pose, boolean showDummyPilot) {
      float[] s = pose.scl[i];
      if (s[0] != 0.0F || s[1] != 0.0F || s[2] != 0.0F) {
         BbModel.Bone b = this.bones.get(i);
         float px = b.px / 16.0F;
         float py = b.py / 16.0F;
         float pz = b.pz / 16.0F;
         ps.pushPose();
         ps.translate(px + pose.pos[i][0] / 16.0F, py + pose.pos[i][1] / 16.0F, pz + pose.pos[i][2] / 16.0F);
         float rx = b.rx + pose.rot[i][0];
         float ry = b.ry + pose.rot[i][1];
         float rz = b.rz + pose.rot[i][2];
         if (rx != 0.0F || ry != 0.0F || rz != 0.0F) {
            ps.mulPose(new Quaternionf().rotationZYX((float)Math.toRadians(rz), (float)Math.toRadians(ry), (float)Math.toRadians(rx)));
         }

         if (s[0] != 1.0F || s[1] != 1.0F || s[2] != 1.0F) {
            ps.scale(s[0], s[1], s[2]);
         }

         ps.translate(-px, -py, -pz);
         int l = b.name.equals("fuego") ? 15728880 : light;
         com.mojang.blaze3d.vertex.PoseStack.Pose last = ps.last();

         for (BbModel.Quad q : b.quads) {
            emit(q, last, vc, l, overlay);
         }

         if (showDummyPilot) {
            for (BbModel.Quad q : b.pilotQuads) {
               emit(q, last, vc, l, overlay);
            }
         }

         for (int child : b.children) {
            this.renderBone(child, ps, vc, l, overlay, pose, showDummyPilot);
         }

         ps.popPose();
      }
   }

   private static void emit(BbModel.Quad q, com.mojang.blaze3d.vertex.PoseStack.Pose pose, VertexConsumer vc, int light, int overlay) {
      for (int k = 0; k < 4; k++) {
         vc.addVertex(pose, q.x[k], q.y[k], q.z[k])
            .setColor(255, 255, 255, 255)
            .setUv(q.u[k], q.v[k])
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, q.nx, q.ny, q.nz);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Bone {
      String name;
      int parent;
      float px;
      float py;
      float pz;
      float rx;
      float ry;
      float rz;
      final List<BbModel.Quad> quads = new ArrayList<>();
      final List<BbModel.Quad> pilotQuads = new ArrayList<>();
      final List<Integer> children = new ArrayList<>();
   }

   @Environment(EnvType.CLIENT)
   public static class Clip {
      float length;
      boolean loop;
      final Map<String, BbModel.Track[]> bones = new HashMap<>();

      public float length() {
         return this.length;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Pose {
      public final float[][] rot;
      public final float[][] pos;
      public final float[][] scl;

      public Pose(int bones) {
         this.rot = new float[bones][3];
         this.pos = new float[bones][3];
         this.scl = new float[bones][3];
         this.reset();
      }

      public void reset() {
         for (int i = 0; i < this.rot.length; i++) {
            this.rot[i][0] = this.rot[i][1] = this.rot[i][2] = 0.0F;
            this.pos[i][0] = this.pos[i][1] = this.pos[i][2] = 0.0F;
            this.scl[i][0] = this.scl[i][1] = this.scl[i][2] = 1.0F;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Quad {
      final float[] x = new float[4];
      final float[] y = new float[4];
      final float[] z = new float[4];
      final float[] u = new float[4];
      final float[] v = new float[4];
      float nx;
      float ny;
      float nz;
   }

   @Environment(EnvType.CLIENT)
   private static class Track {
      float[] t;
      float[][] v;
      boolean[] smooth;
   }
}
