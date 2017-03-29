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

        /* 説明文 */
        this.addLabel(0, 0, 0, 200, 16, 0xf0f060, I18n.format("notifymod.config.help.1"));

        /* 耐久通知チェックボックス */
        this.addControl(new GuiCheckbox(0, 0, 16, I18n.format("notifymod.config.option.notify.enabled") +" ('"+ mod.getDurabilityNotifyKeyName() +"' Key)"),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
        			@Override
        			public void actionPerformed(GuiCheckbox control)
        			{
        				mod.setDurabilityNotify(control.checked = !control.checked);
        				LiteLoader.getInstance().writeConfig(mod);
        			}
        		}).checked = mod.isDurabilityNotify();

        /* エンチャントアイテムのみチェックボックス */
        this.addControl(new GuiCheckbox(1, 16, 32, I18n.format("notifymod.config.option.notify.onlyEnchant.enabled")),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setDurabilityNotifyOnlyEnchant(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isDurabilityNotifyOnlyEnchant();

        /* ツールのエンチャントを表示する */
        this.addControl(new GuiCheckbox(2, 16, 48, I18n.format("notifymod.config.option.displayEnchant.enabled") +" ('"+ mod.getDisplayEnchantKeyName() +"' Key)"),
        		new ConfigOptionListener<GuiCheckbox>()
        		{
            		@Override
            		public void actionPerformed(GuiCheckbox control)
            		{
            			mod.setDisplayEnchant(control.checked = !control.checked);
            			LiteLoader.getInstance().writeConfig(mod);
            		}
        		}).checked = mod.isDisplayEnchant();

        /* 説明文2 */
        this.addLabel(3, 32, 64, 200, 16, 0xe0e0e0, I18n.format("notifymod.config.help.2") +" ('Ctrl+"+ mod.getDisplayEnchantKeyName() +"' Key)");

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

		}, 4, 32, 78, "", 0, 8.9F, mod.getDisplayEnchantPreset(),

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

        /* 説明文3 */
        this.addLabel(5, 32, 102, 200, 16, 0xe0e0e0, I18n.format("notifymod.config.help.3"));

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

		}, 6, 32, 116, "", 1.0F, 10.0F, mod.getEnchantDisplaySecs(),

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

        /* エンチャント名表示時間をリセット(2.0秒に)する */
        this.addControl(new GuiHoverLabel(7, 184, 122, mc.fontRendererObj, I18n.format("notifymod.config.option.displayEnchant.reset")),
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
    }

    @Override
    public void onPanelHidden()
    {
        // This example applies the changes immediately, however you may wish to only save changes
        // when the user clicks "save and close". In which case you should apply your changes here
    }
}
