package lod.fashigoon;

import org.legendofdragoon.modloader.events.registries.RegistryEvent;
import org.legendofdragoon.modloader.registries.MutableRegistry;

public class RegisterFashionSlotEvent extends RegistryEvent.Register<FashionSlot> {
  public RegisterFashionSlotEvent(final MutableRegistry<FashionSlot> registry) {
    super(registry);
  }
}
