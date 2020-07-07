package com.prohitman.crittersaroundtheworldmod.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class ModRenderTypes extends RenderType {

	public ModRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn,
			boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getBumLayer(ResourceLocation locationIn) {
		RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(locationIn, false, false);
		return makeType("eyes", DefaultVertexFormats.ENTITY, 7, 256, false, true,
				RenderType.State.getBuilder().texture(renderstate$texturestate).transparency(ADDITIVE_TRANSPARENCY)
						 .alpha(DEFAULT_ALPHA).writeMask(COLOR_WRITE).fog(BLACK_FOG).build(false));
	}
}
