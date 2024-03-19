package Jade.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class KeyListener
{
  private static KeyListener instance;
  
  private boolean keyPressed[] = new boolean[400];
  
  private KeyListener()
  {
  
  }
  
  public static KeyListener get()
  {
    if (instance == null)
    {
      instance = new KeyListener();
    }
    
    return instance;
  }
  
  public static void keyCallback(long window, int key, int scancode, int action, int mods)
  {
    KeyListener kl = get();
    if (action == GLFW_PRESS)
    {
      kl.keyPressed[key] = true;
    }
    else if (action == GLFW_RELEASE)
    {
      kl.keyPressed[key] = false;
    }
  }
  
  public static boolean isKeyPressed(int keyCode)
  {
    KeyListener kl = get();
    if (keyCode < kl.keyPressed.length)
    {
      return kl.keyPressed[keyCode];
    }
    return false;
  }
}
