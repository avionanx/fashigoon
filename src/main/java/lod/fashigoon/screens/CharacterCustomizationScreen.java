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
import lod.fashigoon.controls.ItemSlotButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static lod.fashigoon.Fashigoon.FASHION_DATA_CONFIG;
import static lod.fashigoon.Fashigoon.getExtraWidth;
import static lod.fashigoon.Fashigoon.getTranslationKey;
import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;

public class CharacterCustomizationScreen extends MenuScreen {
  public static final FontOptions UI_WHITE = new FontOptions().colour(TextColour.WHITE).size(0.5f);


  private final HashMap<Integer, CharacterFashionData> characterData;
  private final List<Integer> characterIds;

  private int currentCharIndex = 0;

  private final List<ItemSlotButton> slotButtons = new ArrayList<>();
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
    renderText(I18n.translate(getTranslationKey("menu", "weapon")), 58, 68, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "outfit")), 58, 94, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "attachment")), 58, 120, UI_WHITE);
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
    AtomicInteger y = new AtomicInteger();
    charData.slots.forEach((slot, item) -> {

      final I18nText buttonText = new I18nText(item == null ? Fashigoon.getTranslationKey("menu", "none") : item.getNameTranslationKey());
      this.addButton(buttonText, 58, 75 + y.get(), () -> {});
      y.addAndGet(30);
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

    return button;
  }


}
