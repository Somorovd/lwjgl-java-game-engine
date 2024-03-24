package jade.components;

import imgui.ImGui;
import jade.Transform;
import jade.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;


public class SpriteRenderer extends Component
{
  private Vector4f color  = new Vector4f(1, 1, 1, 1);
  private Sprite   sprite = new Sprite();
  
  private transient Transform lastTransform;
  private transient boolean   isDirty = true;
  
  @Override
  public void start()
  {
    lastTransform = gameObject.transform.copyTo();
  }
  
  @Override
  public void update(float dt)
  {
    if (!lastTransform.equals(gameObject.transform))
    {
      gameObject.transform.copyTo(lastTransform);
      isDirty = true;
    }
  }
  
  @Override
  public void imgui()
  {
    float[] imColor = {color.x, color.y, color.z, color.w};
    if (ImGui.colorPicker4("Color Picker: ", imColor))
    {
      color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
      isDirty = true;
    }
  }
  
  public Vector4f getColor()
  {
    return color;
  }
  
  public Texture getTexture()
  {
    return sprite.getTexture();
  }
  
  public Vector2f[] getTexCoords()
  {
    return sprite.getTexCoords();
  }
  
  public void setSprite(Sprite sprite)
  {
    this.sprite  = sprite;
    this.isDirty = true;
  }
  
  public void setColor(Vector4f color)
  {
    if (!color.equals(this.color))
    {
      this.color   = color;
      this.isDirty = true;
    }
  }
  
  public boolean isDirty()
  {
    return isDirty;
  }
  
  public void setClean()
  {
    isDirty = false;
  }
}
