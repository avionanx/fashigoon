package lod.fashigoon;

import org.legendofdragoon.modloader.registries.RegistryEntry;

public class FashionItem extends RegistryEntry {
  public final FashionItemType fashionItemType;

  public FashionItem(final FashionItemType fashionItemType) {
    this.fashionItemType = fashionItemType;
  }
}
