package com.henoji;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.PreRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;

/**
 * This is a very simple example LiteMod, it draws an analogue clock on the minecraft HUD using
 * a traditional onTick hook supplied by LiteLoader's "Tickable" interface.
 *
 * @author Adam Mummery-Smith
 */
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="durabilitynotifymod.json")
public class LiteModExample implements Tickable, PreRenderListener, Configurable
{
	/**
	 * This is our instance of Clock which we will draw every tick
	 */
//	private Clock clock = new Clock(10, 10);
	private DurabilityNotify dNotify = new DurabilityNotify(Minecraft.getMinecraft());

	/**
	 * This is a keybinding that we will register with the game and use to toggle the clock
	 *
	 * Notice that we specify the key name as an *unlocalised* string. The localisation is provided from the included resource file
	 */
//	private static KeyBinding clockKeyBinding = new KeyBinding("key.clock.toggle", Keyboard.KEY_F12, "key.categories.litemods");
	private static KeyBinding durabilityNotifyKey = new KeyBinding("key.durabilityNotify.toggle", Keyboard.KEY_I, "key.categories.litemods");
	private static KeyBinding displayEnchantKey   = new KeyBinding("key.displayEnchant", Keyboard.KEY_G, "key.categories.litemods");

	@Expose
	@SerializedName("durability_notify")
	private boolean isNotify = true;

	@Expose
	@SerializedName("durability_notify_sound_Mainhand")
	private boolean isNotifySoundMainhand = true;

	@Expose
	@SerializedName("durability_notify_sound_Offhand")
	private boolean isNotifySoundOffhand = true;

	@Expose
	@SerializedName("durability_notify_sound_onlyEnchant")
	private boolean isNotifySoundOnlyEnchant = false;

	@Expose
	@SerializedName("durability_notify_durability_Mainhand")
	private boolean isDisplayDurabilityMainhand = true;

	@Expose
	@SerializedName("durability_notify_durability_Offhand")
	private boolean isDisplayDurabilityOffhand = true;

	@Expose
	@SerializedName("durability_notify_displayEnchant")
	private boolean isDisplayEnchant = true;

	@Expose
	@SerializedName("durability_notify_displayEnchant_preset")
	private int displayEnchantPreset = 0;

	@Expose
	@SerializedName("durability_notify_displayEnchant_Secs")
	private float enchantDisplaySecs = 2.0F;

	/**
	 * Default constructor. All LiteMods must have a default constructor. In general you should do very little
	 * in the mod constructor EXCEPT for initialising any non-game-interfacing components or performing
	 * sanity checking prior to initialisation
	 */
	public LiteModExample()
	{
	}

	/**
	 * getName() should be used to return the display name of your mod and MUST NOT return null
	 *
	 * @see com.mumfrey.liteloader.LiteMod#getName()
	 */
	@Override
	public String getName()
	{
		return "Durability Notify Mod";
	}

	/**
	 * getVersion() should return the same version string present in the mod metadata, although this is
	 * not a strict requirement.
	 *
	 * @see com.mumfrey.liteloader.LiteMod#getVersion()
	 */
	@Override
	public String getVersion()
	{
		return "1.0.0";
	}

	@Override
	public Class<? extends ConfigPanel> getConfigPanelClass()
	{
	    return ExampleModConfigPanel.class;
	}

	/**
	 * init() is called very early in the initialisation cycle, before the game is fully initialised, this
	 * means that it is important that your mod does not interact with the game in any way at this point.
	 *
	 * @see com.mumfrey.liteloader.LiteMod#init(java.io.File)
	 */
	@Override
	public void init(File configPath)
	{
		// The key binding declared above won't do anything unless we register it, ModUtilties provides
		// a convenience method for this

		LiteLoader.getInput().registerKeyBinding(LiteModExample.durabilityNotifyKey);
		LiteLoader.getInput().registerKeyBinding(LiteModExample.displayEnchantKey);

		this.dNotify.setNotifySoundHand(this.isNotifySoundMainhand, 0);
		this.dNotify.setNotifySoundHand(this.isNotifySoundOffhand, 1);
		this.dNotify.setNotifySoundOnlyEnchant(this.isNotifySoundOnlyEnchant);
		this.dNotify.setDisplayDurabilityHand(this.isDisplayDurabilityMainhand, 0);
		this.dNotify.setDisplayDurabilityHand(this.isDisplayDurabilityOffhand, 1);
		this.dNotify.setDisplayEnchant(this.isDisplayEnchant);
		this.dNotify.setDisplayEnchantPreset(this.displayEnchantPreset);
		this.dNotify.setEnchantDisplaySecs(this.enchantDisplaySecs);
	}

