package lod.fashigoon;

import legend.game.scripting.ScriptLifecycleEvent;
import lod.fashigoon.configs.FashionConfig;
import lod.fashigoon.screens.CharacterCustomizationScreen;
import legend.core.AddRegistryEvent;
import legend.core.IoHelper;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputActionRegistryEvent;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.ScancodeInputActivation;
import legend.game.EngineState;
import legend.game.characters.CharacterTemplate;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.inventory.WhichMenu;
import legend.game.inventory.screens.MenuStack;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.RenderEvent;
import legend.game.modding.events.battle.CombatantModelLoadedEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.input.InputReleasedEvent;
import legend.game.modding.events.input.RegisterDefaultInputBindingsEvent;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.scripting.ScriptState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.events.Priority;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.Registry;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.REGISTRIES;
import static legend.core.GameEngine.RENDERER;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.Graphics.displayHeight_1f8003e4;
import static legend.game.Graphics.displayWidth_1f8003e0;
import static legend.game.Menus.whichMenu_800bdc38;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.combat.bent.BattleEntity27c.FLAG_DRAGOON;

@Mod(id = Fashigoon.MOD_ID, version = "3.0.0")
public class Fashigoon {
  private static final Logger LOGGER = LogManager.getFormatterLogger(Fashigoon.class);
  public static final String MOD_ID = "fashigoon";

  public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> FASHIGOON_CONFIG_REGISTRAR = new Registrar<>(REGISTRIES.config, MOD_ID);
  public static final RegistryDelegate<FashionConfig> FASHION_DATA_CONFIG = FASHIGOON_CONFIG_REGISTRAR.register("fashigoon_data", FashionConfig::new);
  public static final Registry<FashionSlot> FASHION_SLOT_REGISTRY = new FashionSlotRegistry();
  public static final Registry<FashionItem> FASHION_ITEM_REGISTRY = new FashionItemRegistry();

  public static final Registrar<InputAction, InputActionRegistryEvent> FASHIGOON_INPUT_REGISTRAR = new Registrar<>(REGISTRIES.inputActions, MOD_ID);
  public static final RegistryDelegate<InputAction> FASHIGOON_INPUT_CUSTOMIZATION_MENU = FASHIGOON_INPUT_REGISTRAR.register("fashigoon_customization_menu", InputAction::editable);

  private final MenuStack menuStack = new MenuStack();
  private final ArrayList<Scene> scenes = new ArrayList<>();

  public Fashigoon() {
    EVENTS.register(this);
  }

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  @EventListener
  public void configRegistry(final ConfigRegistryEvent event) {
    FASHIGOON_CONFIG_REGISTRAR.registryEvent(event);
  }


  /**
   * For now, this event handler will initialize empty data for all charIds that exist once game starts.
   * @param event
   */
  @EventListener
  public void newGameHandler(final NewGameEvent event) {
    final var fashionData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    for(int charIndex = 0; charIndex < event.gameState.charData_32c.size(); charIndex++) {
      HashMap<RegistryId, RegistryId> slots = new HashMap<>();
      CharacterFashionData characterFashionData = new CharacterFashionData();
      for(final RegistryId slot : FASHION_SLOT_REGISTRY) {
        slots.put(slot, null);
      }
      characterFashionData.slots = slots;
      fashionData.put(charIndex, characterFashionData);
    }

    debug();
  }

  void debug() {
    final var fashionData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    fashionData.get(0).slots.put(FashigoonSlots.WEAPON.getId(), FASHION_ITEM_REGISTRY.getEntry("tides:glowstick").getId());
    CONFIG.setConfig(FASHION_DATA_CONFIG.get(), fashionData);
  }

