package jade;

import jade.imgui.ImGuiLayer;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.renderer.*;
import jade.scenes.LevelEditorScene;
import jade.scenes.LevelScene;
import jade.scenes.Scene;
import jade.util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window
{
  private static Window window       = null;
  private static Scene  currentScene = null;
  public         float  r            = 1.0f;
  public         float  g            = 1.0f;
  public         float  b            = 1.0f;
  public         float  a            = 1.0f;
  private        int    width;
  private        int    height;
  private        String title;
  
  private long           glfwWindow;
  private ImGuiLayer     imGuiLayer;
  private FrameBuffer    frameBuffer;
  private PickingTexture pickingTexture;
  
  private Window()
  {
    this.width  = 960;
    this.height = 540;
    this.title  = "Mario";
  }
  
  public static void changeScene(int newScene)
  {
    switch (newScene)
    {
      case 0:
        currentScene = new LevelEditorScene();
        break;
      case 1:
        currentScene = new LevelScene();
      default:
        assert false : "Unknown scene '" + newScene + "'";
        break;
    }
    currentScene.load();
    currentScene.init();
    currentScene.start();
  }
  
  public static Window get()
  {
    if (Window.window == null)
    {
      Window.window = new Window();
    }
    
    return Window.window;
  }
  
  public static Scene getScene()
  {
    return currentScene;
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
    glfwSetWindowSizeCallback(glfwWindow, (w, width, height) -> {
      Window.setWidth(width);
      Window.setHeight(height);
    });
    
    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);
    
    glfwShowWindow(glfwWindow);
    
    // critical for LWJGL to work with GLFW
    GL.createCapabilities();
    
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    
    frameBuffer    = new FrameBuffer(1920, 1080);
    pickingTexture = new PickingTexture(1920, 1080);
    
    imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
    imGuiLayer.initImGui();
    
    glViewport(0, 0, 1920, 1080);
  }
  
  private void loop()
  {
    float beginTime = (float) glfwGetTime();
    float endTime;
    float dt        = -1.0f;
    
    Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
    Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
    
    while (!glfwWindowShouldClose(glfwWindow))
    {
      glfwPollEvents();
      
      // Render pass 1. Render to picking texture
      glDisable(GL_BLEND);
      pickingTexture.enableWriting();
      
      glViewport(0, 0, 1920, 1080);
      glClearColor(0, 0, 0, 0);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      
      Renderer.bindShader(pickingShader);
      currentScene.render();
      
      pickingTexture.disableWriting();
      glEnable(GL_BLEND);
      
      // Render pass 2. Render actual game
      DebugDraw.beginFrame();
      
      // framebuffer for the viewport
      frameBuffer.bind();
      
      glClearColor(r, g, b, a);
      glClear(GL_COLOR_BUFFER_BIT);
      
      if (dt >= 0)
      {
        DebugDraw.draw();
        Renderer.bindShader(defaultShader);
        currentScene.update(dt);
        currentScene.render();
      }
      frameBuffer.unbind();
      
      imGuiLayer.update(dt, currentScene);
      glfwSwapBuffers(glfwWindow);
      
      endTime   = (float) glfwGetTime();
      dt        = endTime - beginTime;
      beginTime = endTime;
    }
    
    currentScene.saveExit();
  }
  
  public static float getWidth()
  {
    return get().width;
  }
  
  public static float getHeight()
  {
    return get().height;
  }
  
  public static void setWidth(int width) {get().width = width;}
  
  public static void setHeight(int height) {get().height = height;}
  
  public static FrameBuffer getFramebuffer() {return get().frameBuffer;}
  
  public static float getTargetAspectRatio()
  {
    return 16.0f / 9.0f;
  }
}
