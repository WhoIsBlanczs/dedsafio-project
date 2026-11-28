J=$(ls *.jar | tr "\n" ":")
for c in net.minecraft.client.renderer.GameRenderer net.minecraft.client.renderer.ScreenEffectRenderer net.minecraft.client.Minecraft; do javap -p -cp $J $c | grep -iE 'activation'; done
javap -cp $J net.minecraft.client.renderer.texture.DynamicTexture | grep 'DynamicTexture('
javap -cp $J net.minecraft.client.gui.components.ChatComponent | grep -E 'addMessage|addClientSystemMessage'
javap -cp $J net.minecraft.client.gui.Gui | grep -iE 'chat'
javap -cp $J net.minecraft.client.renderer.entity.player.AvatarRenderer | grep -iE 'renderHand|Arm'
javap -cp $J net.minecraft.client.renderer.ItemInHandRenderer 2>&1 | head -3
javap -p -cp $J net.minecraft.client.renderer.item.ItemInHandRenderer 2>&1 | grep -iE 'renderArmWith|renderItem|renderPlayerArm|applyItemArm|class' | head
javap -cp $J net.minecraft.client.renderer.LightmapRenderStateExtractor 2>&1 | head -12
javap -p -cp $J net.minecraft.client.renderer.Lightmap 2>&1 | head -20
