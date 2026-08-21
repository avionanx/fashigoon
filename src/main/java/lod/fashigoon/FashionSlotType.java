package lod.fashigoon;

import java.util.Arrays;

public enum FashionSlotType {
  WEAPON,
  OUTFIT,
  ATTACHMENT;

  public static FashionSlotType get(final String param) {
    return Arrays.stream(FashionSlotType.values())
      .filter(type -> type.name().toLowerCase().equals(param))
      .findFirst().get();
  }
}
