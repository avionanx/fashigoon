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
    final FileData data = new ExpandableFileData(fashionData.size() * (fashionData.size() * 5));
    final IntRef offset = new IntRef();

    data.writeVarInt(offset, fashionData.size());
    fashionData.entrySet().forEach(entry -> {
      data.writeVarInt(offset, entry.getKey());
      data.writeVarInt(offset, entry.getValue().slots.size());

      entry.getValue().slots.forEach((slot, item) -> {
        data.writeRegistryId(offset, slot);
        data.writeRegistryId(offset, item);
      });
    });

    return data.getBytes();
  }

  private static HashMap<Integer, CharacterFashionData> deserialize(final byte[] in) {
    final HashMap<Integer, CharacterFashionData> fashionData = new HashMap<>();
    final FileData data = new FileData(in);
    final IntRef offset = new IntRef();

    final int charCount = data.readVarInt(offset);

    for(int i = 0; i < charCount; i++) {
      final CharacterFashionData charData = new CharacterFashionData();

      final int charId = data.readVarInt(offset);
      final int slotCount = data.readVarInt(offset);
      final HashMap<RegistryId, RegistryId> slots = new HashMap<>(slotCount);

      for(int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
        final RegistryId slot = data.readRegistryId(offset);
        final RegistryId item = data.readRegistryId(offset);

        slots.put(slot, item);
      }

      charData.slots = slots;

      fashionData.put(charId, charData);
    }

    return fashionData;
  }
}
