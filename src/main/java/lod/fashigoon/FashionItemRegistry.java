package lod.fashigoon;

import org.legendofdragoon.modloader.registries.MutableRegistry;

public class FashionItemRegistry extends MutableRegistry<FashionItem> {
  public FashionItemRegistry() {
    super(Fashigoon.id("fashion_item"));
  }
}
