package jade.components;

import org.joml.Vector3f;


public class GuiTestComponent extends Component
{
  public  int      publicInt    = 0;
  private int      privateInt   = 10;
  private float    privateFloat = 6.66f;
  public  boolean  publicBool   = false;
  private Vector3f privateV3    = new Vector3f(1, 0, 0);
}
