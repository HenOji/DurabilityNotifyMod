package com.henoji;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/**
 * 音をプレイヤーの位置に鳴らすクラス.
 * ピッチの変更が可能.
 *
 * @author HenOji
 */

public class SetSound extends PositionedSound
{
	private static final ResourceLocation NOTIFY_SOUND_PATH = new ResourceLocation("example", "notify01");

	SetSound (ResourceLocation loc, float pitchIn, float xIn, float yIn, float zIn)
	{
		super(loc, SoundCategory.MASTER);
		this.volume = 1.0F;
		this.pitch  = pitchIn;
		this.xPosF  = xIn;
		this.yPosF  = yIn;
		this.zPosF  = zIn;
		this.repeat = false;
		this.repeatDelay = 0;
		this.attenuationType = AttenuationType.NONE;

	}
	/* カスタムサウンドをプレイヤーの位置で再生 */
	public static SetSound setNotifySound (float pitchIn)
	{
		return new SetSound(NOTIFY_SOUND_PATH, pitchIn,
							(float)Minecraft.getMinecraft().player.posX,
							(float)Minecraft.getMinecraft().player.posY,
							(float)Minecraft.getMinecraft().player.posZ);
	}
}
