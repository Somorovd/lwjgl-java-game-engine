package jade.util;

import jade.components.SpriteSheet;
import jade.renderer.Shader;
import jade.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AssetPool
{
  private static Map<String, Shader>      shaders      = new HashMap<>();
  private static Map<String, Texture>     textures     = new HashMap<>();
  private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
  
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
      Texture texture = new Texture();
      texture.init(resourceName);
      textures.put(path, texture);
    }
    
    return textures.get(path);
  }
  
  public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet)
  {
    File   file = new File(resourceName);
    String path = file.getAbsolutePath();
    if (!spriteSheets.containsKey(path))
    {
      spriteSheets.put(path, spriteSheet);
    }
  }
  
  public static SpriteSheet getSpritesheet(String resourceName)
  {
    File   file = new File(resourceName);
    String path = file.getAbsolutePath();
    if (!spriteSheets.containsKey(path))
    {
      assert false : "Error: Tried to access spriteSheet '" + resourceName + "' that is not in asset pool";
    }
    
    return spriteSheets.getOrDefault(path, null);
  }
}
