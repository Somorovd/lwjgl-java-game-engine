package jade;

import java.util.ArrayList;
import java.util.List;


public abstract class Scene
{
  public boolean isRunning = false;
  
  protected Camera           camera;
  protected List<GameObject> gameObjects;
  
  public Scene()
  {
    this.gameObjects = new ArrayList<>();
  }
  
  public void init() {}
  
  public void start()
  {
    for (GameObject go : gameObjects)
    {
      go.start();
    }
    isRunning = true;
  }
  
  public void addGameObject(GameObject go)
  {
    gameObjects.add(go);
    if (isRunning)
    {
      go.start();
    }
  }
  
  public abstract void update(float dt);
}
