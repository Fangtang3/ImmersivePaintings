package immersive_paintings.client.render.entity.renderer;

import immersive_paintings.Config;
import immersive_paintings.Main;
import immersive_paintings.entity.ImmersivePaintingEntity;
import immersive_paintings.resources.ClientPaintingManager;
import immersive_paintings.resources.ObjectLoader;
import immersive_paintings.resources.Painting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import owens.oobjloader.Face;
import owens.oobjloader.FaceVertex;

import java.util.List;

public class ImmersivePaintingEntityRenderer<T extends ImmersivePaintingEntity> extends EntityRenderer<T, EntityRenderState> {
    public ImmersivePaintingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public EntityRenderState createRenderState() {
        return null;
    }

    @Override
    public void render(EntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        //matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        //matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-entity.getPitch(tickDelta)));
        matrixStack.scale(0.0625f, 0.0625f, 0.0625f);
        renderPainting(matrixStack, vertexConsumerProvider, state);
        matrixStack.pop();
        super.render(state, matrixStack, vertexConsumerProvider, light);
    }

    public Identifier getTexture(EntityRenderState paintingEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        Config config = Config.getInstance();

        ClientPlayerEntity player = client.player;
        double distance = (player == null ? 0 : player.getPos().distanceTo(paintingEntity.nameLabelPos));
        double blocksVisible = Math.tan(client.options.getFov().getValue() / 180.0 * Math.PI / 2.0) * 2.0 * distance;
        int resolution = ClientPaintingManager.getPainting(paintingEntity).resolution;
        double pixelDensity = blocksVisible * resolution / client.getWindow().getHeight();

        Painting.Type type = pixelDensity > config.eighthResolutionThreshold ? Painting.Type.EIGHTH
                : pixelDensity > config.quarterResolutionThreshold ? Painting.Type.QUARTER
                : pixelDensity > config.halfResolutionThreshold ? Painting.Type.HALF
                : Painting.Type.FULL;

        return ClientPaintingManager.getPaintingTexture(paintingEntity, type).textureIdentifier;
    }

    protected int getLight(int light) {
        return light;
    }

    protected int getFrameLight(int light) {
        return light;
    }

    private void renderPainting(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, EntityRenderState entity) {
        int light = WorldRenderer.getLightmapCoordinates((BlockRenderView) ServerWorld.OVERWORLD, new BlockPos((int) entity.x, (int) entity.y, (int) entity.z));

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f posMat = entry.getPositionMatrix();
        Matrix3f normMat = entry.getNormalMatrix();

        VertexConsumer vertexConsumer;


        float width = entity.width;
        float height = entity.height;

        //canvas
        vertexConsumer = vertexConsumerProvider.getBuffer(isTranslucent() ? RenderLayer.getEntityTranslucent(getTexture(entity)) : RenderLayer.getEntitySolid(getTexture(entity)));
        renderFaces(isTranslucent() ? "objects/graffiti.obj" : "objects/canvas.obj", posMat, normMat, vertexConsumer, getLight(light), width, height);
    }

    protected boolean isTranslucent() {
        return false;
    }

    private void renderFaces(String name, Matrix4f posMat, Matrix3f normMat, VertexConsumer vertexConsumer, int light, float width, float height) {
        List<Face> faces = ObjectLoader.objects.get(Main.locate(name));
        for (Face face : faces) {
            for (FaceVertex v : face.vertices) {
                vertex(posMat,
                        normMat,
                        vertexConsumer,
                        v.v.x * (width - (float) 0.0 * 2),
                        v.v.y * (height - (float) 0.0 * 2),
                        v.v.z * 16.0f,
                        v.t.u * (width - (float) 0.0 * 2) / width + (float) 0.0 / width,
                        (1.0f - v.t.v) * (height - (float) 0.0 * 2) / height + (float) 0.0 / height,
                        v.n.x,
                        v.n.y,
                        v.n.z,
                        light);
            }
        }
    }

    private List<Face> getFaces(Identifier frame, String part) {
        Identifier id = Identifier.of(frame.getNamespace(), frame.getPath() + "/" + part + ".obj");
        if (ObjectLoader.objects.containsKey(id)) {
            return ObjectLoader.objects.get(id);
        } else {
            return List.of();
        }
    }

    private void renderFrame(Identifier frame, Matrix4f posMat, Matrix3f normMat, VertexConsumer vertexConsumer, int light, float width, float height) {
        List<Face> faces = getFaces(frame, "bottom");
        for (int x = 0; x < width / 16; x++) {
            float u = width == 16 ? 0.75f : (x == 0 ? 0.0f : x == width / 16 - 1 ? 0.5f : 0.25f);
            for (Face face : faces) {
                for (FaceVertex v : face.vertices) {
                    vertex(posMat, normMat, vertexConsumer, v.v.x + x * 16 - (width - 16) / 2, v.v.y - (height - 16) / 2, v.v.z, v.t.u * 0.25f + u, (1.0f - v.t.v), v.n.x, v.n.y, v.n.z, light);
                }
            }
        }
        faces = getFaces(frame, "top");
        for (int x = 0; x < width / 16; x++) {
            float u = width == 16 ? 0.75f : (x == 0 ? 0.0f : x == width / 16 - 1 ? 0.5f : 0.25f);
            for (Face face : faces) {
                for (FaceVertex v : face.vertices) {
                    vertex(posMat, normMat, vertexConsumer, v.v.x + x * 16 - (width - 16) / 2, v.v.y + (height - 16) / 2, v.v.z, v.t.u * 0.25f + u, (1.0f - v.t.v), v.n.x, v.n.y, v.n.z, light);
                }
            }
        }
        faces = getFaces(frame, "right");
        for (int y = 0; y < height / 16; y++) {
            float u = 0.25f;
            for (Face face : faces) {
                for (FaceVertex v : face.vertices) {
                    vertex(posMat, normMat, vertexConsumer, v.v.x + (width - 16) / 2, v.v.y + y * 16 - (height - 16) / 2, v.v.z, v.t.u * 0.25f + u, (1.0f - v.t.v), v.n.x, v.n.y, v.n.z, light);
                }
            }
        }
        faces = getFaces(frame, "left");
        for (int y = 0; y < height / 16; y++) {
            float u = 0.25f;
            for (Face face : faces) {
                for (FaceVertex v : face.vertices) {
                    vertex(posMat, normMat, vertexConsumer, v.v.x - (width - 16) / 2, v.v.y + y * 16 - (height - 16) / 2, v.v.z, v.t.u * 0.25f + u, (1.0f - v.t.v), v.n.x, v.n.y, v.n.z, light);
                }
            }
        }
    }

    private void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z - 0.5f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalX, normalY, normalZ);
    }
}