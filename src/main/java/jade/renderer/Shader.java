package jade.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;


public class Shader
{
  
  private int     shaderProgramId;
  private String  vertexSrc;
  private String  fragmentSrc;
  private String  filepath;
  private boolean beingUsed = false;
  
  public Shader(String filepath)
  {
    this.filepath = filepath;
    try
    {
      String   source      = new String(Files.readAllBytes(Paths.get(filepath)));
      String   regex       = "#type +([a-zA-z]+)";
      Pattern  pattern     = Pattern.compile(regex);
      String[] splitString = pattern.split(source);
      Matcher  matcher     = pattern.matcher(source);
      
      int i = 1;
      while (matcher.find())
      {
        String type = matcher.group(1);
        switch (type)
        {
          case "vertex":
            vertexSrc = splitString[i++];
            break;
          case "fragment":
            fragmentSrc = splitString[i++];
            break;
          default:
            throw new IOException("Unexpected token '" + type + "'");
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      assert false : "Error: Could not open file for shader: '" + filepath + "'";
    }
  }
  
  public void compile()
  {
    int vertexId;
    int fragmentId;
    
    vertexId = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexId, vertexSrc);
    glCompileShader(vertexId);
    
    fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentId, fragmentSrc);
    glCompileShader(fragmentId);
    
    // Check for errors in compilation
    int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed");
      System.out.println(glGetShaderInfoLog(vertexId, len));
      assert false : "";
    }
    
    // Check for errors in compilation
    success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed");
      System.out.println(glGetShaderInfoLog(fragmentId, len));
      assert false : "";
    }
    
    // Link shaders
    shaderProgramId = glCreateProgram();
    glAttachShader(shaderProgramId, vertexId);
    glAttachShader(shaderProgramId, fragmentId);
    glLinkProgram(shaderProgramId);
    
    // Check for linking errors
    success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
    if (success == GL_FALSE)
    {
      int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tLinking shaders failed");
      System.out.println(glGetProgramInfoLog(shaderProgramId, len));
      assert false : "";
    }
  }
  
  public void use()
  {
    if (!beingUsed)
    {
      glUseProgram(shaderProgramId);
      beingUsed = true;
    }
  }
  
  public void detach()
  {
    glUseProgram(0);
    beingUsed = false;
  }
  
  public void uploadMat4f(String varName, Matrix4f mat)
  {
    use();
    int         varLocation = glGetUniformLocation(shaderProgramId, varName);
    FloatBuffer matBuffer   = BufferUtils.createFloatBuffer(16);
    mat.get(matBuffer);
    glUniformMatrix4fv(varLocation, false, matBuffer);
  }
  
  public void uploadMat3f(String varName, Matrix3f mat)
  {
    use();
    int         varLocation = glGetUniformLocation(shaderProgramId, varName);
    FloatBuffer matBuffer   = BufferUtils.createFloatBuffer(9);
    mat.get(matBuffer);
    glUniformMatrix3fv(varLocation, false, matBuffer);
  }
  
  public void uploadVec4f(String varName, Vector4f vec)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
  }
  
  public void uploadVec3f(String varName, Vector3f vec)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform3f(varLocation, vec.x, vec.y, vec.z);
  }
  
  public void uploadVec2f(String varName, Vector2f vec)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform2f(varLocation, vec.x, vec.y);
  }
  
  public void uploadFloat(String varName, float val)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform1f(varLocation, val);
  }
  
  public void uploadInt(String varName, int val)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform1i(varLocation, val);
  }
  
  public void uploadTexture(String varName, int slot)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform1i(varLocation, slot);
  }
  
  public void uploadIntArray(String varName, int[] array)
  {
    use();
    int varLocation = glGetUniformLocation(shaderProgramId, varName);
    glUniform1iv(varLocation, array);
  }
}
