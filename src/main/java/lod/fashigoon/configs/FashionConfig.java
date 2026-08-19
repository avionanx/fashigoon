package lod.fashigoon.configs;

import lod.fashigoon.CharacterFashionData;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

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
    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
      objectOutputStream.writeObject(fashionData);
      return byteArrayOutputStream.toByteArray();
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static HashMap<Integer, CharacterFashionData> deserialize(final byte[] data) {
    try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data); final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
      return (HashMap<Integer, CharacterFashionData>) objectInputStream.readObject();
    } catch(final IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
