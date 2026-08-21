package lod.fashigoon;

import org.legendofdragoon.modloader.registries.RegistryEntry;

public class FashionItem extends RegistryEntry {
  public final FashionSlot fashionSlot;

  public FashionItem(final FashionSlot fashionSlot) {
    this.fashionSlot = fashionSlot;
  }
}
