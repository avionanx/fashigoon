package lod.fashigoon.screens;

import legend.core.lang.I18nText;
import legend.core.lang.TextComponent;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.TextColour;
import lod.fashigoon.CharacterFashionData;
import legend.core.platform.input.InputAction;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.ui.UiBox;
import lod.fashigoon.Fashigoon;
import lod.fashigoon.FashigoonSlots;
import lod.fashigoon.controls.ItemSlotButton;
import org.jetbrains.annotations.NotNull;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_LEFT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_RIGHT;
import static lod.fashigoon.Fashigoon.FASHION_DATA_CONFIG;
import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;
import static lod.fashigoon.Fashigoon.FASHION_SLOT_REGISTRY;
import static lod.fashigoon.Fashigoon.getExtraWidth;
import static lod.fashigoon.Fashigoon.getTranslationKey;
import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;

public class CharacterCustomizationScreen extends MenuScreen {
  public static final FontOptions UI_WHITE = new FontOptions().colour(TextColour.WHITE).size(0.85f);
  public static final FontOptions UI_WHITE_SMALL = new FontOptions().colour(TextColour.WHITE).size(0.5f);


  private final HashMap<Integer, CharacterFashionData> characterData;
  private final List<Integer> characterIds;

  private int currentCharIndex = 0;

  private final List<ItemSlotButton> slotButtons = new ArrayList<>();
  private final UiBox contentBox;
  private final UiBox descriptionBox;
  private String descriptionTranslationKey;

  public CharacterCustomizationScreen() {
    playMenuSound(4);
    this.characterData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    this.characterIds = this.characterData.keySet().stream().toList();
    this.currentCharIndex = this.characterIds.getFirst();

    this.contentBox = new UiBox(40, 40, 288, 150);
    this.descriptionBox = new UiBox(40, 195, 288, 24);
    this.loadCharacterDataAndButtons();
  }

  @Override
  protected void render() {
    this.contentBox.render();
    renderText(I18n.translate(gameState_800babc8.charData_32c.get(this.currentCharIndex).template.getTranslationKey()), 58, 50, UI_WHITE);
    if(this.descriptionTranslationKey != null) {
      renderText(I18n.translate(this.descriptionTranslationKey), 58, 205, UI_WHITE);
      this.descriptionBox.render();
    }

    renderText(I18n.translate(getTranslationKey("menu", "weapon")), 58, 68, UI_WHITE_SMALL);
    renderText(I18n.translate(getTranslationKey("menu", "weapon")), 58, 68, UI_WHITE_SMALL);
    renderText(I18n.translate(getTranslationKey("menu", "outfit")), 58, 94, UI_WHITE_SMALL);
    renderText(I18n.translate(getTranslationKey("menu", "attachment")), 58, 120, UI_WHITE_SMALL);
  }

  @Override
  protected InputPropagation inputActionPressed(@NotNull final InputAction action, final boolean repeat) {
    if(action == INPUT_ACTION_MENU_RIGHT.get() && !repeat) {
      this.currentCharIndex = (this.currentCharIndex + 1) % this.characterData.size();
      this.loadCharacterDataAndButtons();
    }
    else if(action == INPUT_ACTION_MENU_LEFT.get() && !repeat) {
      this.currentCharIndex = Math.floorMod(this.currentCharIndex - 1, this.characterData.size());
      this.loadCharacterDataAndButtons();
    }
    else if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.deferAction(this::unload);
    }
    return super.inputActionPressed(action, repeat);
  }

  private void clearButtons() {
    this.slotButtons.forEach(this::removeControl);
    this.slotButtons.clear();
  }

  private void loadCharacterDataAndButtons() {
    final var charData = this.characterData.get(this.currentCharIndex);
    this.clearButtons();

    charData.slots.forEach((slotId, itemId) -> {
      final I18nText buttonText = new I18nText(itemId == null ?
        Fashigoon.getTranslationKey("menu", "none") :
        FASHION_ITEM_REGISTRY.getEntry(itemId).get().getNameTranslationKey()
      );
      // TODO fix this awful thing
      int y = 0;
      final var slotDelegate = FASHION_SLOT_REGISTRY.getEntry(slotId);

      if(slotDelegate == FashigoonSlots.WEAPON) {
        y = 75;
      } else if(slotDelegate == FashigoonSlots.OUTFIT) {
        y = 100;
      } else if(slotDelegate == FashigoonSlots.ATTACHMENT_1) {
        y = 125;
      } else if(slotDelegate == FashigoonSlots.ATTACHMENT_2) {
        y = 145;
      } else if(slotDelegate == FashigoonSlots.ATTACHMENT_3) {
        y = 165;
      }

      final ItemSlotButton button = this.addButton(buttonText, 58, y, () -> {
        this.getStack().pushScreen(new FashionItemListScreen(this.currentCharIndex, slotDelegate.get().fashionItemType,
          newItemId -> {
            final I18nText newButtonText = new I18nText(newItemId == null ?
              Fashigoon.getTranslationKey("menu", "none") :
              FASHION_ITEM_REGISTRY.getEntry(newItemId).get().getNameTranslationKey()
            );
            ((ItemSlotButton)this.getFocus()).setText(newButtonText);
            charData.slots.put(slotId, newItemId);
            },
          this::updateDescriptionText
          ));
      });

      button.onHoverIn(() -> {
        playMenuSound(1);
        this.setFocus(button);
        this.updateDescriptionText(itemId);
      });
    });
  }

  private void unload() {
    this.getStack().popScreen();
    playMenuSound(3);
    gameState_800babc8.indicatorsDisabled_4e3 = false;
    SCRIPTS.resume();
  }

  private ItemSlotButton addButton(final TextComponent textComponent, final int x, final int y, final Runnable onClick) {
    final ItemSlotButton button = this.addControl(new ItemSlotButton(textComponent));
    button.setPos(x, y);
    button.setZ(1);
    button.setWidth(80);
    button.onPressed(onClick::run);

    this.slotButtons.add(button);

    return button;
  }

  private void updateDescriptionText(final RegistryId itemId) {
    if(itemId != null) {
      this.descriptionTranslationKey = FASHION_ITEM_REGISTRY.getEntry(itemId).get().getDescriptionTranslationKey();
    } else {
      this.descriptionTranslationKey = null;
    }
  }
}
