G=$(ls geckolib-*.jar); mkdir -p /tmp/g && cd /tmp/g && unzip -q -o $OLDPWD/$G
for c in $(grep -rl 'SpecialModelRenderers\|ID_MAPPER\|special' --include=*.class com | head -10); do echo "== $c"; strings -n 3 $c | grep -E '^[a-z_:/]+$' | head -12 | tr '\n' ' '; echo; done
