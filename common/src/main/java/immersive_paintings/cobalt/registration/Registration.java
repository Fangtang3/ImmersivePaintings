package immersive_paintings.cobalt.registration;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class Registration {
    private static Impl INSTANCE;

    public static <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj) {
        return INSTANCE.register(registry, id, obj);
    }

    public static <T extends Entity> void registerEntityRenderer(EntityType<?> type, EntityRendererFactory<T> constructor) {
        //noinspection unchecked
        INSTANCE.registerEntityRenderer((EntityType<T>) type, constructor);
    }

    public static abstract class Impl {
        protected Impl() {
            INSTANCE = this;
        }

        public abstract <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj);

        public abstract <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererFactory<T> constructor);
    }
}
