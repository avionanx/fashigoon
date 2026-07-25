package fashigoon;

import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.Serializable;

public class CharacterFashionData implements Serializable {
  private static final long serialVersionUID = 129348938L;

  private RegistryId weaponSlot;
  private RegistryId outfitSlot;
  private RegistryId attachment1;
  private RegistryId attachment2;
  private RegistryId attachment3;
}
