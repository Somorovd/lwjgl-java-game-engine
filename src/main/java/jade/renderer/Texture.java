package jade.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Texture
{
  private String filepath;
  private int    width;
  private int    height;
  
  private transient int texId;
  
  public Texture()
  {
    texId  = -1;
    width  = -1;
    height = -1;
  }
  
  public Texture(int width, int height)
  {
    // intended to only be used for framebuffers
    
    this.filepath = "Generated";
    
    // Generate texture on GPU
    texId = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texId);
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
  }
  
  
  public void init(String filepath)
  {
    this.filepath = filepath;
    
    // Generate texture on GPU
    texId = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texId);
    
    // Set texture parameters
    // Repeat image in both directions
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // Pixelate when stretching
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    // Pixelate when shrinking
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    
    IntBuffer width    = BufferUtils.createIntBuffer(1);
    IntBuffer height   = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    stbi_set_flip_vertically_on_load(true);
    ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
    
    if (image != null)
    {
      this.width  = width.get(0);
      this.height = height.get(0);
      if (channels.get(0) == 3)
      {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
      }
      else if (channels.get(0) == 4)
      {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
      }
      else
      {
        assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
      }
      
      
    }
    else
    {
      assert false : "Error: (Texture) Could not load image '" + filepath + "'";
    }
    
    stbi_image_free(image);
  }
  
  public void bind()
  {
    glBindTexture(GL_TEXTURE_2D, texId);
  }
  
  public void unbind()
  {
    glBindTexture(GL_TEXTURE_2D, 0);
  }
  
  public int getWidth() {return width;}
  
  public int getHeight() {return height;}
  
  public int getId()
  {
    return texId;
  }
  
  public String getFilepath()
  {
    return filepath;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == null) {return false;}
    if (!(o instanceof Texture oTex)) {return false;}
    return (
      oTex.getWidth() == this.width &&
        oTex.getHeight() == this.height &&
        oTex.getId() == this.texId &&
        oTex.getFilepath().equals(this.filepath));
  }
  
}
