G=$(ls geckolib-*.jar); mkdir -p /tmp/g && cd /tmp/g && unzip -q -o $OLDPWD/$G
grep -rao 'geckolib/[a-z_/]*' --include=*.class . | sort -u | head -40
javap -p -c -cp . com.geckolib.model.DefaultedGeoModel com.geckolib.model.DefaultedEntityGeoModel | grep -E 'ldc|String|Method' | head -60
