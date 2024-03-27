package jade.components;

import jade.Window;
import jade.renderer.DebugDraw;
import jade.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class GridLines extends Component
{
  @Override
  public void update(float dt)
  {
    Vector2f cameraPos      = Window.getScene().getCamera().position;
    Vector2f projectionSize = Window.getScene().getCamera().getProjectionSize();
    Vector3f color          = new Vector3f(0.8f);
    
    int firstX   = ((int) (cameraPos.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH);
    int firstY   = ((int) (cameraPos.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT);
    int numV     = (int) (projectionSize.x / Settings.GRID_WIDTH);
    int numH     = (int) (projectionSize.y / Settings.GRID_HEIGHT);
    int w        = (int) projectionSize.x;
    int h        = (int) projectionSize.y;
    int maxLines = Math.max(numH, numV);
    
    for (int i = 0; i < maxLines; i++)
    {
      int x = firstX + (Settings.GRID_WIDTH * i);
      int y = firstY + (Settings.GRID_WIDTH * i);
      
      if (i < numV)
      {
        DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + h), color);
      }
      if (i < numH)
      {
        DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + w, y), color);
      }
    }
  }
}
