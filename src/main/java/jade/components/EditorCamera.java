package jade.components;

import jade.Camera;
import jade.input.KeyListener;
import jade.input.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;


public class EditorCamera extends Component
{
  private final float   dragDebounce      = 0.032f;
  private final float   dragSensitivity   = 30.0f;
  private final float   scrollSensitivity = 0.1f;
  private       float   currentDebounce   = dragDebounce;
  private       float   currentLerpTime   = 0.0f;
  private       boolean reset             = false;
  
  private Camera   levelEditorCamera;
  private Vector2f clickOrigin;
  
  public EditorCamera(Camera levelEditorCamera)
  {
    this.levelEditorCamera = levelEditorCamera;
    this.clickOrigin       = new Vector2f();
  }
  
  @Override
  public void update(float dt)
  {
    Vector2f mousePos = new Vector2f(MouseListener.findOrthoX(), MouseListener.findOrthoY());
    
    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && currentDebounce > 0)
    {
      this.clickOrigin = mousePos;
      currentDebounce -= dt;
    }
    else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
    {
      Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
      levelEditorCamera.position.sub(delta.mul(dt * dragSensitivity));
      clickOrigin.lerp(mousePos, dt);
    }
    else if (!MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
    {
      currentDebounce = dragDebounce;
    }
    
    if (MouseListener.getScrollY() != 0.0f)
    {
      float addValue = (float) Math.pow(
        Math.abs(MouseListener.getScrollY() * scrollSensitivity),
        1 / levelEditorCamera.getZoom()
      );
      addValue *= -Math.signum(MouseListener.getScrollY());
      levelEditorCamera.addZoom(addValue);
    }
    
    // period on the number pad
    if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL))
    {
      reset = true;
    }
    
    if (reset)
    {
      levelEditorCamera.position.lerp(new Vector2f(), currentLerpTime);
      levelEditorCamera.setZoom((1.0f - levelEditorCamera.getZoom()) * currentLerpTime);
      currentLerpTime += 0.1f * dt;
      
      if (Math.abs(levelEditorCamera.position.x) <= 5 && Math.abs(levelEditorCamera.position.y) <= 5)
      {
        levelEditorCamera.position.set(0, 0);
        levelEditorCamera.setZoom(1);
        reset = false;
      }
    }
    
    levelEditorCamera.adjustProjection();
  }
}
