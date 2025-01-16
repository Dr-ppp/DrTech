package com.drppp.drtech.Client.Particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class ParticleFlame extends net.minecraft.client.particle.ParticleFlame {

    protected float hugeFlameScale;

    public ParticleFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.particleScale *= 3.F;
        this.hugeFlameScale = particleScale;
    }

    @Override
    public void renderParticle(@NotNull BufferBuilder buffer, @NotNull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // Ensure particleMaxAge is not zero to avoid division by zero
        if (this.particleMaxAge == 0) {
            return;
        }

        float f0 = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
        this.particleScale = this.hugeFlameScale * (1.0F - f0 * f0 * 0.5F);

        final float TEXTURE_SIZE = 16.0F;
        final float UV_INCREMENT = 0.0624375F;
        final float PARTICLE_SCALE_MULTIPLIER = 0.1F;

        float uMin = (float) this.particleTextureIndexX / TEXTURE_SIZE;
        float uMax = uMin + UV_INCREMENT;
        float vMin = (float) this.particleTextureIndexY / TEXTURE_SIZE;
        float vMax = vMin + UV_INCREMENT;
        float scale = PARTICLE_SCALE_MULTIPLIER * this.particleScale;

        if (this.particleTexture != null) {
            uMin = this.particleTexture.getMinU();
            uMax = this.particleTexture.getMaxU();
            vMin = this.particleTexture.getMinV();
            vMax = this.particleTexture.getMaxV();
        }

        float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightmapX = brightness >> 16 & 65535;
        int lightmapY = brightness & 65535;

        Vec3d[] vertices = new Vec3d[4];
        vertices[0] = new Vec3d(-rotationX * scale - rotationXY * scale, -rotationZ * scale, -rotationYZ * scale - rotationXZ * scale);
        vertices[1] = new Vec3d(-rotationX * scale + rotationXY * scale, rotationZ * scale, -rotationYZ * scale + rotationXZ * scale);
        vertices[2] = new Vec3d(rotationX * scale + rotationXY * scale, rotationZ * scale, rotationYZ * scale + rotationXZ * scale);
        vertices[3] = new Vec3d(rotationX * scale - rotationXY * scale, -rotationZ * scale, rotationYZ * scale - rotationXZ * scale);

        if (this.particleAngle != 0.0F) {
            float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float cosHalfAngle = MathHelper.cos(angle * 0.5F);
            float sinHalfAngleX = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.x;
            float sinHalfAngleY = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.y;
            float sinHalfAngleZ = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.z;
            Vec3d rotationAxis = new Vec3d(sinHalfAngleX, sinHalfAngleY, sinHalfAngleZ);

            for (int i = 0; i < 4; ++i) {
                Vec3d vertex = vertices[i];
                double dotProduct = rotationAxis.dotProduct(vertex);
                double scaleFactor = 2.0D * dotProduct;
                double cosSquared = cosHalfAngle * cosHalfAngle;
                double sinSquared = rotationAxis.dotProduct(rotationAxis);
                vertices[i] = rotationAxis.scale(scaleFactor).add(vertex.scale(cosSquared - sinSquared)).add(rotationAxis.crossProduct(vertex).scale(2.0F * cosHalfAngle));
            }
        }

        buffer.pos((double) posX + vertices[0].x, (double) posY + vertices[0].y, (double) posZ + vertices[0].z).tex(uMax, vMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos((double) posX + vertices[1].x, (double) posY + vertices[1].y, (double) posZ + vertices[1].z).tex(uMax, vMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos((double) posX + vertices[2].x, (double) posY + vertices[2].y, (double) posZ + vertices[2].z).tex(uMin, vMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos((double) posX + vertices[3].x, (double) posY + vertices[3].y, (double) posZ + vertices[3].z).tex(uMin, vMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
    }


    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {

        public Particle createParticle(int particleID, @NotNull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int @NotNull ... parameters) {
            return new ParticleFlame(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }

    }
}