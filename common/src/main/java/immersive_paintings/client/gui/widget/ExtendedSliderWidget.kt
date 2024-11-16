package immersive_paintings.client.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

abstract class ExtendedSliderWidget<T> (
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: Text,
    value: Double,
    private val onApplyValue: Consumer<T>
): SliderWidget(x, y, width, height, text, value) {
    private var oldValue: T? = null
    protected open fun getOpticalValue(): Double {
        return value
    }

    abstract fun getValue(): T

    override fun renderWidget(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        lateinit var texture: Identifier
        try {
            val method = this.javaClass.getDeclaredMethod("getTexture")
            method.isAccessible = true
            texture = method.invoke(this) as Identifier
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val i: Int = (if (this.isHovered) 2 else 1) * 20
        context?.drawTexture1(texture, this.x + (getOpticalValue() * (this.width - 8).toDouble()).toInt(), this.y, 0, 46 + i, 4, 20)
        context?.drawTexture1(texture, this.x + (getOpticalValue() * (this.width - 8).toDouble()).toInt() + 4, this.y, 196, 46 + i, 4, 20)

        super.renderWidget(context, mouseX, mouseY, delta)
    }

    override fun applyValue() {
        val v: T = getValue()
        if (v != oldValue) {
            oldValue = v
            onApplyValue.accept(v)
        }
    }
}