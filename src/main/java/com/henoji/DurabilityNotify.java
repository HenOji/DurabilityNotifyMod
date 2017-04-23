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
	private Minecraft mc = Minecraft.getMinecraft();
	private Render    render = new Render();

	private boolean isNotifySoundOnlyEnchant,
					isDisplayEnchant,
					isPresetChanged;

	private boolean[] isNotifySoundArray = {false,false},
					  isDisplayDurabilityArray = {false,false},
					  isItemChanged = {false,false};

	private float enchantDisplaySecs,
				  displayTicks,
				  partialTicks;

	private int   displayEnchantPreset;

	private final int[] NOTIFY_ARRAY = {50, 25, 10, 3, 2, 1, 0, -1}; // 通知耐久値 降順

	private ItemStack[] prevItemArray   = {ItemStack.EMPTY, ItemStack.EMPTY};
	private int[]       prevHotbarArray = {-1, -1};
	private int[]       notifyKeyArray  = {0, 0};

	/* Constructor */
	public DurabilityNotify(){}

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

	/* ここから始まる my Revolution */
	public void startNotify(float partialTicks)
	{
		ItemStack mainhandItem = mc.player.inventory.getCurrentItem();
		int currentHotbar      = mc.player.inventory.currentItem;
		ItemStack offhandItem  = mc.player.inventory.offHandInventory.get(0);

		/* 耐久通知検索 と 耐久値表示*/
		searchAndDisplay(mainhandItem, currentHotbar, 0);
		searchAndDisplay(offhandItem, -1, 1);

		if(isItemChanged[0])
		{
			if(isDisplayEnchant && mainhandItem.isItemEnchanted()) setDisplayTicks();
			else if(!mainhandItem.isItemEnchanted())
			{
				displayTicks     = 0;
				isItemChanged[0] = false;
			}
		}
		// エンチャント表示
		if((int)displayTicks > 0) displayEnchant(partialTicks, mainhandItem);
	}

	/* アイテムの耐久通知検索と耐久値表示 */
	private void searchAndDisplay(ItemStack handItem, int currentHotbar, int handKey)
	{
		boolean isTool = handItem.isItemStackDamageable();
		int itemDurability = handItem.getMaxDamage() - handItem.getItemDamage();
		if(isNotifySoundArray[handKey]) searchNotify(handItem, currentHotbar, handKey, itemDurability);
		if(isDisplayDurabilityArray[handKey] && isTool) displayDurability(handKey, itemDurability);
	}

	/* 耐久通知検索メソッド */
	private void searchNotify(ItemStack handItem, int currentHotbar, int handKey, int itemDurability)
	{
		boolean isTool = handItem.isItemStackDamageable();
		/* (1 OR 2) AND 3 */
		if(prevHotbarArray[handKey] != currentHotbar               // 1: 前Tickとホットバーが違うか
		|| prevItemArray[handKey].getItem() != handItem.getItem()) // 2: 前Tickとアイテムが違うか
		{
			isItemChanged[handKey] = true; // アイテムが変わった
			notifyKeyArray[handKey] = 0;
			if(isTool && itemDurability <= NOTIFY_ARRAY[0])  // 3: 通知耐久最大値以下か
			{
				for (notifyKeyArray[handKey] = 1; notifyKeyArray[handKey] < NOTIFY_ARRAY.length; notifyKeyArray[handKey]++)
				{
					// アイテムの耐久値が通知する値より大きい
					if(itemDurability > NOTIFY_ARRAY[notifyKeyArray[handKey]]) break;
				}
				if(isNotifySoundArray[handKey]) notifySound(handItem, notifyKeyArray[handKey]); // 通知音
			}
		}
		/* (1 NOR 2) AND 4 */
		/* 4: アイテム耐久値が通知耐久値以下か */
		else if(isTool && itemDurability <= NOTIFY_ARRAY[notifyKeyArray[handKey]])
		{
			notifyKeyArray[handKey]++;
			if(isNotifySoundArray[handKey]) notifySound(handItem, notifyKeyArray[handKey]); // 通知音
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
			mc.getSoundHandler().playSound(SetSound.setNotifySound(pitch));
		}
	}

	/* 耐久表示メソッド */
	private void displayDurability(int handKey, int itemDurability)
	{
		int fontColor;
		if(notifyKeyArray[handKey] > 2)
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
		if(isItemChanged[0] || isPresetChanged)
		{
			render.setDisplayEnchant(currentItem, displayEnchantPreset, isItemChanged[0]);
			isItemChanged[0] = false;
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
