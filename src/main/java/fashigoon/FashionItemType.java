package fashigoon;

import java.util.Arrays;

public enum FashionItemType {
  WEAPON,
  OUTFIT,
  ATTACHMENT;

  public static FashionItemType get(final String param) {
    return Arrays.stream(FashionItemType.values())
      .filter(type -> type.name().toLowerCase().equals(param))
      .findFirst().get();
  }
}
