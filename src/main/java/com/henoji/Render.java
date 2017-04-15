package com.henoji;

import static com.mumfrey.liteloader.gl.GL.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

/**
 * 画面に描画するクラス.
 * 文字描画, テクスチャ描画, エンチャント描画をする.
 *
 * @author HenOji
 */

public class Render{

	private Minecraft mc = Minecraft.getMinecraft();
	private FontRenderer fontRenderer = mc.fontRendererObj;

	private final int BOTTOM_LEFT  = 0,
					  HOTBAR_LEFT  = 1,
					  HOTBAR_RIGHT = 2,
					  BOTTOM_RIGHT = 3,
					  MIDDLE_RIGHT = 4,
					  TOP_RIGHT    = 5,
					  TOP_CENTER   = 6,
					  TOP_LEFT     = 7,
					  MIDDLE_LEFT  = 8;

	private int dispW,
				dispH,
				fontW,
				fontH,
				enchantX,
				enchantY;

	private List<String> enchantList = new ArrayList<String>();

	/* Constractor */
	public Render(){}

	/* マイクラ解像度取得 */
	private void setScaledResolution()
	{
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		this.dispW = scaledResolution.getScaledWidth();
		this.dispH = scaledResolution.getScaledHeight();
	}

	/* 耐久値描画 */
	public void renderSelectedString(String text, int color, int handKey)
	{
		setScaledResolution();
		int posX = 0, posY = 0, fontW = fontRenderer.getStringWidth(text);
		if(handKey == 0) // メインハンド
		{
			posX = (dispW - fontW) / 2;
			posY = dispH - 48;
		}
		else // オフハンド
		{
			if(mc.gameSettings.mainHand == EnumHandSide.RIGHT) // メインハンドが右
			{
				posX = dispW / 2 -97 -12 - fontW / 2;
			}
			else // メインハンドが左
			{
				posX = dispW / 2 +97 +12 - fontW / 2;
			}
			posY = dispH - 32;
		}

		glPushMatrix();
		glEnableBlend();
		glBlendFuncSeparate(770, 771, 1, 0);
		fontRenderer.drawString(text, posX +1, posY, 0);
		fontRenderer.drawString(text, posX -1, posY, 0);
		fontRenderer.drawString(text, posX, posY +1, 0);
		fontRenderer.drawString(text, posX, posY -1, 0);
		fontRenderer.drawString(text, posX, posY, color); // 耐久値
		glDisableBlend();
		glPopMatrix();
	}

	/* エンチャントリスト・表示位置 X, Yセット */
	public void setDisplayEnchant(ItemStack currentItem, int displayEnchantPreset, boolean isChanged)
	{
		if(isChanged)
		{
			/* エンチャントリスト初期化・セット */
			int i = 0;
			this.enchantList.clear();
			this.fontW = 0;

			for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(currentItem).entrySet())
			{
				this.enchantList.add(entry.getKey().getTranslatedName(entry.getValue()));
				this.fontW = Integer.max(fontW, fontRenderer.getStringWidth(enchantList.get(i)));
				i++;
			}
			this.fontH = fontRenderer.FONT_HEIGHT *i;
		}

		/* 表示位置 X, Yセット */
		setScaledResolution();
		switch (displayEnchantPreset)
		{
		case BOTTOM_LEFT:
			this.enchantX = 2;
			this.enchantY = this.dispH -1 -Integer.max(fontH, fontRenderer.FONT_HEIGHT *4);
			break;

		case HOTBAR_LEFT:
			this.enchantX = this.dispW / 2 - 91 -31 -fontW;
			this.enchantY = this.dispH -1 -Integer.max(fontH, fontRenderer.FONT_HEIGHT *4);

			if((mc.gameSettings.mainHand == EnumHandSide.LEFT || mc.player.inventory.offHandInventory.get(0) == ItemStack.EMPTY) && mc.gameSettings.attackIndicator != 2)
			{
				this.enchantX += 26;
			}
			break;

		case HOTBAR_RIGHT:
			this.enchantX = this.dispW / 2 + 91 +31;
			this.enchantY = this.dispH -1 -Integer.max(fontH, fontRenderer.FONT_HEIGHT *4);

			if((mc.gameSettings.mainHand == EnumHandSide.RIGHT || mc.player.inventory.offHandInventory.get(0) == ItemStack.EMPTY) && mc.gameSettings.attackIndicator != 2)
			{
				this.enchantX -= 26;
			}
			break;

		case BOTTOM_RIGHT:
			this.enchantX = this.dispW -2 -fontW;
			this.enchantY = this.dispH -1 -Integer.max(fontH, fontRenderer.FONT_HEIGHT *4);
			break;

		case MIDDLE_RIGHT:
			this.enchantX = this.dispW -2 -fontW;
			this.enchantY = (this.dispH -fontH) /2;
			break;

		case TOP_RIGHT:
			this.enchantX = this.dispW -2 -fontW;
			this.enchantY = 2;
			break;

		case TOP_CENTER:
			this.enchantX = (this.dispW -fontW) /2;
			this.enchantY = 2;
			break;

		case TOP_LEFT:
			this.enchantX = 2;
			this.enchantY = 2;
			break;

		case MIDDLE_LEFT:
			this.enchantX = 2;
			this.enchantY = (dispH -fontH) /2;
			break;
		}
	}

	/* エンチャント描画 */
	public void renderItemEnchant(int alpha)
	{
		Gui.drawRect(this.enchantX -1, this.enchantY -1, this.enchantX +this.fontW +1, this.enchantY +this.fontH, 0x00202020 + (Integer.min(192, alpha) << 24));
		glPushMatrix();
		glEnableBlend();
		glBlendFuncSeparate(770, 771, 1, 0);
		for(int i = 0; i < enchantList.size(); i++)
		{
			fontRenderer.drawStringWithShadow(enchantList.get(i), this.enchantX, this.enchantY +i *fontRenderer.FONT_HEIGHT, 0x00ffffff + (alpha << 24));
		}
		glDisableBlend();
		glPopMatrix();
	}
}
