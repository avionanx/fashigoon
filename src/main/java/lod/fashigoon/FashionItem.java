package lod.fashigoon;

import legend.game.characters.CharacterTemplate;
import org.legendofdragoon.modloader.registries.RegistryEntry;

public class FashionItem extends RegistryEntry {
  public final FashionSlotType slotType;
  public final CharacterTemplate characterType;

  public FashionItem(final FashionSlotType slotType, final CharacterTemplate characterType) {
    this.slotType = slotType;
    this.characterType = characterType;
  }

  @Override
  public String getTranslationKey() {
    return this.getTranslationKey("name");
  }

  @Override
  public String getTranslationKey(final String type) {
    final String[] keys = this.getRegistryId().entryId().split("_", 2);
    return keys[0] + '.' + keys[1] + '.' + type;
  }

  public String getNameTranslationKey() {
    return this.getTranslationKey();
  }

  public String getDescriptionTranslationKey() {
    return this.getTranslationKey("description");
  }
}
