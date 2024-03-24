package jade.components;

import jade.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;


public class SpriteSheet
{
  
  private Texture      texture;
  private List<Sprite> sprites;
  
  public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing)
  {
    this.sprites = new ArrayList<>();
    this.texture = texture;
    
    float width    = (float) texture.getWidth();
    float height   = (float) texture.getHeight();
    int   currentX = 0;
    int   currentY = texture.getHeight() - spriteHeight;
    
    for (int i = 0; i < numSprites; i++)
    {
      float top    = (currentY + spriteHeight) / height;
      float bottom = currentY / height;
      float left   = currentX / width;
      float right  = (currentX + spriteWidth) / width;
      
      Vector2f[] texCoords = {
        new Vector2f(right, top),
        new Vector2f(right, bottom),
        new Vector2f(left, bottom),
        new Vector2f(left, top),
      };
      
      Sprite sprite = new Sprite();
      sprite.setTexture(texture);
      sprite.setTexCoords(texCoords);
      sprites.add(sprite);
      
      currentX += spriteWidth + spacing;
      if (currentX >= width)
      {
        currentX = 0;
        currentY += spriteHeight + spacing;
      }
    }
  }
  
  public Sprite getSprite(int index)
  {
    return sprites.get(index);
  }
}
