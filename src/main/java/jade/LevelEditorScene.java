package jade;

import jade.input.KeyListener;

import java.awt.event.KeyEvent;


public class LevelEditorScene extends Scene
{
  
  private boolean changingScene            = false;
  private float   maxTimeToChangeScene     = 2.0f;
  private float   currentTimeToChangeScene = 0.0f;
  
  public LevelEditorScene()
  {
    System.out.println("Inside LevelEditor Scene");
  }
  
  @Override
  public void update(float dt)
  {
    if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
    {
      changingScene = true;
    }
    
    if (changingScene && currentTimeToChangeScene < maxTimeToChangeScene)
    {
      currentTimeToChangeScene += dt;
      
      Window w = Window.get();
      float  f = 1.0f - currentTimeToChangeScene / maxTimeToChangeScene;
      w.r = f;
      w.g = f;
      w.b = f;
      w.a = f;
    }
    else if (changingScene)
    {
      Window.changeScene(1);
    }
  }
}
