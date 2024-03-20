package jade;

import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window
{
  private int    width;
  private int    height;
  private String title;
  private long   glfwWindow;
  
  private static Window window       = null;
  private static Scene  currentScene = null;
  
  public float r = 1.0f;
  public float g = 1.0f;
  public float b = 1.0f;
  public float a = 1.0f;
  
  private Window()
  {
    this.width = 960;
    this.height = 540;
    this.title = "Mario";
  }
  
  public static void changeScene(int newScene)
  {
    switch (newScene)
    {
      case 0:
        currentScene = new LevelEditorScene();
        currentScene.init();
        break;
      case 1:
        currentScene = new LevelScene();
        currentScene.init();
      default:
        assert false : "Unknown scene '" + newScene + "'";
        break;
    }
  }
  
  public static Window get()
  {
    if (Window.window == null)
    {
      Window.window = new Window();
    }
    
    return Window.window;
  }
  
  public void run()
  {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");
    
    init();
    changeScene(0);
    loop();
    
    // Free the memory
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);
    
    //Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }
  
  private void init()
  {
    // Setup an error callback
    GLFWErrorCallback.createPrint(System.err).set();
    
    // Initialize GLFW
    if (!glfwInit())
    {
      throw new IllegalStateException("Unable to initialize GLFW");
    }
    
    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    
    // Create the window
    glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
    
    if (glfwWindow == NULL)
    {
      throw new IllegalStateException("Failed to crete the GLFW window");
    }
    
    // Register input callbacks
    glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
    glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
    glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
    glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
    
    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);
    
    glfwShowWindow(glfwWindow);
    
    // critical for LWJGL to work with GLFW
    GL.createCapabilities();
  }
  
  private void loop()
  {
    float beginTime = Time.getTime();
    float endTime;
    float dt        = -1.0f;
    
    while (!glfwWindowShouldClose(glfwWindow))
    {
      glfwPollEvents();
      
      glClearColor(r, g, b, a);
      glClear(GL_COLOR_BUFFER_BIT);
      
      if (dt >= 0)
      {
        currentScene.update(dt);
      }
      
      glfwSwapBuffers(glfwWindow);
      
      endTime = Time.getTime();
      dt = endTime - beginTime;
      beginTime = endTime;
    }
  }
}
