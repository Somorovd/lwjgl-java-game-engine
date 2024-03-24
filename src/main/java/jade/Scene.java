package jade;

import imgui.ImGui;
import jade.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;


public abstract class Scene
{
  public    boolean          isRunning        = false;
  protected Renderer         renderer         = new Renderer();
  protected Camera           camera;
  protected List<GameObject> gameObjects      = new ArrayList<>();
  protected GameObject       activeGameObject = null;
  
  
  public Scene() {}
  
  public void init() {}
  
  public void start()
  {
    for (GameObject go : gameObjects)
    {
      go.start();
      renderer.add(go);
    }
    isRunning = true;
  }
  
  public void addGameObjectToScene(GameObject go)
  {
    gameObjects.add(go);
    if (isRunning)
    {
      go.start();
      renderer.add(go);
    }
  }
  
  public void sceneImgui()
  {
    if (activeGameObject != null)
    {
      ImGui.begin("Inspector");
      activeGameObject.imgui();
      ImGui.end();
    }
    
    imgui();
  }
  
  public void imgui() {}
  
  public Camera getCamera()
  {
    return camera;
  }
  
  public abstract void update(float dt);
}
