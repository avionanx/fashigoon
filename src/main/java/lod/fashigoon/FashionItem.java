package lod.fashigoon;

import legend.game.characters.CharacterTemplate;
import legend.game.inventory.ItemStack;
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
    return this.getRegistryId().modId() + '.' + this.getRegistryId().entryId() + '.' + type;
  }

  public String getNameTranslationKey() {
    return this.getTranslationKey();
  }

  public String getDescriptionTranslationKey() {
    return this.getTranslationKey("description");
  }
}
