G=$(ls geckolib-*.jar); mkdir -p /tmp/g && cd /tmp/g && unzip -q -o $OLDPWD/$G
grep -raoh -E '(geckolib/(models|animations)|geo/|animations/|\.geo\.json|\.animation\.json|textures/)[a-z_/.]*' --include=*.class . | sort -u | head -40
grep -ral 'geckolib/models' --include=*.class . | head
