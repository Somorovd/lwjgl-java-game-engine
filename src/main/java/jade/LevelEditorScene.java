package jade;

import jade.renderer.Shader;
import jade.util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class LevelEditorScene extends Scene
{
  private int    vaoId;
  private int    vboId;
  private int    eboId;
  private Shader defaultShader;
  
  //@formatter:off
  private float[] vertexArray = {
    // position           // color
      0.0f, 100.0f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f, // Top left
    100.0f, 100.0f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f, // Top right
    100.0f,   0.0f, 0.0f,    0.0f, 0.0f, 0.0f, 1.0f, // Bottom right
      0.0f,   0.0f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f  // Bottom left
  };
  
  // IMPORTANT: define tris in CCW order
  private int[] elementArray = {
    0, 2, 1, // top right
    0, 3, 2  // bottom left
  };
  //@formatter:on
  
  
  public LevelEditorScene() {}
  
  @Override
  public void init()
  {
    camera        = new Camera(new Vector2f());
    defaultShader = new Shader("assets/shaders/default.glsl");
    defaultShader.compile();
    
    // ===================================
    // Generate VAO, VBO, EBO
    // ===================================
    
    vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
    vertexBuffer.put(vertexArray).flip();
    
    vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
    IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
    elementBuffer.put(elementArray).flip();
    
    eboId = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    
    // Add the vertex attribute pointers
    int positionSize    = 3;
    int colorSize       = 4;
    int floatSizeBytes  = 4;
    int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
    
    // index matches the locations specified in the glsl files
    glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
    glEnableVertexAttribArray(1);
  }
  
  @Override
  public void update(float dt)
  {
    defaultShader.use();
    
    defaultShader.uploadMat4f("uProjMat", camera.getProjectionMatrix());
    defaultShader.uploadMat4f("uViewMat", camera.getViewMatrix());
    defaultShader.uploadFloat("uTime", Time.getTime());
    
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    
    glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    
    // unbind everything
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
    defaultShader.detach();
  }
}
