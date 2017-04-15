package com.henoji;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.client.gui.GuiHoverLabel;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.resources.I18n;

/**
 * This is a simple example of adding a config panel to a mod. Your LiteMod class should implement
 * {@link Configurable} and return this class in order to support the settings functionality of the
 * mod panel.
 *
 * @author Adam Mummery-Smith
 */


public class ExampleModConfigPanel extends AbstractConfigPanel
{

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.ConfigPanel#getPanelTitle()
     */
    @Override
    public String getPanelTitle()
    {
        return I18n.format("notifymod.config.title");
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.AbstractConfigPanel#addOptions(com.mumfrey.liteloader.modconfig.ConfigPanelHost)
     */
    @Override
    protected void addOptions(ConfigPanelHost host)
    {
        final LiteModExample mod = host.<LiteModExample>getMod();
        int y = 0;

        /* 注意文 */
        this.addLabel(1, 0, y, 200, 16, 0xf0f060, I18n.format("notifymod.config.help.1"));

        y += 16;

        /* General */
        this.addLabel(2, 0, y, 200, 16, 0x99ccff, I18n.format("notifymod.config.help.2"));

        y += 16;

        /* Mod ON/OFF */
        this.addControl(new GuiCheckbox(0, 8, y, I18n.format("notifymod.config.option.notify.enabled") +" ('"+ mod.getDurabilityNotifyKeyName() +"' Key)"),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
        			@Override
        			public void actionPerformed(GuiCheckbox control)
        			{
        				mod.setDurabilityNotify(control.checked = !control.checked);
        				LiteLoader.getInstance().writeConfig(mod);
        			}
        		}).checked = mod.isDurabilityNotify();

        y += 20;

        /* Notify Sound */
        this.addLabel(3, 0, y, 200, 16, 0x99ccff, I18n.format("notifymod.config.help.3"));

        y += 16;

        /* Main Hand */
        this.addControl(new GuiCheckbox(1, 8, y, I18n.format("notifymod.config.option.mainhand")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setNotifySoundMainhand(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isNotifySoundMainhand();

        /* Off Hand */
        this.addControl(new GuiCheckbox(2, 128, y, I18n.format("notifymod.config.option.offhand")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setNotifySoundOffhand(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isNotifySoundOffhand();

        y += 16;

        /* エンチャントアイテムのみ通知音鳴らすチェックボックス */
        this.addControl(new GuiCheckbox(3, 8, y, I18n.format("notifymod.config.option.notify.onlyEnchant.enabled")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setNotifySoundOnlyEnchant(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isNotifySoundOnlyEnchant();

        y += 20;

        /* Display Durability */
        this.addLabel(4, 0, y, 200, 16, 0x99ccff, I18n.format("notifymod.config.help.4"));

        y += 16;

        /* Main Hand */
        this.addControl(new GuiCheckbox(4, 8, y, I18n.format("notifymod.config.option.mainhand")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setDisplayDurabilityMainhand(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isDisplayDurabilityMainhand();

        /* Off Hand */
        this.addControl(new GuiCheckbox(5, 128, y, I18n.format("notifymod.config.option.offhand")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setDisplayDurabilityOffhand(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isDisplayDurabilityOffhand();

        y += 20;

        /* Display Enchant */
        this.addLabel(5, 0, y, 200, 16, 0x99ccff, I18n.format("notifymod.config.help.5"));

        y += 16;

        /* アイテムが変わったらエンチャントを表示するチェックボックス */
        this.addControl(new GuiCheckbox(6, 8, y, I18n.format("notifymod.config.option.displayEnchant.enabled") +" ('"+ mod.getDisplayEnchantKeyName() +"' Key)"),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setDisplayEnchant(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isDisplayEnchant();

        y += 16;

        /* 表示位置 */
        this.addLabel(6, 24, y, 200, 16, 0xe0e0e0, I18n.format("notifymod.config.help.6") +" ('Ctrl+"+ mod.getDisplayEnchantKeyName() +"' Key)");

        y += 14;

        /* エンチャント名表示位置プリセット スライドバー */
        this.addControl(new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
			@Override
			public void setEntryValue(int id, String value) {}
			@Override
			public void setEntryValue(int id, float value)
			{
				mod.setDisplayEnchantPreset((int)value);
				LiteLoader.getInstance().writeConfig(mod);
			}
			@Override
			public void setEntryValue(int id, boolean value) {}

		}, 4, 24, y, "", 0, 8.9F, mod.getDisplayEnchantPreset(),

    		new GuiSlider.FormatHelper()
    		{
				@Override
				public String getText(int id, String name, float value)
				{
					return I18n.format("notifymod.config.option.displayEnchant.preset." + mod.getDisplayEnchantPreset());
				}
    		}),
        		new ConfigOptionListener<GuiSlider>()
				{
					@Override
					public void actionPerformed(GuiSlider control){}
				});

        y += 24;

        /* 表示時間 */
        this.addLabel(7, 24, y, 200, 16, 0xe0e0e0, I18n.format("notifymod.config.help.7"));

        y += 14;

        final GuiSlider displaySecSlider;

        /* エンチャント名表示時間 スライドバー */
        this.addControl(displaySecSlider = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
			@Override
			public void setEntryValue(int id, String value) {}
			@Override
			public void setEntryValue(int id, float value)
			{
				mod.setEnchantDisplaySecs((float)((int)(value *10) /10.0F));
				LiteLoader.getInstance().writeConfig(mod);
			}
			@Override
			public void setEntryValue(int id, boolean value) {}

		}, 6, 24, y, "", 1.0F, 10.0F, mod.getEnchantDisplaySecs(),

    		new GuiSlider.FormatHelper()
    		{
				@Override
				public String getText(int id, String name, float value)
				{
					return mod.getEnchantDisplaySecs() + I18n.format("notifymod.config.option.displayEnchant.sec");
				}
    		}),
        		new ConfigOptionListener<GuiSlider>()
				{
					@Override
					public void actionPerformed(GuiSlider control){}
				});

        y += 6;

        /* エンチャント名表示時間をリセット(2.0秒に)する */
        this.addControl(new GuiHoverLabel(7, 176, y, mc.fontRendererObj, I18n.format("notifymod.config.option.reset")),
        		new ConfigOptionListener<GuiHoverLabel>()
        		{
            		@Override
            		public void actionPerformed(GuiHoverLabel control)
            		{
            			mod.setEnchantDisplaySecs(2.0F);
            			displaySecSlider.setSliderValue(2.0F, false);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		});

        y += 24 - 6;
    }

    @Override
    public void onPanelHidden()
    {
        // This example applies the changes immediately, however you may wish to only save changes
        // when the user clicks "save and close". In which case you should apply your changes here
    }
}
