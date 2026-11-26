package ar.hombrosafio.ruleta.client.mixin;

import ar.hombrosafio.ruleta.client.RouletteChatGlint;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = {
	"net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess",
	"net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess"
})
public abstract class RouletteChatMixin {
	@ModifyVariable(method = "handleMessage", at = @At("HEAD"), argsOnly = true)
	private FormattedCharSequence ruleta$shine(FormattedCharSequence text) {
		return RouletteChatGlint.wrap(text);
	}
}
