package lod.fashigoon;

import org.legendofdragoon.modloader.registries.RegistryEntry;

public class FashionItem extends RegistryEntry {
  public final FashionSlotType slotType;

  public FashionItem(final FashionSlotType slotType) {
    this.slotType = slotType;
  }
}
