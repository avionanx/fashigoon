package lod.fashigoon.screens;

import lod.fashigoon.CharacterFashionData;
import lod.fashigoon.FashionItem;
import lod.fashigoon.FashionSlotType;
import legend.core.platform.input.InputAction;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.ui.UiBox;
import org.jetbrains.annotations.NotNull;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static lod.fashigoon.Fashigoon.FASHION_DATA_CONFIG;
import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;
import static lod.fashigoon.Fashigoon.getExtraWidth;
import static lod.fashigoon.Fashigoon.getTranslationKey;
import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.SItem.UI_WHITE;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;

public class CharacterCustomizationScreen extends MenuScreen {
  private final HashMap<Integer, CharacterFashionData> characterData;
  private final List<Integer> characterIds;

  private int currentCharIndex = 0;

  private final List<Button> menuButtons = new ArrayList<>();
  private final UiBox contentBox;
  private final int extraWidth;

  public CharacterCustomizationScreen() {
    this.extraWidth = (int)getExtraWidth();

    playMenuSound(4);
    this.characterData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    this.characterIds = this.characterData.keySet().stream().toList();
    this.currentCharIndex = this.characterIds.getFirst();

    this.contentBox = new UiBox(40, 40, 288, 160);

    this.loadCharacterDataAndButtons(0);
  }

  @Override
  protected void render() {
    this.contentBox.render();
    renderText(I18n.translate(getTranslationKey("menu", "weapon")), 50, 50, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "outfit")), 50, 80, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "attachment")), 50, 110, UI_WHITE);
  }

  @Override
  protected InputPropagation inputActionPressed(@NotNull final InputAction action, final boolean repeat) {
    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.deferAction(this::unload);
    }
    return super.inputActionPressed(action, repeat);
  }

  private void loadCharacterDataAndButtons(final int charIndex) {
    final var charData = CONFIG.getConfig(FASHION_DATA_CONFIG.get()).get(charIndex);
  }
/*
  private void showFashionInventory(final FashionSlotType fashionItemType, final Button button, RegistryId registryId) {
    final List<FashionItem> fashionItems = new ArrayList<>();
    for(final RegistryId id : FASHION_ITEM_REGISTRY) {
      final FashionItem item = FASHION_ITEM_REGISTRY.getEntry(id).get();
      if(item.fashionItemType == fashionItemType) {
        fashionItems.add(item);
      }
    }

  }
  */
  private void unload() {
    this.getStack().popScreen();
    playMenuSound(3);
    gameState_800babc8.indicatorsDisabled_4e3 = false;
    SCRIPTS.resume();
  }
}
