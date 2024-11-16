package immersive_paintings.client.gui.widget

import immersive_paintings.util.FlowingText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.text.Text
import java.util.function.Consumer

class CallbackCheckboxWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Text,
    private val tooltip: Text?,
    private var checked: Boolean,
    showMessage: Boolean,
    private val onChecked: Consumer<Boolean>
): PressableWidget(x, y, width, height, message) {
    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
        }

        if (isHovered && tooltip != null) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, FlowingText.wrap(tooltip, 160), mouseX, mouseY)
        }
    }

    override fun onPress() {
        checked = !checked
        onChecked.accept(checked)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        builder?.let { i ->
            i.put(NarrationPart.TITLE, this.narrationMessage);
            if (this.active) {
                if (this.isFocused) {
                    i.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.focused"))
                } else {
                    i.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.hovered"))
                }
            }
        }
    }
}
