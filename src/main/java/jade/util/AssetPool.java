package jade.util;

import jade.renderer.Shader;
import jade.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AssetPool
{
  private static Map<String, Shader>  shaders  = new HashMap<>();
  private static Map<String, Texture> textures = new HashMap<>();
  
  public static Shader getShader(String resourceName)
  {
    File   file = new File(resourceName);
    String path = file.getAbsolutePath();
    if (!shaders.containsKey(path))
    {
      Shader shader = new Shader(resourceName);
      shader.compile();
      shaders.put(path, shader);
    }
    
    return shaders.get(path);
  }
  
  public static Texture getTexture(String resourceName)
  {
    File   file = new File(resourceName);
    String path = file.getAbsolutePath();
    if (!textures.containsKey(path))
    {
      Texture texture = new Texture(resourceName);
      textures.put(path, texture);
    }
    
    return textures.get(path);
  }
}
