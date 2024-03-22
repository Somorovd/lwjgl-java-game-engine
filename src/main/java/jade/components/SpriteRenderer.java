package jade.components;

public class SpriteRenderer extends Component
{
  @Override
  public void start()
  {
    System.out.println("SpriteRenderer starting");
  }
  
  @Override
  public void update(float dt)
  {
    System.out.println("SpriteRenderer updating");
  }
}
