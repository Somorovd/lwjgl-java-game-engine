package jade;

import org.joml.Vector2f;


public class Transform
{
  public Vector2f position;
  public Vector2f scale;
  
  
  public Transform()
  {
    init(new Vector2f(), new Vector2f());
  }
  
  public Transform(Vector2f position, Vector2f scale)
  {
    init(position, scale);
  }
  
  public Transform copyTo()
  {
    return new Transform(
      new Vector2f(position),
      new Vector2f(scale)
    );
  }
  
  public void copyTo(Transform to)
  {
    to.position.set(position);
    to.scale.set(scale);
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof Transform t))
    {
      return false;
    }
    
    return t.position.equals(position) && t.scale.equals(scale);
  }
  
  private void init(Vector2f position, Vector2f scale)
  {
    this.position = position;
    this.scale    = scale;
  }
}
