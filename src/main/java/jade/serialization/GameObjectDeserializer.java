package jade.serialization;

import com.google.gson.*;
import jade.GameObject;
import jade.Transform;
import jade.components.Component;

import java.lang.reflect.Type;


public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
  @Override
  public GameObject deserialize(JsonElement jsonElement, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    
    String    name       = jsonObject.get("name").getAsString();
    JsonArray components = jsonObject.getAsJsonArray("components");
    Transform transform  = context.deserialize(jsonObject.get("transform"), Transform.class);
    int       zIndex     = context.deserialize(jsonObject.get("zIndex"), int.class);
    
    GameObject go = new GameObject(name, transform, zIndex);
    for (JsonElement e : components)
    {
      Component c = context.deserialize(e, Component.class);
      go.addComponent(c);
    }
    
    return go;
  }
  
}