	/**
	 * upgradeSettings is used to notify a mod that its version-specific settings are being migrated
	 *
	 * @see com.mumfrey.liteloader.LiteMod#upgradeSettings(java.lang.String, java.io.File, java.io.File)
	 */
	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath)
	{
	}

	@Override
	public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock)
	{
		// The three checks here are critical to ensure that we only draw the clock as part of the "HUD"
		// and don't draw it over active GUI's or other elements
		// ゲームプレイ画面のみ作動
		if(mc.currentScreen == null && Minecraft.isGuiEnabled() && inGame && mc.playerController.gameIsSurvivalOrAdventure())
		{
			if (LiteModExample.durabilityNotifyKey.isPressed())
			{
				setDurabilityNotify(!isNotify);
				if(isNotify)
				{
					mc.getSoundHandler().playSound(SetSound.setNotifySound(2.0F));
				}
				else
				{
					mc.getSoundHandler().playSound(SetSound.setNotifySound(0.6F));
				}
				LiteLoader.getInstance().writeConfig(this);
			}
			if(LiteModExample.displayEnchantKey.isPressed() && isNotify && mc.player.inventory.getCurrentItem().isItemEnchanted())
			{
				if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				{
					// displayEnchantKey + Ctrl
					setDisplayEnchantPreset((this.displayEnchantPreset + 1) % 9); // エンチャント表示位置
					LiteLoader.getInstance().writeConfig(this);
				}
				dNotify.setDisplayTicks();
			}
			// 通知スタート
			if (isNotify)
			{
				dNotify.startNotify(mc, partialTicks);
			}
		}
	}

/*	boolean getClockVisibility()

	{
	    return this.clock.isVisible();
	}

	void setClockVisibility(boolean visible)
	{
		this.clock.setVisible(this.clockVisible = visible);
	}*/

	/* 通知機能 ON/OFF */
	boolean isDurabilityNotify()
	{
		return this.isNotify;
	}

	void setDurabilityNotify(boolean toggle)
	{
		this.isNotify = toggle;
	}

	/* 通知音 メインハンド ON/OFF */
	boolean isNotifySoundMainhand()
	{
		return this.dNotify.isNotifySoundHand(0);
	}

	void setNotifySoundMainhand(boolean toggle)
	{
		this.dNotify.setNotifySoundHand(this.isNotifySoundMainhand = toggle, 0);
	}

	/* 通知音 オフハンド ON/OFF */
	boolean isNotifySoundOffhand()
	{
		return this.dNotify.isNotifySoundHand(1);
	}

	void setNotifySoundOffhand(boolean toggle)
	{
		this.dNotify.setNotifySoundHand(this.isNotifySoundOffhand = toggle, 1);
	}

	/* 通知音エンチャントのみ ON/OFF */
	boolean isNotifySoundOnlyEnchant()
	{
		return this.dNotify.isNotifySoundOnlyEnchant();
	}

	/* 耐久値表示 メインハンド ON/OFF */
	boolean isDisplayDurabilityMainhand()
	{
		return this.dNotify.isDisplayDurabilityHand(0);
	}

	void setDisplayDurabilityMainhand(boolean toggle)
	{
		this.dNotify.setDisplayDurabilityHand(this.isDisplayDurabilityMainhand = toggle, 0);
	}

	/* 耐久値表示 オフハンド ON/OFF */
	boolean isDisplayDurabilityOffhand()
	{
		return this.dNotify.isDisplayDurabilityHand(1);
	}

	void setDisplayDurabilityOffhand(boolean toggle)
	{
		this.dNotify.setDisplayDurabilityHand(this.isDisplayDurabilityOffhand = toggle, 1);
	}

	void setNotifySoundOnlyEnchant(boolean toggle)
	{
		this.dNotify.setNotifySoundOnlyEnchant(this.isNotifySoundOnlyEnchant = toggle);
	}

	/* 通知機能 ON/OFF キー名 getter */
	String getDurabilityNotifyKeyName()
	{
		return Keyboard.getKeyName(durabilityNotifyKey.getKeyCode());
	}

	/* エンチャント名表示キー"名" getter */
	String getDisplayEnchantKeyName()
	{
		return Keyboard.getKeyName(displayEnchantKey.getKeyCode());
	}

	/* エンチャント表示getter */
	boolean isDisplayEnchant()
	{
		return this.dNotify.isDisplayEnchant();
	}

	void setDisplayEnchant(boolean toggle)
	{
		this.dNotify.setDisplayEnchant(this.isDisplayEnchant = toggle);
	}

	/* エンチャント表示位置プリセット */
	int getDisplayEnchantPreset()
	{
		return this.dNotify.getDisplayEnchantPreset();
	}

	void setDisplayEnchantPreset(int preset)
	{
		this.dNotify.setDisplayEnchantPreset(this.displayEnchantPreset = preset);
	}

	/* エンチャント表示時間 */
	float getEnchantDisplaySecs()
	{
		return this.dNotify.getEnchantDisplaySecs();
	}

	void setEnchantDisplaySecs(float Secs)
	{
		this.dNotify.setEnchantDisplaySecs(this.enchantDisplaySecs = Secs);
	}

	@Override
	public void onRenderWorld(float partialTicks)
	{
//		System.err.printf(">> onRenderWorld!\n");
	}

	@Override
	public void onSetupCameraTransform(float partialTicks, int pass, long timeSlice)
	{
//		System.err.printf(">> onSetupCameraTransform %s, %d, %d!\n", partialTicks, pass, timeSlice);
	}

	@Override
	public void onRenderSky(float partialTicks, int pass)
	{
//		System.err.printf(">> onRenderSky %s, %d!\n", partialTicks, pass);
	}

	@Override
	public void onRenderClouds(float partialTicks, int pass, RenderGlobal renderGlobal)
	{
//		System.err.printf(">> onRenderClouds %s, %d!\n", partialTicks, pass);
	}

	@Override
	public void onRenderTerrain(float partialTicks, int pass)
	{
//		System.err.printf(">> onRenderTerrain %s, %d!\n", partialTicks, pass);
	}
}
