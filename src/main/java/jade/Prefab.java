package jade;

import jade.components.Sprite;
import jade.components.SpriteRenderer;
import org.joml.Vector2f;


public class Prefab
{
  public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
  {
    GameObject block = new GameObject(
      "Sprite_Object_Gen",
      new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)),
      0
    );
    SpriteRenderer spr = new SpriteRenderer();
    spr.setSprite(sprite);
    block.addComponent(spr);
    return block;
  }
}
