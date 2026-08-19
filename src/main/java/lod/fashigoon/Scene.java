package lod.fashigoon;

import legend.core.QueuedModelStandard;
import legend.core.Tuple;
import legend.core.gte.GsCOORDINATE2;
import legend.core.gte.MV;
import legend.core.opengl.Obj;
import legend.core.opengl.Texture;
import legend.game.types.Model124;

import java.util.ArrayList;

import static legend.core.GameEngine.GTE;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Graphics.GsGetLw;
import static legend.game.Graphics.GsSetLightMatrix;
import static legend.game.Graphics.lightColourMatrix_800c3508;
import static legend.game.Graphics.lightDirectionMatrix_800c34e8;


public class Scene {
  private final ArrayList<Tuple<Obj, Integer>> mesh;
  private final Texture texture;

  private Model124 parent;

  public Scene(final ArrayList<Tuple<Obj, Integer>> mesh, final Texture texture) {
    this.mesh = mesh;
    this.texture = texture;
  }

  public void reparent(final Model124 model) {
    this.parent = model;
  }

  public void render() {
    for(int entryIndex = 0; entryIndex < this.mesh.size(); entryIndex++) {
      final Tuple<Obj, Integer> entry = this.mesh.get(entryIndex);

      this.parent.modelParts_00[entry.b()].coord2_04.flg = 0;

      final MV lw = new MV();
      GsGetLw(this.parent.modelParts_00[entry.b()].coord2_04, lw);
      GsSetLightMatrix(lw);

      final var queuedModel = RENDERER.queueModel(entry.a(), lw, QueuedModelStandard.class)
        .depthOffset(this.parent.zOffset_a0)
        .lightDirection(lightDirectionMatrix_800c34e8)
        .lightColour(lightColourMatrix_800c3508)
        .backgroundColour(GTE.backgroundColour)
        ;

      if(this.texture != null) {
        queuedModel.texture(this.texture);
      }
    }
  };

  public void delete() {
    this.mesh.forEach(entry -> entry.a().delete());

    if(this.texture != null) {
      this.texture.delete();
    }
  }
}
