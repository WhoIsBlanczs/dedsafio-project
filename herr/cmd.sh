J=$(ls *.jar | grep -v -E 'lwjgl|natives|msal4j|azure|jna|text2speech|icu4j|oshi|jtracy|-sources|netty-(transport|codec|handler|resolver)')
java ../../herr/Strip.java /tmp/stubs.jar $J 2>&1 | tail -3
ls -la /tmp/stubs.jar
base64 -w 2000 /tmp/stubs.jar
