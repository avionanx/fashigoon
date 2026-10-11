package lod.fashigoon;

import legend.game.characters.CharacterTemplate;
import org.legendofdragoon.modloader.registries.RegistryEntry;

import java.util.ArrayList;

public class FashionItem extends RegistryEntry {
  public final FashionSlotType slotType;
  public final ArrayList<CharacterTemplate> characterTypes;

  public FashionItem(final FashionSlotType slotType, final ArrayList<CharacterTemplate> characterTypes) {
    this.slotType = slotType;
    this.characterTypes = characterTypes;
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
