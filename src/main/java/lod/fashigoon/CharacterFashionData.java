package lod.fashigoon;

import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.Serializable;

public class CharacterFashionData implements Serializable {
  private static final long serialVersionUID = 129348938L;

  public RegistryId weaponSlot;
  public RegistryId outfitSlot;
  public RegistryId attachment1;
  public RegistryId attachment2;
  public RegistryId attachment3;
}
