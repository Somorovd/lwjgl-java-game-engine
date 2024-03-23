package jade;

import jade.components.SpriteRenderer;
import jade.util.AssetPool;
import org.joml.Vector2f;


public class LevelEditorScene extends Scene
{
  public LevelEditorScene() {}
  
  private void loadResources()
  {
    AssetPool.getShader("assets/shaders/default.glsl");
  }
  
  @Override
  public void init()
  {
    camera = new Camera(new Vector2f());
    
    GameObject obj1 = new GameObject(
      "Object 1",
      new Transform(
        new Vector2f(0, 0),
        new Vector2f(256, 256)
      )
    );
    obj1.addComponent(
      new SpriteRenderer(AssetPool.getTexture("assets/images/test-image.png"))
    );
    this.addGameObjectToScene(obj1);
    
    loadResources();
  }
  
  @Override
  public void update(float dt)
  {
    System.out.println("FPS" + 1.0f / dt);
    for (GameObject go : gameObjects)
    {
      go.update(dt);
    }
    
    renderer.render();
  }
}
