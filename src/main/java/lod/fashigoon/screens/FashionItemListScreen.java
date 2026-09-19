package lod.fashigoon.screens;

import legend.core.lang.I18nText;
import legend.core.lang.TextComponent;
import legend.core.platform.input.InputAction;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import lod.fashigoon.Fashigoon;
import lod.fashigoon.FashionItem;
import lod.fashigoon.FashionSlotType;
import lod.fashigoon.controls.ItemSlotButton;
import org.jetbrains.annotations.NotNull;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.function.Consumer;

import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;
import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;
import static lod.fashigoon.Fashigoon.getTranslationKey;
import static lod.fashigoon.screens.CharacterCustomizationScreen.UI_WHITE;

public class FashionItemListScreen extends MenuScreen {
  private ArrayList<ItemSlotButton> itemButtons = new ArrayList<>();
  private FashionSlotType slotType;

  public FashionItemListScreen(final int charIndex, final FashionSlotType slotType, final Consumer<RegistryId> equipCall, final Consumer<FashionItem> updateDescriptionText) {
    this.slotType = slotType;

    int y = 68;
    for(final RegistryId itemId : FASHION_ITEM_REGISTRY) {
      final FashionItem fashionItem = FASHION_ITEM_REGISTRY.getEntry(itemId).get();

      if(fashionItem.characterType == gameState_800babc8.charData_32c.get(charIndex).template && fashionItem.slotType == slotType) {
        final ItemSlotButton button = this.addButton(fashionItem, 180, y, () -> {
          this.deferAction(this::unload);
          equipCall.accept(itemId);
        });

        button.onHoverIn(() -> {
          playMenuSound(1);
          this.setFocus(button);
          updateDescriptionText.accept(fashionItem);
        });

        y += 20;
      }
    }

  }

  @Override
  protected void render() {
    renderText(I18n.translate(getTranslationKey("menu", this.slotType.name().toLowerCase())), 180, 50, UI_WHITE);
  }

  @Override
  protected boolean propagateRender() {
    return true;
  }

  @Override
  protected boolean propagateInput() {
    return false;
  }

  @Override
  protected InputPropagation inputActionPressed(@NotNull final InputAction action, final boolean repeat) {
    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.deferAction(this::unload);
    }
    return super.inputActionPressed(action, repeat);
  }

  private void unload() {
    this.getStack().popScreen();
    playMenuSound(3);
  }

  private ItemSlotButton addButton(final FashionItem fashionItem, final int x, final int y, final Runnable onClick) {
    final ItemSlotButton button = this.addControl(new ItemSlotButton(fashionItem));
    button.setPos(x, y);
    button.setZ(1);
    button.setWidth(80);
    button.onPressed(onClick::run);

    this.itemButtons.add(button);

    return button;
  }
}
