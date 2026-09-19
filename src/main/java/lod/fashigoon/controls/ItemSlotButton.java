package lod.fashigoon.controls;

import legend.core.lang.TextComponent;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.inventory.screens.controls.Button;

import static legend.game.SItem.UI_TEXT;
import static legend.game.Text.renderText;
import static legend.game.Text.textZ_800bdf00;

public class ItemSlotButton extends Button {
  private final FontOptions DEFAULT = new FontOptions().colour(TextColour.LIGHT_GREY).shadowColour(TextColour.DARK_GREY);
  private final FontOptions HOVER = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.LIGHT_GREY);

  private FontOptions currentFontOptions = DEFAULT;
  public ItemSlotButton(TextComponent text) {
    super(text);

    this.onGotFocus(() -> this.currentFontOptions = HOVER);
    this.onLostFocus(() -> this.currentFontOptions = DEFAULT);
  }

  @Override
  public void hoverIn() {

  }

  @Override
  public void hoverOut() {

  }

  public FontOptions getFontOptions() {
    return this.currentFontOptions;
  }

  @Override
  protected void render(final int x, final int y) {
    final int oldZ = textZ_800bdf00;
    textZ_800bdf00 = this.getZ() - 1;
    renderText(this.getText().get(), x, y, this.currentFontOptions);
    textZ_800bdf00 = oldZ;
  }
}
