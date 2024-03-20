package jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class LevelEditorScene extends Scene
{
  
  private String vertexShaderSrc   = "#version 330 core\n" +
    "\n" +
    "layout (location=0) in vec3 aPos;\n" +
    "layout (location=1) in vec4 aColor;\n" +
    "\n" +
    "out vec4 fColor;\n" +
    "\n" +
    "void main()\n" +
    "{\n" +
    "    fColor = aColor;\n" +
    "    gl_Position = vec4(aPos, 1.0);\n" +
    "}";
  private String fragmentShaderSrc = "#version 330 core\n" +
    "\n" +
    "in vec4 fColor;\n" +
    "\n" +
    "out vec4 color;\n" +
    "\n" +
    "void main()\n" +
    "{\n" +
    "    color = fColor;\n" +
    "}";
  
  private int vertexId;
  private int fragmentId;
  private int shaderProgram;
  private int vaoId;
  private int vboId;
  private int eboId;
  
  //@formatter:off
  private float[] vertexArray = {
    // position           // color
    -0.5f,  0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f, // Top left
     0.5f,  0.5f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f, // Top right
     0.5f, -0.5f, 0.0f,    0.0f, 0.0f, 0.0f, 1.0f, // Bottom right
    -0.5f, -0.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f  // Bottom left
  };
  
  // IMPORTANT: define tris in CCW order
  private int[] elementArray = {
    0, 2, 1, // top right
    0, 3, 2  // bottom left
  };
  //@formatter:on
  
  
  public LevelEditorScene()
  {
  }
  
  @Override
  public void init()
  {
    // ===================================
    // Compile shaders
    // ===================================
    
    vertexId = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexId, vertexShaderSrc);
    glCompileShader(vertexId);
    
    fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentId, fragmentShaderSrc);
    glCompileShader(fragmentId);
    
    // Check for errors in compilation
    int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compiltion failed");
      System.out.println(glGetShaderInfoLog(vertexId, len));
      assert false : "";
    }
    
    // Check for errors in compilation
    success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compiltion failed");
      System.out.println(glGetShaderInfoLog(fragmentId, len));
      assert false : "";
    }
    
    // Link shaders
    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexId);
    glAttachShader(shaderProgram, fragmentId);
    glLinkProgram(shaderProgram);
    
    // Check for linking errors
    success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed");
      System.out.println(glGetProgramInfoLog(shaderProgram, len));
      assert false : "";
    }
    
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
    glUseProgram(shaderProgram);
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    
    glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    
    // unbind everything
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
    glUseProgram(0);
  }
}
