package jade.components;

import jade.GameObject;


public abstract class Component
{
  public GameObject gameObject = null;
  
  public void start() {}
  
  public void update(float dt) {}
  
  public void imgui() {}
}
