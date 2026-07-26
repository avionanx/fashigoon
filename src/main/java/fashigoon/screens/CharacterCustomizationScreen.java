package fashigoon.screens;

import fashigoon.CharacterFashionData;
import legend.core.platform.input.InputAction;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.ui.UiBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static fashigoon.Fashigoon.FASHION_DATA_CONFIG;
import static fashigoon.Fashigoon.getTranslationKey;
import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.RENDERER;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.SItem.UI_WHITE;
import static legend.game.SItem.UI_WHITE_CENTERED;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Text.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.sound.Audio.playMenuSound;

public class CharacterCustomizationScreen extends MenuScreen {

  private int currentCharIndex = 0;

  private final HashMap<Integer, CharacterFashionData> characterData;
  private final List<Integer> characterIds;

  private final UiBox contentbox;

  public CharacterCustomizationScreen() {
    playMenuSound(4);
    this.characterData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    this.characterIds = this.characterData.keySet().stream().toList();
    this.currentCharIndex = this.characterIds.getFirst();

    this.contentbox = new UiBox(30, 30, 308, 180);
  }

  @Override
  protected void render() {
    this.contentbox.render();
    renderText(I18n.translate(getTranslationKey("menu", "weapon")), 140, 60, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "outfit")), 140, 90, UI_WHITE);
    renderText(I18n.translate(getTranslationKey("menu", "attachment")), 140, 120, UI_WHITE);
  }

  @Override
  protected InputPropagation inputActionPressed(@NotNull final InputAction action, final boolean repeat) {
    if(action == INPUT_ACTION_MENU_BACK.get() && !repeat) {
      this.deferAction(this::unload);
    }
    return super.inputActionPressed(action, repeat);
  }

  public void unload() {
    this.getStack().popScreen();
    playMenuSound(3);
    gameState_800babc8.indicatorsDisabled_4e3 = false;
    SCRIPTS.resume();
  }
}
