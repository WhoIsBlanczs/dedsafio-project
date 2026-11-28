cd ../..
mkdir -p run/mods && cd run && echo "eula=true" > eula.txt && printf 'online-mode=false\nlevel-type=minecraft\\:flat\nspawn-protection=0\n' > server.properties
for u in $(cat ../herr/jars.txt); do curl -sL -o mods/$(basename $u) $u; done; ls -la mods
cd ..
( sleep 1; while ! grep -q 'Done (' run/logs/latest.log 2>/dev/null; do sleep 3; if ! pgrep -f GradleWorker >/dev/null && [ -f /tmp/fin ]; then break; fi; done; sleep 20; cat herr/comandos.txt 2>/dev/null; echo stop ) | timeout 900 ./gradlew runServer -q --console=plain 2>&1 | grep -vE '^\s*at |Picked up' | tail -150
