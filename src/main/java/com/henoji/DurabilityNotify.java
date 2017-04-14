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
	private Minecraft mc;
	private Render    render;

	private boolean isNotifySoundOnlyEnchant,
					isDisplayEnchant,
					isItemChanged,
					isPresetChanged;

	private boolean[] isNotifySoundArray = {false,false},
					  isDisplayDurabilityArray = {false,false};

	private float enchantDisplaySecs,
				  displayTicks,
				  partialTicks;

	private int   displayEnchantPreset;

	private final int[] NOTIFY_ARRAY = {50, 25, 10, 3, 2, 1, 0, -1}; // 通知耐久値 降順

	private ItemStack[] prevItemArray   = {ItemStack.EMPTY, ItemStack.EMPTY};
	private int[]       prevHotbarArray = {-1, -1};
	private int[]       notifyKeyArray  = {0, 0};

	/* Constructor */
	public DurabilityNotify(Minecraft mc)
	{
		this.render = new Render(mc);
	}

	/* 設定の通知音を鳴らすか */
	public boolean isNotifySoundHand(int Key)
	{
		return this.isNotifySoundArray[Key];
	}
	public void setNotifySoundHand(boolean toggle, int Key)
	{
		this.isNotifySoundArray[Key] = toggle;
	}

	/* 設定のエンチャントのみに適用するか */
	public boolean isNotifySoundOnlyEnchant()
	{
		return this.isNotifySoundOnlyEnchant;
	}
	public void setNotifySoundOnlyEnchant(boolean toggle)
	{
		this.isNotifySoundOnlyEnchant = toggle;
	}

	/* 設定の耐久値を表示するか */
	public boolean isDisplayDurabilityHand(int Key)
	{
		return this.isDisplayDurabilityArray[Key];
	}
	public void setDisplayDurabilityHand(boolean toggle, int Key)
	{
		this.isDisplayDurabilityArray[Key] = toggle;
	}

	/* 設定のエンチャントの表示 */
	public boolean isDisplayEnchant()
	{
		return this.isDisplayEnchant;
	}
	public void setDisplayEnchant(boolean toggle)
	{
		this.isDisplayEnchant = toggle;
	}

	/* 設定のエンチャント表示位置プリセット */
	public int getDisplayEnchantPreset()
	{
		return this.displayEnchantPreset;
	}
	public void setDisplayEnchantPreset(int preset)
	{
		this.displayEnchantPreset = preset;
		this.isPresetChanged = true;
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
	public void setDisplayTicks()
	{
		this.displayTicks = this.enchantDisplaySecs * 20.0F;
	}

	/* ここから始まる */
	public void startNotify(Minecraft mc, float partialTicks)
	{
		this.mc = mc;

		ItemStack mainhandItem = mc.player.inventory.getCurrentItem();
		int currentHotbar      = mc.player.inventory.currentItem;
		ItemStack offhandItem  = mc.player.inventory.offHandInventory.get(0);

		/* 耐久通知検索 */
		searchNotify(mainhandItem, currentHotbar, 0);
		searchNotify(offhandItem, -1, 1);

		// エンチャント表示
		if(isItemChanged && isDisplayEnchant && mainhandItem.isItemEnchanted()) setDisplayTicks();
		if(!mainhandItem.isItemEnchanted()) displayTicks = 0;
		if((int)displayTicks > 0)
		{
			displayEnchant(partialTicks, mainhandItem);
		}
	}

	/* 耐久通知検索メソッド */
	private void searchNotify(ItemStack handItem, int currentHotbar, int handKey)
	{
		/* 1 AND (2 OR 3) AND 4 */
		if(handItem.isItemStackDamageable()) // 1: ツールか
		{
			int itemDurability = handItem.getMaxDamage() - handItem.getItemDamage();

			if(prevHotbarArray[handKey] != currentHotbar               // 2: 前Tickとホットバーが違うか
			|| prevItemArray[handKey].getItem() != handItem.getItem()) // 3: 前Tickとアイテムが違うか
			{
				notifyKeyArray[handKey] = 0;
				if(handKey == 0) isItemChanged = true; // アイテムが変わった
				if(itemDurability <= NOTIFY_ARRAY[0]) // 4: 通知耐久最大値以下か
				{
					for (notifyKeyArray[handKey] = 1; notifyKeyArray[handKey] < NOTIFY_ARRAY.length; notifyKeyArray[handKey]++)
					{
						// アイテムの耐久値が通知する値より大きい
						if(itemDurability > NOTIFY_ARRAY[notifyKeyArray[handKey]]) break;
					}
					if(isNotifySoundArray[handKey]) notifySound(handItem, notifyKeyArray[handKey]); // 通知音
				}
			}
			else // ホットバーとアイテムが前Tickと同じ
			{
				if(itemDurability <= NOTIFY_ARRAY[notifyKeyArray[handKey]]) // 耐久値が通知耐久値以下
				{
					notifyKeyArray[handKey]++;
					if(isNotifySoundArray[handKey]) notifySound(handItem, notifyKeyArray[handKey]); // 通知音
				}
			}
			/* 耐久値表示 */
			if(isDisplayDurabilityArray[handKey]) displayDurability(handKey, itemDurability);
		}
		/* 配列に代入 */
		prevItemArray[handKey]   = handItem;
		prevHotbarArray[handKey] = currentHotbar;
	}

	/* 通知音メソッド */
	private void notifySound(ItemStack currentItem, int notifyKey)
	{
		if(!isNotifySoundOnlyEnchant       // 1: "エンチャントのみ通知" にチェックが付いているか
		||  currentItem.isItemEnchanted()) // 2: アイテムにエンチャントが付いているか
		{
			float pitch = ( 0.85F + notifyKey * 0.15F );
			mc.getSoundHandler().playSound(SetSound.setNotifySound(
					pitch,
					mc.player.posX,
					mc.player.posY,
					mc.player.posZ));
		}
	}

	/* 耐久表示メソッド */
	private void displayDurability(int handKey, int itemDurability)
	{
		int fontColor;
		if(notifyKeyArray[handKey] > 2) // 耐久値 色
		{
			fontColor = 0xffff4040; // 赤色
		}
		else if(notifyKeyArray[handKey] > 0)
		{
			fontColor = 0xffffff20; // 黄色
		}
		else
		{
			fontColor = 0xff80ff20; // 緑色
		}
		render.renderSelectedString(Integer.toString(itemDurability), fontColor, handKey);
	}

	/* エンチャント表示メソッド */
	private void displayEnchant(float partialTicks, ItemStack currentItem)
	{
		if(isItemChanged || isPresetChanged)
		{
			render.setDisplayEnchant(currentItem, displayEnchantPreset, isItemChanged);
			isItemChanged = false;
			if(!(displayEnchantPreset == 1 || displayEnchantPreset == 2)) isPresetChanged = false;
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
