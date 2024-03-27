package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class Camera
{
  private Matrix4f projectionMatrix;
  private Matrix4f viewMatrix;
  private Matrix4f inverseProjectionMatrix;
  private Matrix4f inverseViewMatrix;
  
  public  Vector2f position;
  private Vector2f projectionSize = new Vector2f(32 * 40, 32 * 21);
  
  public Camera(Vector2f position)
  {
    this.position                = position;
    this.projectionMatrix        = new Matrix4f();
    this.viewMatrix              = new Matrix4f();
    this.inverseProjectionMatrix = new Matrix4f();
    this.inverseViewMatrix       = new Matrix4f();
    
    adjustProjection();
  }
  
  public void adjustProjection()
  {
    projectionMatrix.identity();
    // using 32x32 px tiles
    projectionMatrix.ortho(
      0.0f,
      projectionSize.x,
      0.0f,
      projectionSize.y,
      0.0f,
      100.0f
    );
    projectionMatrix.invert(inverseProjectionMatrix);
  }
  
  public Matrix4f getViewMatrix()
  {
    Vector3f cameraFront  = new Vector3f(0.0f, 0.0f, -1.0f);
    Vector3f cameraUp     = new Vector3f(0.0f, 1.0f, 0.0f);
    Vector3f cameraEye    = new Vector3f(position.x, position.y, 20.0f);
    Vector3f cameraCenter = cameraFront.add(position.x, position.y, 0.0f);
    
    viewMatrix.identity();
    viewMatrix.lookAt(cameraEye, cameraCenter, cameraUp);
    viewMatrix.invert(inverseViewMatrix);
    
    return viewMatrix;
  }
  
  public Matrix4f getProjectionMatrix()
  {
    return projectionMatrix;
  }
  
  public Matrix4f getInverseProjectionMatrix()
  {
    return inverseProjectionMatrix;
  }
  
  public Matrix4f getInverseViewMatrix()
  {
    return inverseViewMatrix;
  }
  
  public Vector2f getProjectionSize()
  {
    return projectionSize;
  }
}
