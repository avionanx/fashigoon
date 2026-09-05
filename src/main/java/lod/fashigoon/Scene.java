package lod.fashigoon;

import legend.core.renderer.QueuedModelStandard;
import legend.core.gte.MV;
import legend.core.renderer.Texture;
import legend.game.types.Model124;

import java.util.ArrayList;

import static legend.core.GameEngine.GTE;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Graphics.GsGetLw;
import static legend.game.Graphics.GsSetLightMatrix;
import static legend.game.Graphics.lightColourMatrix_800c3508;
import static legend.game.Graphics.lightDirectionMatrix_800c34e8;


public class Scene {
  private final ArrayList<Model> model;
  private final ArrayList<Texture> textures;

  private Model124 parent;
  private int scriptStateIndex;

  public Scene(final ArrayList<Model> model, final ArrayList<Texture> textures) {
    this.model = model;
    this.textures = textures;
  }

  public void setParent(final Model124 model, final int scriptStateIndex) {
    this.scriptStateIndex = scriptStateIndex;
    this.parent = model;
  }

  public void render() {
    for(int entryIndex = 0; entryIndex < this.model.size(); entryIndex++) {
      final Model entry = this.model.get(entryIndex);

      this.parent.modelParts_00[entry.attachmentInfoStruct.getAttachmentIndex()].coord2_04.flg = 0;

      final MV lw = new MV();
      GsGetLw(this.parent.modelParts_00[entry.attachmentInfoStruct.getAttachmentIndex()].coord2_04, lw);
      GsSetLightMatrix(lw);
      lw
        .scale(1000)
      ;

      entry.mesh.forEach(meshEntry -> {
        final var queuedModel = RENDERER.queueModel(meshEntry.a(), lw, QueuedModelStandard.class)
          .depthOffset(this.parent.zOffset_a0)
          .lightDirection(lightDirectionMatrix_800c34e8)
          .lightColour(lightColourMatrix_800c3508)
          .backgroundColour(GTE.backgroundColour)
          ;

        if(meshEntry.b() != -1) {
          queuedModel.texture(this.textures.get(meshEntry.b()));
        }
      });
    }
  };

  public void unload() {
    this.model.forEach(entry -> entry.mesh.forEach(meshEntry -> meshEntry.a().delete()));
    this.textures.forEach(Texture::delete);
  }

  public int getScriptStateIndex() {
    return this.scriptStateIndex;
  }

  public long getReplacementFlags() {
    int flags = 0;
    for(final Model model : this.model) {
      if(model.attachmentInfoStruct.isReplacement()) {
        flags |= 0x1L << model.attachmentInfoStruct.getAttachmentIndex();
      }
    }
    return flags;
  }
}
