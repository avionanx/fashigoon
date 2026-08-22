package lod.fashigoon;

public class AttachmentInfoStruct {
  private final int attachmentIndex;
  private final boolean isReplacement;

  public AttachmentInfoStruct(final int attachmentIndex, final boolean isReplacement) {
    this.attachmentIndex = attachmentIndex;
    this.isReplacement = isReplacement;
  }

  public int getAttachmentIndex() {
    return this.attachmentIndex;
  }

  public boolean isReplacement() {
    return this.isReplacement;
  }
}
