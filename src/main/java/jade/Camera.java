package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class Camera
{
  private Matrix4f projectionMatrix;
  private Matrix4f viewMatrix;
  private Vector2f position;
  
  public Camera(Vector2f position)
  {
    this.position         = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix       = new Matrix4f();
    
    adjustProjection();
  }
  
  public void adjustProjection()
  {
    projectionMatrix.identity();
    // using 32x32 px tiles
    projectionMatrix.ortho(
      0.0f,
      32.0f * 40.0f,
      0.0f,
      32.0f * 21.0f,
      0.0f,
      100.0f
    );
  }
  
  public Matrix4f getViewMatrix()
  {
    Vector3f cameraFront  = new Vector3f(0.0f, 0.0f, -1.0f);
    Vector3f cameraUp     = new Vector3f(0.0f, 1.0f, 0.0f);
    Vector3f cameraEye    = new Vector3f(position.x, position.y, 20.0f);
    Vector3f cameraCenter = cameraFront.add(position.x, position.y, 0.0f);
    
    viewMatrix.identity();
    viewMatrix.lookAt(cameraEye, cameraCenter, cameraUp);
    
    return viewMatrix;
  }
  
  public Matrix4f getProjectionMatrix()
  {
    return projectionMatrix;
  }
}
