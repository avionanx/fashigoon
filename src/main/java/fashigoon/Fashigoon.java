package fashigoon;

import fashigoon.configs.FashionConfig;
import fashigoon.screens.CharacterCustomizationScreen;
import legend.core.AddRegistryEvent;
import legend.core.IoHelper;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputActionRegistryEvent;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.ScancodeInputActivation;
import legend.game.inventory.WhichMenu;
import legend.game.inventory.screens.MenuStack;
import legend.game.modding.events.RenderEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.input.InputReleasedEvent;
import legend.game.modding.events.input.RegisterDefaultInputBindingsEvent;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.Registry;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.REGISTRIES;
import static legend.core.GameEngine.SCRIPTS;
import static legend.game.Menus.whichMenu_800bdc38;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

@Mod(id = Fashigoon.MOD_ID, version = "3.0.0")
public class Fashigoon {
  private static final Logger LOGGER = LogManager.getFormatterLogger(Fashigoon.class);
  public static final String MOD_ID = "fashigoon";

  public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> FASHIGOON_CONFIG_REGISTRAR = new Registrar<>(REGISTRIES.config, MOD_ID);
  public static final RegistryDelegate<FashionConfig> FASHION_DATA_CONFIG = FASHIGOON_CONFIG_REGISTRAR.register("fashigoon_data", FashionConfig::new);
  private static final Registry<FashionItem> FASHION_ITEM_REGISTRY = new FashionItemRegistry();
  private static final Registrar<FashionItem, RegisterFashionItemEvent> FASHION_ITEM_REGISTRAR = new Registrar<>(FASHION_ITEM_REGISTRY, MOD_ID);

  public static final Registrar<InputAction, InputActionRegistryEvent> FASHIGOON_INPUT_REGISTRAR = new Registrar<>(REGISTRIES.inputActions, MOD_ID);
  public static final RegistryDelegate<InputAction> FASHIGOON_INPUT_CUSTOMIZATION_MENU = FASHIGOON_INPUT_REGISTRAR.register("fashigoon_customization_menu", InputAction::editable);

  private final MenuStack menuStack = new MenuStack();

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
    for(final Integer id : event.gameState.charIds_88) {
      fashionData.put(id, new CharacterFashionData());
    }
  }

  /**
   * This event should walk mods/fashigoon/collections and register each collection found there
   * @param event
   */
  @EventListener
  public void gameLoadedHandler(final GameLoadedEvent event) {
    this.loadCollections();
  }

  private void loadCollections() {
    LOGGER.info("Registering collections");
    final Path base = Path.of("mods", "fashigoon", "collections").toAbsolutePath();
    final File[] collections = base.toFile().listFiles();
    for(final File collectionRoot : Objects.requireNonNull(collections)) {
      final String collectionName = collectionRoot.getName();
      LOGGER.info("Found collection: %s" , collectionName);
      final Path csvPath = collectionRoot.toPath().resolve("%s.csv".formatted(collectionName));
      try {
        final List<String[]> lines = IoHelper.loadCsvFile(csvPath);
        for(final String[] line : lines) {
          FASHION_ITEM_REGISTRAR.register("%s-%s".formatted(collectionName, line[0]), () -> new FashionItem(FashionItemType.get(line[1])));
        }
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @EventListener
  public void registerRegistries(final AddRegistryEvent event) {
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

  public static String getTranslationKey(final String... args) {
    return MOD_ID + '.' + String.join(".", args);
  }
}
