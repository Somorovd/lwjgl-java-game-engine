package jade;

import jade.components.Component;

import java.util.ArrayList;
import java.util.List;


public class GameObject
{
  public Transform transform;
  
  private String          name;
  private List<Component> components;
  
  public GameObject(String name)
  {
    init(name, new Transform());
  }
  
  public GameObject(String name, Transform transform)
  {
    init(name, transform);
  }
  
  private void init(String name, Transform transform)
  {
    this.name       = name;
    this.transform  = transform;
    this.components = new ArrayList<>();
  }
  
  public <T extends Component> T getComponent(Class<T> componentClass)
  {
    for (Component c : components)
    {
      // if c is of type T
      if (componentClass.isAssignableFrom(c.getClass()))
      {
        return componentClass.cast(c);
      }
    }
    return null;
  }
  
  public <T extends Component> void removeComponent(Class<T> componentClass)
  {
    for (int i = 0; i < components.size(); i++)
    {
      if (componentClass.isAssignableFrom(components.getClass()))
      {
        components.remove(i);
        return;
      }
    }
  }
  
  public void addComponent(Component c)
  {
    components.add(c);
    c.gameObject = this;
  }
  
  public void update(float dt)
  {
    for (Component c : components)
    {
      c.update(dt);
    }
  }
  
  public void start()
  {
    for (Component c : components)
    {
      c.start();
    }
  }
}
