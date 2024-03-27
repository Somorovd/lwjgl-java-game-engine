package jade.renderer;

import jade.Window;
import jade.util.AssetPool;
import jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class DebugDraw
{
  private static int          MAX_LINES   = 500;
  private static List<Line2D> lines       = new ArrayList<>();
  private static float[]      vertexArray = new float[MAX_LINES * 6 * 2];
  private static Shader       shader      = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
  private static boolean      started     = false;
  
  private static int vaoId;
  private static int vboId;
  
  public static void start()
  {
    // Generate the vao
    vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);
    
    // Create the vbo and buffer some memory
    vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);
    
    // Enable the vertex array attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);
    
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
    glEnableVertexAttribArray(1);
    
    glLineWidth(2.0f);
  }
  
  public static void beginFrame()
  {
    if (!started)
    {
      start();
      started = true;
    }
    
    // Remove dead lines
    for (int i = 0; i < lines.size(); i++)
    {
      if (lines.get(i).beginFrame() < 0)
      {
        lines.remove(i--);
      }
    }
  }
  
  public static void draw()
  {
    if (lines.size() <= 0)
    {
      return;
    }
    
    int index = 0;
    for (Line2D line : lines)
    {
      for (int i = 0; i < 2; i++)
      {
        Vector2f position = i == 0 ? line.getFrom() : line.getTo();
        Vector3f color    = line.getColor();
        
        // load position
        vertexArray[index + 0] = position.x;
        vertexArray[index + 1] = position.y;
        vertexArray[index + 2] = -10.0f;
        // load color
        vertexArray[index + 3] = color.x;
        vertexArray[index + 4] = color.y;
        vertexArray[index + 5] = color.z;
        
        index += 6;
      }
    }
    
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2)
    );
    
    // use our shader
    shader.use();
    shader.uploadMat4f("uProj", Window.getScene().getCamera().getProjectionMatrix());
    shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());
    
    // bind the vao
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    
    // draw the batch
    glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);
    
    // disable location
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
    
    
    shader.detach();
  }
  
  // ==========================================
  // Add line2D methods
  // ==========================================
  
  public static void addLine2D(Vector2f from, Vector2f to)
  {
    // TODO: ADD CONSTANTS FOR COMMON COLORS
    addLine2D(from, to, new Vector3f(1, 0, 0), 1);
  }
  
  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color)
  {
    // TODO: ADD CONSTANTS FOR COMMON COLORS
    addLine2D(from, to, color, 1);
  }
  
  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime)
  {
    if (lines.size() >= MAX_LINES)
    {
      return;
    }
    
    DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
  }
  
  // ==========================================
  // Add box2D methods
  // ==========================================
  
  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime)
  {
    Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2));
    Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2));
    
    Vector2f[] verts = {
      new Vector2f(min.x, min.y),
      new Vector2f(min.x, max.y),
      new Vector2f(max.x, max.y),
      new Vector2f(max.x, min.y)
    };
    
    if (rotation != 0.0f)
    {
      for (Vector2f v : verts)
      {
        JMath.rotate(v, rotation, center);
      }
    }
    
    for (int i = 0; i < 4; i++)
    {
      addLine2D(verts[i], verts[(i + 1) % 4], color, lifetime);
    }
  }
  
  // ==========================================
  // Add circle2D methods
  // ==========================================
  public static void addCircle2D(Vector2f center, float radius, Vector3f color, int lifetime)
  {
    Vector2f[] points    = new Vector2f[20];
    int        increment = 360 / points.length;
    
    Vector2f tmp    = new Vector2f(radius, 0);
    Vector2f origin = new Vector2f();
    
    for (int i = 0; i < points.length; i++)
    {
      JMath.rotate(tmp, increment, origin);
      points[i] = new Vector2f(tmp).add(center);
    }
    for (int i = 0; i < points.length; i++)
    {
      addLine2D(points[i], points[(i + 1) % points.length], color, lifetime);
    }
  }
}
