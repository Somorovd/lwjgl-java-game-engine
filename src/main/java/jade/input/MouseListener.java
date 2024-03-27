package jade.input;

import jade.Camera;
import jade.Window;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class MouseListener
{
  private static MouseListener instance;
  
  private double scrollX, scrollY;
  private double posX, posY, lastY, lastX;
  private boolean mouseButtonPressed[] = new boolean[3];
  private boolean isDragging;
  
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
    
    float currentX = getX();
    currentX = (currentX / Window.getWidth()) * 2.0f - 1.0f;
    Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
    tmp.mul(inverseProj).mul(inverseView);
    currentX = tmp.x;
    return currentX;
  }
  
  public static float findOrthoY()
  {
    Camera   camera      = Window.getScene().getCamera();
    Matrix4f inverseProj = camera.getInverseProjectionMatrix();
    Matrix4f inverseView = camera.getInverseViewMatrix();
    
    float currentY = Window.getHeight() - getY();
    currentY = (currentY / Window.getHeight()) * 2.0f - 1.0f;
    Vector4f tmp = new Vector4f(0, currentY, 0, 1);
    tmp.mul(inverseProj).mul(inverseView);
    currentY = tmp.y;
    System.out.println(currentY);
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
}
