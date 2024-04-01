package jade.input;

import jade.Camera;
import jade.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class MouseListener
{
  // could just be a static class, no need for singleton
  private static MouseListener instance;
  
  private double   scrollX;
  private double   scrollY;
  private double   posX;
  private double   posY;
  private double   lastX;
  private double   lastY;
  private boolean  mouseButtonPressed[] = new boolean[3];
  private boolean  isDragging;
  private Vector2f gameViewportPos      = new Vector2f();
  private Vector2f gameViewportSize     = new Vector2f();
  
  private MouseListener()
  {
    this.scrollX = 0.0;
    this.scrollY = 0.0;
    this.posX    = 0.0;
    this.posY    = 0.0;
    this.lastX   = 0.0;
    this.lastY   = 0.0;
  }
  
  public static MouseListener get()
  {
    if (instance == null)
    {
      instance = new MouseListener();
    }
    
    return instance;
  }
  
  public static void mousePosCallback(long window, double xpos, double ypos)
  {
    MouseListener ml = get();
    ml.lastX      = ml.posX;
    ml.lastY      = ml.posY;
    ml.posX       = xpos;
    ml.posY       = ypos;
    ml.isDragging = Arrays.asList(ml.mouseButtonPressed).contains(true);
  }
  
  public static void mouseButtonCallback(long window, int button, int action, int mods)
  {
    MouseListener ml = get();
    
    if (action == GLFW_PRESS)
    {
      if (button < ml.mouseButtonPressed.length)
      {
        ml.mouseButtonPressed[button] = true;
      }
    }
    else if (action == GLFW_RELEASE)
    {
      ml.mouseButtonPressed[button] = false;
      ml.isDragging                 = false;
    }
  }
  
  public static void mouseScrollCallback(long window, double xoff, double yoff)
  {
    MouseListener ml = get();
    ml.scrollX = xoff;
    ml.scrollY = yoff;
  }
  
  public static void endFrame()
  {
    MouseListener ml = get();
    ml.scrollX = 0;
    ml.scrollY = 0;
    ml.lastX   = ml.posX;
    ml.lastY   = ml.posY;
  }
  
  public static float getX()
  {
    return (float) get().posX;
  }
  
  public static float getY()
  {
    return (float) get().posY;
  }
  
  public static float findOrthoX()
  {
    Camera   camera      = Window.getScene().getCamera();
    Matrix4f inverseProj = camera.getInverseProjectionMatrix();
    Matrix4f inverseView = camera.getInverseViewMatrix();
    
    float currentX = getX() - get().gameViewportPos.x;
    currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
    
    Vector4f tmp      = new Vector4f(currentX, 0, 0, 1);
    Matrix4f viewProj = new Matrix4f();
    inverseView.mul(inverseProj, viewProj);
    tmp.mul(viewProj);
    
    currentX = tmp.x;
    return currentX;
  }
  
  public static float findOrthoY()
  {
    Camera   camera      = Window.getScene().getCamera();
    Matrix4f inverseProj = camera.getInverseProjectionMatrix();
    Matrix4f inverseView = camera.getInverseViewMatrix();
    
    float currentY = getY() - get().gameViewportPos.y;
    // imgui and opengl y is flipped so use (-)
    currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
    
    Vector4f tmp      = new Vector4f(0, currentY, 0, 1);
    Matrix4f viewProj = new Matrix4f();
    inverseView.mul(inverseProj, viewProj);
    tmp.mul(viewProj);
    
    currentY = tmp.y;
    return currentY;
  }
  
  public static float getScreenX()
  {
    float currentX = getX() - get().gameViewportPos.x;
    currentX = (currentX / get().gameViewportSize.x) * 1920;
    return currentX;
  }
  
  public static float getScreenY()
  {
    float currentY = getY() - get().gameViewportPos.y;
    currentY = 1080 - (currentY / get().gameViewportSize.y) * 1080;
    return currentY;
  }
  
  public static float getDx()
  {
    MouseListener ml = get();
    return (float) (ml.lastX - ml.posX);
  }
  
  public static float getDy()
  {
    MouseListener ml = get();
    return (float) (ml.lastY - ml.posY);
  }
  
  public static float getScrollX()
  {
    return (float) get().scrollX;
  }
  
  public static float getScrollY()
  {
    return (float) get().scrollY;
  }
  
  public static boolean isDragging()
  {
    return get().isDragging;
  }
  
  public static boolean mouseButtonDown(int button)
  {
    MouseListener ml = get();
    if (button < ml.mouseButtonPressed.length)
    {
      return ml.mouseButtonPressed[button];
    }
    return false;
  }
  
  public static void setGameViewportPos(Vector2f gameViewportPos)
  {
    get().gameViewportPos.set(gameViewportPos);
  }
  
  public static void setGetGameViewportSize(Vector2f getGameViewportSize)
  {
    get().gameViewportSize.set(getGameViewportSize);
  }
}


