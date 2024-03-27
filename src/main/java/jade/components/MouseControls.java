package jade.components;

import jade.GameObject;
import jade.Window;
import jade.input.MouseListener;
import jade.util.Settings;

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
      holdingObject.transform.position.x = (int) (MouseListener.findOrthoX() / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
      holdingObject.transform.position.y = (int) (MouseListener.findOrthoY() / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
      
      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
      {
        placeHolding();
      }
    }
  }
}
