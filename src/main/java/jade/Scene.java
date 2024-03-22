package jade;

import jade.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;


public abstract class Scene
{
  public boolean isRunning = false;
  
  protected Camera           camera;
  protected Renderer         renderer;
  protected List<GameObject> gameObjects;
  
  
  public Scene()
  {
    this.gameObjects = new ArrayList<>();
    this.renderer    = new Renderer();
  }
  
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
  
  public Camera getCamera()
  {
    return camera;
  }
  
  public abstract void update(float dt);
}
