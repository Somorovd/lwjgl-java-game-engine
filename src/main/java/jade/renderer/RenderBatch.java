package jade.renderer;

import jade.Camera;
import jade.Transform;
import jade.Window;
import jade.components.SpriteRenderer;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class RenderBatch
{
  /*
    Vertex Array
  
    float <--- Pos
    float
    
    float <--- Color
    float
    float
    float
   */
  
  private final int POS_SIZE          = 2;
  private final int COLOR_SIZE        = 4;
  private final int POS_OFFSET        = 0;
  private final int COLOR_OFFSET      = 2 * Float.BYTES;
  private final int VERTEX_SIZE       = 6;
  private final int VERTEX_SIZE_BYTES = 6 * Float.BYTES;
  
  private SpriteRenderer[] sprites;
  private int              numSprites;
  private boolean          hasRoom;
  private float[]          vertices;
  private int              vaoId;
  private int              vboId;
  private int              maxBatchSize;
  private Shader           shader;
  
  public RenderBatch(int maxBatchSize)
  {
    this.maxBatchSize = maxBatchSize;
    this.numSprites   = 0;
    this.hasRoom      = true;
    this.sprites      = new SpriteRenderer[maxBatchSize];
    this.vertices     = new float[maxBatchSize * 4 * VERTEX_SIZE];
    this.shader       = new Shader("assets/shaders/default.glsl");
    
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
  }
  
  public void render()
  {
    // For now, re-buffer all data every frame
    // TODO : re-buffer only changed data
    
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    
    shader.use();
    
    Camera camera = Window.getScene().getCamera();
    shader.uploadMat4f("uProjMat", camera.getProjectionMatrix());
    shader.uploadMat4f("uViewMat", camera.getViewMatrix());
    
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    
    glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
    
    // unbind everything
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
    
    shader.detach();
  }
  
  public void addSprite(SpriteRenderer spr)
  {
    // Add render object
    int index = numSprites;
    sprites[index] = spr;
    numSprites++;
    
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
    SpriteRenderer spr   = sprites[index];
    Transform      t     = spr.gameObject.transform;
    Vector4f       color = spr.getColor();
    
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
      vertices[offset + 0] = t.position.x + dirs[i][0] * t.scale.x;
      vertices[offset + 1] = t.position.y + dirs[i][1] * t.scale.y;
      vertices[offset + 2] = color.x;
      vertices[offset + 3] = color.y;
      vertices[offset + 4] = color.z;
      vertices[offset + 5] = color.w;
      
      offset += VERTEX_SIZE;
    }
  }
  
  public boolean hasRoom()
  {
    return hasRoom;
  }
}
