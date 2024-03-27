package jade;

import jade.components.Component;

import java.util.ArrayList;
import java.util.List;


public class GameObject
{
  private static int ID_COUNTER = 0;
  
  private int uid = -1;
  
  public Transform transform;
  
  private String          name;
  private List<Component> components;
  private int             zIndex;
  
  public GameObject(String name, Transform transform, int zIndex)
  {
    this.name       = name;
    this.transform  = transform;
    this.components = new ArrayList<>();
    this.zIndex     = zIndex;
    this.uid        = ID_COUNTER++;
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
    c.generateId();
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
  
  public void imgui()
  {
    for (Component c : components)
    {
      c.imgui();
    }
  }
  
  public List<Component> getAllComponents()
  {
    return components;
  }
  
  public int getzIndex()
  {
    return zIndex;
  }
  
  public static void init(int maxId)
  {
    ID_COUNTER = maxId;
  }
  
  public int getUid()
  {
    return uid;
  }
}
