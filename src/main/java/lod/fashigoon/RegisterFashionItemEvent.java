package lod.fashigoon;

import org.legendofdragoon.modloader.events.registries.RegistryEvent;
import org.legendofdragoon.modloader.registries.MutableRegistry;

public class RegisterFashionItemEvent extends RegistryEvent.Register<FashionItem> {
  public RegisterFashionItemEvent(final MutableRegistry<FashionItem> registry) {
    super(registry);
  }
}
