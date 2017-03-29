package com.henoji;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

/**
 * メインクラス.
 * 手持ちのツールの耐久が減っている時に 音とアイコンで知らせる.
 *
 * @author HenOji
 */

public class DurabilityNotify
{
	private ItemStack    prevItem = ItemStack.EMPTY;
	private Minecraft    mc;
	private Render       render;

	private boolean isNotifyOnlyEnchant,
					isDisplayEnchant,
					isChanged;

	private float   enchantDisplaySecs,
					displayTicks,
					partialTicks;

	private int     displayEnchantPreset,
					prevHotbar = -1,
					notifyArrayKey;

	private final int[] NOTIFY_ARRAY = {50, 25, 10, 3, 2, 1, 0, -1}; // 通知耐久値 降順

	/* Constructor */
	public DurabilityNotify(Minecraft mc)
	{
		this.mc = mc;
		this.render = new Render(mc);
	}

	public int getCurrentHotbar()
	{
		return mc.player.inventory.currentItem;
	}

	public ItemStack getCurrentItem()
	{
		return mc.player.inventory.getCurrentItem();
	}

	public int getItemDurability()
	{
		return (getCurrentItem().getMaxDamage()) - (getCurrentItem().getItemDamage());
	}

	/* 設定のエンチャントのみに適用するか */
	public boolean isNotifyOnlyEnchant()
	{
		return this.isNotifyOnlyEnchant;
	}
	public void setNotifyOnlyEnchant(boolean toggle)
	{
		this.isNotifyOnlyEnchant = toggle;
	}

	/* 設定のエンチャントの表示 */
	public boolean isDisplayEnchant()
	{
		return this.isDisplayEnchant;
	}
	public void setDisplayEnchant(boolean toggle)
	{
		this.isDisplayEnchant = toggle;
		if(!toggle) displayTicks = 0;
	}

	/* 設定のエンチャント表示位置プリセット */
	public int getDisplayEnchantPreset()
	{
		return this.displayEnchantPreset;
	}
	public void setDisplayEnchantPreset(int preset)
	{
		this.displayEnchantPreset = preset;
		this.isChanged = true;
	}

	/* 設定のエンチャント表示時間 */
	public float getEnchantDisplaySecs()
	{
		return this.enchantDisplaySecs;
	}
	public void setEnchantDisplaySecs(float secs)
	{
		this.enchantDisplaySecs = secs;
	}

	/* setエンチャント表示時間 */
	public void setDisplayTicks(float partialTicks)
	{
		if(getCurrentItem().isItemEnchanted())
		{
			this.displayTicks = this.enchantDisplaySecs * 20.0F;
			this.partialTicks = partialTicks;
		}
		else
		{
			this.displayTicks = 0;
		}
	}

	public void startNotify(float partialTicks)
	{
		/* 1 AND (2 OR 3) AND 4 */
		if(getCurrentItem().isItemStackDamageable())             // 1: ツールか
		{
			if(prevHotbar != getCurrentHotbar()                  // 2: 前Tickとホットバーが違うか
			|| prevItem.getItem() != getCurrentItem().getItem()) // 3: 前Tickとアイテムが違うか
			{
				notifyArrayKey = 0;
				if(getItemDurability() <= NOTIFY_ARRAY[notifyArrayKey]) // 4: 通知耐久最大値以下か
				{
					for (notifyArrayKey = 1; notifyArrayKey < NOTIFY_ARRAY.length; notifyArrayKey++)
					{
						// アイテムの耐久値が通知する値より大きい
						if(getItemDurability() > NOTIFY_ARRAY[notifyArrayKey]) break;
					}
					notifySound(); // 通知音
				}
				if(isDisplayEnchant)setDisplayTicks(partialTicks);
				isChanged = true;
			}
			else // ホットバーとアイテムが同じ
			{
				if(getItemDurability() <= NOTIFY_ARRAY[notifyArrayKey]) // 耐久値が通知耐久値以下
				{
					notifySound(); // 通知音
					notifyArrayKey++;
				}
			}
			// エンチャント表示
			if((int)displayTicks > 0)
			{
				displayEnchant(partialTicks);
			}

			/* 耐久値表示 */
			int fontColor;
			if(notifyArrayKey > 2) // 耐久値 色
			{
				fontColor = 0xffff4040; // 赤色
			}
			else if(notifyArrayKey > 0)
			{
				fontColor = 0xffffff20; // 黄色
			}
			else
			{
				fontColor = 0xff80ff20; // 緑色
			}
			render.renderSelectedString(Integer.toString(getItemDurability()), fontColor);
		}
		else // 持っているアイテムがツール以外
		{
			displayTicks = 0;
		}
		/* 変数に代入 */
		prevItem   = getCurrentItem();
		prevHotbar = getCurrentHotbar();
	}

	/* 通知音メソッド */
	private void notifySound()
	{
		if(!isNotifyOnlyEnchant                 // 1: "エンチャントのみ通知" にチェックが付いているか
		||  getCurrentItem().isItemEnchanted()) // 2: アイテムにエンチャントが付いているか
		{
			float pitch = ( 0.85F + notifyArrayKey * 0.15F );
			mc.getSoundHandler().playSound(SetSound.setNotifySound(
					pitch,
					mc.player.posX,
					mc.player.posY,
					mc.player.posZ));
		}
	}

	/* エンチャント表示メソッド */
	private void displayEnchant(float partialTicks)
	{
		if(isChanged) // 手持ちアイテム 又は 表示位置が変わっていたらエンチャントリストを更新する
		{
			render.setDisplayEnchant(getCurrentItem(), displayEnchantPreset);
			if(!(displayEnchantPreset == 1 || displayEnchantPreset == 2)) isChanged = false;
		}

		int alpha = (int)(this.displayTicks * 256.0F / 10.0F);
		if(partialTicks > this.partialTicks)
		{
			this.displayTicks = this.displayTicks - (partialTicks - this.partialTicks);
		}
		else
		{
			this.displayTicks = this.displayTicks - (partialTicks + 1 - this.partialTicks);
		}
		this.partialTicks = partialTicks;
		render.renderItemEnchant(Integer.min(255, alpha));
	}
}