  @EventListener
  public void combatantModelLoadedHandler(final CombatantModelLoadedEvent event) {
    if(event.combatant.charSlot_19c == -1) return;

    final PlayerBattleEntity player = SCRIPTS.getObject(6 + event.combatant.charSlot_19c, PlayerBattleEntity.class);
    final ScriptState state = SCRIPTS.getState(6 + event.combatant.charSlot_19c);
    final int stateIndex = 6 + event.combatant.charSlot_19c;
    final CharacterTemplate template = player.character.template;
    final boolean isDragoon = (state.getStor(0x7) & FLAG_DRAGOON) != 0;

    final var fashionData = CONFIG.getConfig(FASHION_DATA_CONFIG.get());
    final CharacterFashionData charData = fashionData.get(player.charId_272);
    final AssetLoader loader = new AssetLoader();
    if(charData != null) {
      if(charData.slots.get(FashigoonSlots.WEAPON.getId()) != null) {
        final Path assetPath = Path.of(
          "mods", "fashigoon", "collections",
          charData.slots.get(FashigoonSlots.WEAPON.getId()).modId(),
          charData.slots.get(FashigoonSlots.WEAPON.getId()).entryId() + ".glb"
        ).toAbsolutePath();
        final Scene scene = loader.loadScene(assetPath);
        player.model_148.partInvisible_f4 |= scene.getReplacementFlags();
        scene.setParent(event.model, stateIndex);
        this.scenes.add(scene);
      }
    }
  }

  @EventListener
  public void registerFashionItemEventHandler(final RegisterFashionItemEvent event) {
    LOGGER.info("Registering collections");
    final Path base = Path.of("mods", "fashigoon", "collections").toAbsolutePath();
    final File[] collections = base.toFile().listFiles();
    for(final File collectionRoot : Objects.requireNonNull(collections)) {
      final String collectionName = collectionRoot.getName();
      LOGGER.info("Found collection: %s" , collectionName);
      final Path csvPath = collectionRoot.toPath().resolve("config.csv");
      try {
        final List<String[]> lines = IoHelper.loadCsvFile(csvPath);
        for(final String[] line : lines) {
          event.register(new RegistryId(collectionName, line[0]), new FashionItem(FASHION_SLOT_REGISTRY.getEntry(line[1]).get()));
        }
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @EventListener
  public void registerFashionSlotEventHandler(final RegisterFashionSlotEvent event) {
    FashigoonSlots.register(event);
  }

  @EventListener
  public void registerRegistries(final AddRegistryEvent event) {
    event.addRegistry(FASHION_SLOT_REGISTRY, RegisterFashionSlotEvent::new);
    event.addRegistry(FASHION_ITEM_REGISTRY, RegisterFashionItemEvent::new);
  }

  @EventListener
  public void inputReleasedHandler(final InputReleasedEvent event) {
    if(whichMenu_800bdc38 != WhichMenu.NONE_0) return;

    if(event.action == FASHIGOON_INPUT_CUSTOMIZATION_MENU.get() && !SCRIPTS.isPaused() && !gameState_800babc8.indicatorsDisabled_4e3) {
      SCRIPTS.pause();
      gameState_800babc8.indicatorsDisabled_4e3 = true;
      this.menuStack.pushScreen(new CharacterCustomizationScreen());
    }
  }

  @EventListener
  public void registerInputActions(final InputActionRegistryEvent event) {
    FASHIGOON_INPUT_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void registerInput(final RegisterDefaultInputBindingsEvent event) {
    event.add(FASHIGOON_INPUT_CUSTOMIZATION_MENU.get(), new ScancodeInputActivation(InputKey.P));
  }

  @EventListener
  public void renderLoop(final RenderEvent event) {
    this.menuStack.render();
  }

  @EventListener
  public void onScriptLifecycle(final ScriptLifecycleEvent event) {
    for(int i = 0; i < this.scenes.size(); i++) {
      final Scene scene = this.scenes.get(i);

      if(scene.getScriptStateIndex() == event.scriptIndex) {
        if(event.getLifecycle() == ScriptLifecycleEvent.Lifecycle.POST_RENDER_CALLBACK) {
          scene.render();
        } else if(event.getLifecycle() == ScriptLifecycleEvent.Lifecycle.PRE_DEALLOCATE) {
          scene.unload();
        }
      }
    }
  }

  public static String getTranslationKey(final String... args) {
    return MOD_ID + '.' + String.join(".", args);
  }

  public static float getExtraWidth() {
    final boolean widescreen = RENDERER.getRenderMode() == EngineState.RenderMode.PERSPECTIVE && CONFIG.getConfig(CoreMod.ALLOW_WIDESCREEN_CONFIG.get());
    final float fullWidth;
    if(widescreen) {
      fullWidth = Math.max(displayWidth_1f8003e0, RENDERER.window().getWidth() / (float)RENDERER.window().getHeight() * displayHeight_1f8003e4);
    } else {
      fullWidth = displayWidth_1f8003e0;
    }

    return fullWidth - displayWidth_1f8003e0;
  }
}
