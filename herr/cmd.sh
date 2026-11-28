J=$(ls *.jar | tr "\n" ":")
javap -p -cp $J net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer | grep -vE 'lambda'
for c in net.minecraft.client.renderer.GameRenderer net.minecraft.client.renderer.LightmapRenderStateExtractor; do echo "-- $c"; javap -p -cp $J $c | grep -iE 'nightvision|skyDarken|getSkyDarken|extract'; done
javap -cp $J net.minecraft.world.level.BlockAndTintGetter net.minecraft.world.level.BlockAndLightGetter 2>&1 | grep -iE 'interface|Brightness|getRawBrightness'
javap -p -cp $J net.minecraft.client.multiplayer.ClientLevel | grep -iE 'cloud|skyDarken'
javap -p -cp $J net.minecraft.client.multiplayer.ClientPacketListener | grep -E 'ClientLevel level|handleEntityEvent'
javap -p -cp $J net.minecraft.client.renderer.state.LightmapRenderState | grep -v static
javap -c -p -cp $J net.minecraft.client.renderer.LightmapRenderStateExtractor | grep -E 'invoke' | grep -iE 'Level|Sky|Dark' | head -10
