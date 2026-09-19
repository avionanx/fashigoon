package lod.fashigoon;

import legend.core.Tuple;
import legend.core.gpu.Bpp;
import legend.core.renderer.Obj;
import legend.core.renderer.PolyBuilder;
import legend.core.renderer.Texture;
import legend.core.renderer.TextureDataFormat;
import legend.core.renderer.VertexOrder;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIMetaData;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public class AssetLoader {
  public AssetLoader() {

  }

  public Scene loadScene(final Path path) {
    final ArrayList<Texture> textures = new ArrayList<>();
    final ArrayList<Obj> meshes = new ArrayList<>();
    final ArrayList<Model> models = new ArrayList<>();

    final AIScene scene = Assimp.aiImportFile(path.toString(), 0);
    // Texture
    for(int textureIndex = 0; textureIndex < scene.mNumTextures(); textureIndex++) {
      final AITexture AItexture = AITexture.create(scene.mTextures().get(textureIndex));
      final ByteBuffer imageBuffer = AItexture.pcDataCompressed();
      try(final MemoryStack stack = stackPush()) {
        final IntBuffer w = stack.mallocInt(1);
        final IntBuffer h = stack.mallocInt(1);
        final IntBuffer comp = stack.mallocInt(1);

        final ByteBuffer data = stbi_load_from_memory(imageBuffer, w, h, comp, 3);
        if(data == null) {
          throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        textures.add(
          Texture.create(path.toString(),
            textureBuilder -> {
            textureBuilder.data(data, w.get(0), h.get(0));
            textureBuilder.dataFormat(TextureDataFormat.RGB);
          })
        );

        stbi_image_free(data);
      }
    }

    // Materials
    final ArrayList<Vector4f> materialColors = new ArrayList<>();
    // Need to check material texture counts to set BPP_24
    final ArrayList<Integer> materialTextureCounts = new ArrayList<>();
    final ArrayList<Integer> materialTextureIndices = new ArrayList<>();
    if(scene.mNumMaterials() != 0) {
      for(int materialIndex = 0; materialIndex < scene.mNumMaterials(); materialIndex++) {
        final AIMaterial material = AIMaterial.create(scene.mMaterials().get(materialIndex));

        final AIColor4D color = AIColor4D.create();
        Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_BASE_COLOR, Assimp.aiTextureType_NONE, 0, color);
        materialColors.add(new Vector4f(color.r(), color.g(), color.b(), color.a()));

        final int materialTextureCount = Assimp.aiGetMaterialTextureCount(material, Assimp.aiTextureType_DIFFUSE);
        materialTextureCounts.add(materialTextureCount);
        try(final AIString texturePath = AIString.calloc()) {
          for(int textureIndex = 0; textureIndex < materialTextureCount; textureIndex++) {
            Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, textureIndex, texturePath, (IntBuffer)null, null, null, null, null, null);
            materialTextureIndices.add(Integer.parseInt(texturePath.dataString().substring(1)));
          }
        }

      }
    }

    final ArrayList<Integer> meshMaterialIndices = new ArrayList<>();
    // Mesh
    for(int meshIndex = 0; meshIndex < scene.mNumMeshes(); meshIndex++) {
      final PolyBuilder builder = new PolyBuilder(path.toString(), VertexOrder.TRIANGLES);

      try(final AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex))) {
        final int materialIndex = mesh.mMaterialIndex();
        meshMaterialIndices.add(materialIndex);
        if(materialTextureCounts.get(materialIndex) > 0) {
          builder.bpp(Bpp.BITS_24);
        }

        final AIFace.Buffer faces = mesh.mFaces();
        final AIVector3D.Buffer vertices = mesh.mVertices();
        final AIVector3D.Buffer normals = mesh.mNormals();
        final AIVector3D.Buffer uvs = mesh.mTextureCoords(0);

        while(faces.hasRemaining()) {
          final AIFace face = faces.get();

          for(int i = 0; i < face.mNumIndices(); i++) {
            final int vertexIndex = face.mIndices().get(i);
            final AIVector3D vertex = vertices.get(vertexIndex);
            final AIVector3D normal = normals.get(vertexIndex);
            final AIVector3D uv = uvs.get(vertexIndex);

            builder.addVertex(vertex.x(), vertex.y(), vertex.z());
            builder.normal(normal.x(), normal.y(), normal.z());
            final Vector4f colour = materialColors.get(mesh.mMaterialIndex());
            builder.rgb(colour.x * 2.0f, colour.y * 2.0f, colour.z * 2.0f);
            if(materialTextureCounts.get(materialIndex) > 0) {
              builder.uv(uv.x(), 1.0f - uv.y());
            }
          }
        }

        meshes.add(builder.build());
      }
    }

    final AINode root = scene.mRootNode();
    final int nodeCount = root.mNumChildren();
    if(nodeCount == 0) {
      // There is a single node
      final Model model = new Model();

      final HashMap<String, Object> nodeExtras = this.getNodeExtras(root);
      model.attachmentInfoStruct = new AttachmentInfoStruct((int)(long)nodeExtras.get("attachment"), (boolean)nodeExtras.getOrDefault("replacement", false));

      final ArrayList<Tuple<Obj, Integer>> meshList = new ArrayList<>();
      for(int meshIndex = 0; meshIndex < root.mNumMeshes(); meshIndex++) {
        final int sceneMeshIndex = root.mMeshes().get(meshIndex);
        int meshTextureIndex;
        if(materialTextureCounts.get(meshMaterialIndices.get(sceneMeshIndex)) > 0) {
          meshTextureIndex = materialTextureIndices.get(meshMaterialIndices.get(sceneMeshIndex));
        } else {
          meshTextureIndex = -1;
        }
        meshList.add(new Tuple<>(meshes.get(sceneMeshIndex), meshTextureIndex));
      }
      model.mesh = meshList;
      models.add(model);
    } else {
      // Multiple nodes, skip root node and walk children
      for(int nodeIndex = 0; nodeIndex < root.mNumChildren(); nodeIndex++) {
        try(final AINode childNode = AINode.create(root.mChildren().get(nodeIndex))) {
          final Model model = new Model();

          final HashMap<String, Object> nodeExtras = this.getNodeExtras(childNode);
          model.attachmentInfoStruct = new AttachmentInfoStruct((int)(long)nodeExtras.get("attachment"), (boolean)nodeExtras.getOrDefault("replacement", false));

          final ArrayList<Tuple<Obj, Integer>> meshList = new ArrayList<>();
          for(int meshIndex = 0; meshIndex < childNode.mNumMeshes(); meshIndex++) {
            final int sceneMeshIndex = childNode.mMeshes().get(meshIndex);
            int meshTextureIndex;
            if(materialTextureCounts.get(meshMaterialIndices.get(sceneMeshIndex)) > 0) {
              meshTextureIndex = materialTextureIndices.get(meshMaterialIndices.get(sceneMeshIndex));
            } else {
              meshTextureIndex = -1;
            }
            meshList.add(new Tuple<>(meshes.get(sceneMeshIndex), meshTextureIndex));
          }
          model.mesh = meshList;
          models.add(model);
        }
      }
    }

    Assimp.aiReleaseImport(scene);

    return new Scene(models, textures);
  }

  private HashMap<String, Object> getNodeExtras(final AINode node) {
    final HashMap<String, Object> extras = new HashMap<>();
    final AIMetaData metaData = node.mMetadata();

    for(int extraIndex = 0; extraIndex < metaData.mNumProperties(); extraIndex++) {
      final String key = metaData.mKeys().get(extraIndex).dataString();
      final int dataType = metaData.mValues().get(extraIndex).mType();
      final long dataAddress = MemoryUtil.memAddress(metaData.mValues().get(extraIndex).mData(1));

      final Object data = switch(dataType) {
        case Assimp.AI_BOOL -> MemoryUtil.memGetBoolean(dataAddress);
        case Assimp.AI_INT32, Assimp.AI_UINT32 -> MemoryUtil.memGetInt(dataAddress);
        case Assimp.AI_INT64, Assimp.AI_UINT64 -> MemoryUtil.memGetLong(dataAddress);
        default -> throw new RuntimeException("Unimplemented extra type");
      };

      extras.put(key, data);
    }

    return extras;
  }
}
