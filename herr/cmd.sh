G=$(ls geckolib-*.jar); mkdir -p /tmp/g && cd /tmp/g && unzip -q -o $OLDPWD/$G
grep -rl 'SpecialModelRenderer' --include=*.class . | head
for c in $(grep -rl 'SpecialModelRenderer' --include=*.class . | head -6); do echo "== $c"; strings -n 4 $c | grep -iE 'geckolib|^[a-z_]+$' | head -15 | tr '\n' ' '; echo; done
find . -name '*.json' -path '*items*' | head; find . -path '*assets*' -name '*.json' | head
