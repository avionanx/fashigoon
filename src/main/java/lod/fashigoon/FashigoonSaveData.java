package lod.fashigoon;

import legend.game.characters.CharacterData2c;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static lod.fashigoon.Fashigoon.FASHION_SLOT_REGISTRY;

public class FashigoonSaveData {
  private final ArrayList<CharacterFashionData> characterData = new ArrayList<>();

  public void addCharacterFashionData(final CharacterFashionData characterFashionData) {
    this.characterData.add(characterFashionData);
  }

  public CharacterFashionData getCharacterFashionData(final CharacterData2c characterData2c) {
    final Optional<CharacterFashionData> fashionData = this.characterData.stream().filter(entry -> entry.characterData2c == characterData2c).findFirst();
    if(fashionData.isPresent()) {
      return fashionData.get();
    }

    final CharacterFashionData newCharacterFashionData = this.makeCharacterFashionData(characterData2c);
    this.characterData.add(newCharacterFashionData);
    return newCharacterFashionData;
  }

  private CharacterFashionData makeCharacterFashionData(final CharacterData2c characterData2c) {
    final CharacterFashionData characterFashionData = new CharacterFashionData();
    characterFashionData.characterData2c = characterData2c;

    final HashMap<RegistryId, RegistryId> slots = new HashMap<>();
    for(final RegistryId slot : FASHION_SLOT_REGISTRY) {
      slots.put(FASHION_SLOT_REGISTRY.getEntry(slot).getId(), null);
    }

    characterFashionData.slots = slots;
    return characterFashionData;
  }
}
