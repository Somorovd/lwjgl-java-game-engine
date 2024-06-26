package jade.components;

import imgui.ImGui;
import jade.GameObject;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public abstract class Component
{
  private static int ID_COUNTER = 0;
  
  private int uid = -1;
  
  public transient GameObject gameObject = null;
  
  public void start() {}
  
  public void update(float dt) {}
  
  public void imgui()
  {
    try
    {
      Field[] fields = this.getClass().getDeclaredFields();
      for (Field field : fields)
      {
        boolean isPrivate = Modifier.isPrivate(field.getModifiers());
        if (isPrivate)
        {
          field.setAccessible(true);
        }
        
        boolean isTransient = Modifier.isTransient(field.getModifiers());
        if (isTransient)
        {
          continue;
        }
        
        Class<?> type  = field.getType();
        Object   value = field.get(this);
        String   name  = field.getName();
        
        if (type == int.class)
        {
          int   val   = (int) value;
          int[] imInt = {val};
          if (ImGui.dragInt(name + ": ", imInt))
          {
            field.set(this, imInt[0]);
          }
        }
        else if (type == float.class)
        {
          float   val     = (float) value;
          float[] imFloat = {val};
          if (ImGui.dragFloat(name + ": ", imFloat))
          {
            field.set(this, imFloat[0]);
          }
        }
        else if (type == boolean.class)
        {
          boolean val = (boolean) value;
          if (ImGui.checkbox(name + ": ", val))
          {
            field.set(this, !val);
          }
        }
        else if (type == Vector3f.class)
        {
          Vector3f val   = (Vector3f) value;
          float[]  imVec = {val.x, val.y, val.z};
          if (ImGui.dragFloat3(name + ": ", imVec))
          {
            // non-primitive so dont need field.set()
            val.set(imVec[0], imVec[1], imVec[2]);
          }
        }
        
        if (isPrivate)
        {
          field.setAccessible(false);
        }
      }
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }
  
  public void generateId()
  {
    if (uid == -1)
    {
      uid = ID_COUNTER++;
    }
  }
  
  public int getUid() {return uid;}
  
  public static void init(int maxId)
  {
    ID_COUNTER = maxId;
  }
  
}
