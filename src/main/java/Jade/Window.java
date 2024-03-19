package Jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window
{
  private int width, height;
  private String title;
  private long glfwWindow;
  
  private static Window window = null;
  
  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Mario";
  }
  
  public static Window get() {
    if (Window.window == null) {
      Window.window = new Window();
    }
    
    return Window.window;
  }
  
  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");
    
    init();
    loop();
    
    // Free the memory
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);
    
    //Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }
  
  private void init() {
    // Setup an error callback
    GLFWErrorCallback.createPrint(System.err).set();
    
    // Initialize GLFW
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }
    
    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    
    // Create the window
    glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
    
    if (glfwWindow == NULL) {
      throw new IllegalStateException("Failed to crete the GLFW window");
    }
    
    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);
    
    glfwShowWindow(glfwWindow);
    
    // critical for LWJGL to work with GLFW
    GL.createCapabilities();
  }
  
  private void loop() {
    while (!glfwWindowShouldClose(glfwWindow)) {
      glfwPollEvents();
      
      glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);
      glfwSwapBuffers(glfwWindow);
    }
  }
}
