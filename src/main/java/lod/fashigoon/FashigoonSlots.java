package lod.fashigoon;

import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

public class FashigoonSlots {
  private FashigoonSlots() { }

  private static final Registrar<FashionSlot, RegisterFashionSlotEvent> REGISTRAR = new Registrar<>(Fashigoon.FASHION_SLOT_REGISTRY, Fashigoon.MOD_ID);

  public static final RegistryDelegate<FashionSlot> WEAPON = REGISTRAR.register("weapon", () -> new FashionSlot(FashionSlotType.WEAPON));
  public static final RegistryDelegate<FashionSlot> OUTFIT = REGISTRAR.register("outfit", () -> new FashionSlot(FashionSlotType.OUTFIT));
  public static final RegistryDelegate<FashionSlot> ATTACHMENT_1 = REGISTRAR.register("attachment_1", () -> new FashionSlot(FashionSlotType.ATTACHMENT));
  public static final RegistryDelegate<FashionSlot> ATTACHMENT_2 = REGISTRAR.register("attachment_2", () -> new FashionSlot(FashionSlotType.ATTACHMENT));
  public static final RegistryDelegate<FashionSlot> ATTACHMENT_3 = REGISTRAR.register("attachment_3", () -> new FashionSlot(FashionSlotType.ATTACHMENT));

  static void register(final RegisterFashionSlotEvent event) {
    REGISTRAR.registryEvent(event);
  }
}
