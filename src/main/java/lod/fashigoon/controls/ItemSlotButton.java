package lod.fashigoon.controls;

import legend.core.lang.I18nText;
import legend.core.lang.TextComponent;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.TextColour;
import legend.game.inventory.screens.controls.Button;
import lod.fashigoon.Fashigoon;
import lod.fashigoon.FashionItem;
import org.legendofdragoon.modloader.registries.RegistryId;

import static legend.game.Text.renderText;
import static legend.game.Text.textZ_800bdf00;
import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;

public class ItemSlotButton extends Button {
  private final FontOptions DEFAULT = new FontOptions().colour(TextColour.LIGHT_GREY).shadowColour(TextColour.DARK_GREY);
  private final FontOptions HOVER = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.LIGHT_GREY);
  private FashionItem item;

  private FontOptions currentFontOptions = DEFAULT;
  public ItemSlotButton(final FashionItem item) {
    final I18nText buttonText = new I18nText(item == null ?
      Fashigoon.getTranslationKey("menu", "none") :
      item.getTranslationKey()
    );

    super(buttonText);
    this.setItem(item);

    this.removeControl(this.getControl(0));

    this.onGotFocus(() -> this.currentFontOptions = HOVER);
    this.onLostFocus(() -> this.currentFontOptions = DEFAULT);
  }

  public FontOptions getFontOptions() {
    return this.currentFontOptions;
  }

  public void setItem(final FashionItem item) {
      this.item = item;
      final I18nText buttonText = new I18nText(item == null ?
        Fashigoon.getTranslationKey("menu", "none") :
        item.getTranslationKey()
      );

      this.setText(buttonText);
  }

  public FashionItem getItem() {
    return this.item;
  }

  @Override
  protected void render(final int x, final int y) {
    final int oldZ = textZ_800bdf00;
    textZ_800bdf00 = this.getZ() - 1;
    renderText(this.getText().get(), x, y, this.currentFontOptions);
    textZ_800bdf00 = oldZ;
  }
}
