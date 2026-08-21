package lod.fashigoon;

import org.legendofdragoon.modloader.registries.MutableRegistry;

public class FashionSlotRegistry extends MutableRegistry<FashionSlot> {
  public FashionSlotRegistry() {
    super(Fashigoon.id("fashion_slot"));
  }
}
