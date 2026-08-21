package lod.fashigoon.configs;

import legend.core.memory.types.IntRef;
import legend.game.unpacker.ExpandableFileData;
import legend.game.unpacker.FileData;
import lod.fashigoon.CharacterFashionData;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FashionConfig extends ConfigEntry<HashMap<Integer, CharacterFashionData>> {
  public FashionConfig() {
    super(new HashMap<>(), ConfigStorageLocation.SAVE, ConfigCategory.OTHER, FashionConfig::serialize, FashionConfig::deserialize);
  }

  private static byte[] serialize(final HashMap<Integer, CharacterFashionData> fashionData) {
    final FileData data = new ExpandableFileData(fashionData.size() * 6);
    final IntRef offset = new IntRef();

    data.writeVarInt(offset, fashionData.size());
    fashionData.entrySet().forEach(entry -> {
      data.writeVarInt(offset, entry.getKey());

      data.writeRegistryId(offset, entry.getValue().weaponSlot);
      data.writeRegistryId(offset, entry.getValue().outfitSlot);
      data.writeRegistryId(offset, entry.getValue().attachment1);
      data.writeRegistryId(offset, entry.getValue().attachment2);
      data.writeRegistryId(offset, entry.getValue().attachment3);
    });

    return data.getBytes();
  }

  private static HashMap<Integer, CharacterFashionData> deserialize(final byte[] in) {
    final HashMap<Integer, CharacterFashionData> fashionData = new HashMap<>();
    final FileData data = new FileData(in);
    final IntRef offset = new IntRef();

    final int count = data.readVarInt(offset);

    for(int i = 0; i < count; i++) {
      final CharacterFashionData charData = new CharacterFashionData();

      final int charId = data.readVarInt(offset);
      charData.weaponSlot = data.readRegistryId(offset);
      charData.outfitSlot = data.readRegistryId(offset);
      charData.attachment1 = data.readRegistryId(offset);
      charData.attachment2 = data.readRegistryId(offset);
      charData.attachment3 = data.readRegistryId(offset);

      fashionData.put(charId, charData);
    }

    return fashionData;
  }
}
