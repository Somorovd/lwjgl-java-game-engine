package jade.components;

import jade.GameObject;
import jade.Window;
import jade.input.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;


public class MouseControls extends Component
{
  GameObject holdingObject = null;
  
  public void pickupObject(GameObject go)
  {
    this.holdingObject = go;
    Window.getScene().addGameObjectToScene(go);
  }
  
  public void placeHolding()
  {
    this.holdingObject = null;
  }
  
  @Override
  public void update(float dt)
  {
    if (holdingObject != null)
    {
      holdingObject.transform.position.x = MouseListener.findOrthoX() - 16;
      holdingObject.transform.position.y = MouseListener.findOrthoY() - 16;
      
      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
      {
        placeHolding();
      }
    }
  }
}
