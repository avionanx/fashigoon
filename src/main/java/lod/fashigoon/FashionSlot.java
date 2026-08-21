package lod.fashigoon;

import org.legendofdragoon.modloader.registries.RegistryEntry;

public class FashionSlot extends RegistryEntry {
  public final FashionSlotType fashionItemType;

  public FashionSlot(final FashionSlotType fashionItemType) {
    this.fashionItemType = fashionItemType;
  }
}
