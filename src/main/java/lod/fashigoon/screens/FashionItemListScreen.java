package lod.fashigoon.screens;

import legend.core.platform.input.InputAction;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.Control;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
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
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;
import static legend.game.sound.Audio.playMenuSound;
import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;
import static lod.fashigoon.Fashigoon.getTranslationKey;
import static lod.fashigoon.screens.CharacterCustomizationScreen.UI_WHITE;

public class FashionItemListScreen extends MenuScreen {
  private ArrayList<ItemSlotButton> itemButtons = new ArrayList<>();

  private int charIndex;
  private FashionSlotType slotType;
  private Consumer<FashionItem> updateDescriptionCallback;

  private int currentPage = 0;
  private static final int ITEMS_PER_PAGE = 4;

  public FashionItemListScreen(final int charIndex, final FashionSlotType slotType, final Consumer<FashionItem> equipCallback, final Consumer<FashionItem> updateDescriptionCallback) {
    this.charIndex = charIndex;
    this.slotType = slotType;
    this.updateDescriptionCallback = updateDescriptionCallback;

    final int yBase = 68;
    int yOffset = 0;
    for(final RegistryId itemId : FASHION_ITEM_REGISTRY) {
      final FashionItem fashionItem = FASHION_ITEM_REGISTRY.getEntry(itemId).get();

      if(this.canEquip(fashionItem)) {
        final ItemSlotButton button = this.addButton(fashionItem, 180, yBase + yOffset, () -> {
          this.deferAction(this::unload);
          equipCallback.accept(fashionItem);
        });

        button.hide();
        button.onHoverIn(() -> {
          playMenuSound(1);
          this.setFocus(button);
          updateDescriptionCallback.accept(fashionItem);
        });

        yOffset += 18;
        yOffset = yOffset % (18 * ITEMS_PER_PAGE);
      }
    }

    this.loadPage(this.currentPage);
  }

  private boolean canEquip(final FashionItem item) {
    return item.slotType == this.slotType && item.characterType == gameState_800babc8.charData_32c.get(this.charIndex).template;
  }

  @Override
  protected void render() {
    renderText(I18n.translate(getTranslationKey("menu", this.slotType.name().toLowerCase())), 180, 50, UI_WHITE);

    renderText(String.valueOf(this.currentPage + 1) + '/' + this.getPageCount(), 230, 170, UI_WHITE);
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
      this.updateDescriptionCallback.accept(null);
      this.deferAction(this::unload);
    }
    else if(action == INPUT_ACTION_MENU_RIGHT.get() && !repeat) {
      this.loadPage(this.currentPage + 1);
    }
    else if(action == INPUT_ACTION_MENU_LEFT.get() && !repeat) {
      this.loadPage(this.currentPage - 1);
    }
    else if(action == INPUT_ACTION_MENU_UP.get() && !repeat) {
      if(this.getFocus() != null) {
        final int currentButtonIndex = this.itemButtons.indexOf(this.getFocus());
        final int newButtonIndex = Math.floorMod(currentButtonIndex - 1, this.itemButtons.size());
        this.setFocus(this.itemButtons.get(newButtonIndex));
        playMenuSound(1);
      }
    }
    else if(action == INPUT_ACTION_MENU_DOWN.get() && !repeat) {
      if(this.getFocus() != null) {
        final int currentButtonIndex = this.itemButtons.indexOf(this.getFocus());
        final int newButtonIndex = (currentButtonIndex + 1) % this.itemButtons.size();
        this.setFocus(this.itemButtons.get(newButtonIndex));
        playMenuSound(1);
      }
    }

    return super.inputActionPressed(action, repeat);
  }

  private int getPageCount() {
    return Math.ceilDiv(this.itemButtons.size(), ITEMS_PER_PAGE);
  }

  private void loadPage(final int newPage) {
    this.itemButtons.forEach(Control::hide);
    this.currentPage = Math.floorMod(newPage, this.getPageCount());
    final int itemCount = currentPage + 1 == this.getPageCount() ? this.itemButtons.size() - this.currentPage * ITEMS_PER_PAGE : ITEMS_PER_PAGE;
    for(int buttonIndex = this.currentPage * ITEMS_PER_PAGE; buttonIndex < this.currentPage * ITEMS_PER_PAGE + itemCount; buttonIndex++) {
      this.itemButtons.get(buttonIndex).show();
    }
    this.setFocus(this.itemButtons.get(this.currentPage * ITEMS_PER_PAGE));
    this.updateDescriptionCallback.accept(this.itemButtons.get(this.currentPage * ITEMS_PER_PAGE).getItem());
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
