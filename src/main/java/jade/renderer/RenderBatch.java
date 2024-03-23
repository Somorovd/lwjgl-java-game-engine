package jade.renderer;

import jade.Camera;
import jade.Transform;
import jade.Window;
import jade.components.SpriteRenderer;
import jade.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class RenderBatch implements Comparable<RenderBatch>
{
  /*
    Vertex Array
  
    Pos        : float x 2
    Color      : float x 4
    tex coords : float x 2
    tex id     : float x 1
   */
  
  private final int POS_SIZE          = 2;
  private final int COLOR_SIZE        = 4;
  private final int TEX_COORD_SIZE    = 2;
  private final int TEX_ID_SIZE       = 1;
  private final int POS_OFFSET        = 0;
  private final int COLOR_OFFSET      = 2 * Float.BYTES;
  private final int TEX_COORD_OFFSET  = 6 * Float.BYTES;
  private final int TEX_ID_OFFSET     = 8 * Float.BYTES;
  private final int VERTEX_SIZE       = 9;
  private final int VERTEX_SIZE_BYTES = 9 * Float.BYTES;
  
  private SpriteRenderer[] sprites;
  private int              numSprites;
  private boolean          hasRoom;
  private float[]          vertices;
  private int              vaoId;
  private int              vboId;
  private int              maxBatchSize;
  private Shader           shader;
  private List<Texture>    textures;
  private int[]            texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
  private int              zIndex;
  
  public RenderBatch(int maxBatchSize, int zIndex)
  {
    this.maxBatchSize = maxBatchSize;
    this.zIndex       = zIndex;
    this.numSprites   = 0;
    this.hasRoom      = true;
    this.sprites      = new SpriteRenderer[maxBatchSize];
    this.vertices     = new float[maxBatchSize * 4 * VERTEX_SIZE];
    this.textures     = new ArrayList<>();
    this.shader       = AssetPool.getShader("assets/shaders/default.glsl");
    
    shader.compile();
  }
  
  public void start()
  {
    // Generate and bind Vertex Array Object
    vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);
    
    // Allocate space for vertices
    vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
    
    // Create and upload indices buffer
    int   eboId   = glGenBuffers();
    int[] indices = generateIndices();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    
    // Enable buffer attribute pointers
    glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
    glEnableVertexAttribArray(0);
    
    glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
    glEnableVertexAttribArray(1);
    
    glVertexAttribPointer(2, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
    glEnableVertexAttribArray(2);
    
    glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
    glEnableVertexAttribArray(3);
  }
  
  public void render()
  {
    boolean shouldRebuffer = false;
    
    for (int i = 0; i < numSprites; i++)
    {
      SpriteRenderer spr = sprites[i];
      if (spr.isDirty())
      {
        loadVertexProperties(i);
        spr.setClean();
        shouldRebuffer = true;
      }
    }
    
    if (shouldRebuffer)
    {
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }
    
    shader.use();
    
    Camera camera = Window.getScene().getCamera();
    shader.uploadMat4f("uProjMat", camera.getProjectionMatrix());
    shader.uploadMat4f("uViewMat", camera.getViewMatrix());
    
    for (int i = 0; i < textures.size(); i++)
    {
      // + 1 because tex 0 is reserved for no texture
      glActiveTexture(GL_TEXTURE0 + i + 1);
      textures.get(i).bind();
    }
    shader.uploadIntArray("uTextures", texSlots);
    
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    
    glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
    
    // unbind everything
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
    
    for (int i = 0; i < textures.size(); i++)
    {
      textures.get(i).unbind();
    }
    
    shader.detach();
  }
  
  public void addSprite(SpriteRenderer spr)
  {
    // Add render object
    int index = numSprites;
    sprites[index] = spr;
    numSprites++;
    
    Texture texture = spr.getTexture();
    if (texture != null && !textures.contains(texture))
    {
      textures.add(texture);
    }
    
    // Add properties to vertices array
    loadVertexProperties(index);
    
    if (numSprites >= maxBatchSize)
    {
      hasRoom = false;
    }
  }
  
  private int[] generateIndices()
  {
    // 6 indices per quad (3 per triangle)
    int[] indices = new int[6 * maxBatchSize];
    
    for (int i = 0; i < maxBatchSize; i++)
    {
      // 0, 1, 2, 3, 4, 5     6, 7, 8, 9, 10, 11    ...
      // 3, 2, 0, 0, 2, 1,    7, 6, 4, 4,  6,  5,   ...
      
      int arrayOffset = 6 * i;
      int quadOffset  = 4 * i;
      
      // tri 1
      indices[arrayOffset + 0] = 3 + quadOffset;
      indices[arrayOffset + 1] = 2 + quadOffset;
      indices[arrayOffset + 2] = 0 + quadOffset;
      // tri 2
      indices[arrayOffset + 3] = 0 + quadOffset;
      indices[arrayOffset + 4] = 2 + quadOffset;
      indices[arrayOffset + 5] = 1 + quadOffset;
    }
    
    return indices;
  }
  
  private void loadVertexProperties(int index)
  {
    SpriteRenderer spr       = sprites[index];
    Transform      transform = spr.gameObject.transform;
    Vector4f       color     = spr.getColor();
    Texture        texture   = spr.getTexture();
    Vector2f[]     texCoords = spr.getTexCoords();
    
    // Find offset within array (4 vertices per sprite)
    int offset = index * 4 * VERTEX_SIZE;
    
    // Add vertices to VAO
    float[][] dirs = {
      {1.0f, 1.0f},
      {1.0f, 0.0f},
      {0.0f, 0.0f},
      {0.0f, 1.0f}
    };
    for (int i = 0; i < 4; i++)
    {
      // position
      vertices[offset + 0] = transform.position.x + dirs[i][0] * transform.scale.x;
      vertices[offset + 1] = transform.position.y + dirs[i][1] * transform.scale.y;
      
      // color
      vertices[offset + 2] = color.x;
      vertices[offset + 3] = color.y;
      vertices[offset + 4] = color.z;
      vertices[offset + 5] = color.w;
      
      // texture coords (uv)
      vertices[offset + 6] = texCoords[i].x;
      vertices[offset + 7] = texCoords[i].y;
      
      // texture id
      // + 1 because tex 0 is reserved for no texture
      vertices[offset + 8] = Math.max(0, textures.indexOf(texture) + 1);
      
      
      offset += VERTEX_SIZE;
    }
  }
  
  public boolean hasRoom()
  {
    return hasRoom;
  }
  
  public boolean hasTextureRoom() {return textures.size() < 8;}
  
  public boolean hasTexture(Texture tex)
  {
    return textures.contains(tex);
  }
  
  public int getzIndex() {return zIndex;}
  
  @Override
  public int compareTo(RenderBatch o)
  {
    return zIndex - o.getzIndex();
  }
}
