package immersive_paintings.client

import immersive_paintings.resources.ByteImage
import net.minecraft.client.texture.NativeImage

object ClientUtils {
    @JvmStatic
    fun byteImageToNativeImage(image: ByteImage): NativeImage {
        val nativeImage = NativeImage(image.width, image.height, false)
        for (x in 0..<image.width) {
            for (y in 0..<image.height) {
                nativeImage::class.java
                    .getDeclaredMethod("setColor", Int::class.java, Int::class.java, Int::class.java)
                    .let {
                        it.isAccessible = true
                        it.invoke(nativeImage, x, y, image.getABGR(x, y))
                    }
            }
        }
        return nativeImage
    }
}
