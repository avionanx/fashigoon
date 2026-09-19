package lod.fashigoon.configs;

import legend.core.memory.types.IntRef;
import legend.game.unpacker.ExpandableFileData;
import legend.game.unpacker.FileData;
import lod.fashigoon.CharacterFashionData;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import lod.fashigoon.Fashigoon;
import lod.fashigoon.FashionItem;
import lod.fashigoon.FashionSlot;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import static lod.fashigoon.Fashigoon.FASHION_ITEM_REGISTRY;
import static lod.fashigoon.Fashigoon.FASHION_SLOT_REGISTRY;

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
      final HashMap<FashionSlot, FashionItem> slots = new HashMap<>(slotCount);

      for(int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
        // TODO Slots should not be null, handle null case somehow? in case slots got added afterwards
        final FashionSlot slot = FASHION_SLOT_REGISTRY.getEntry(data.readRegistryId(offset)).get();

        final RegistryId itemRegistry = data.readRegistryId(offset);
        final FashionItem item = itemRegistry == null ? null : FASHION_ITEM_REGISTRY.getEntry(itemRegistry).get();
        slots.put(slot, item);
      }

      charData.slots = slots;

      fashionData.put(charId, charData);
    }

    return fashionData;
  }
}
