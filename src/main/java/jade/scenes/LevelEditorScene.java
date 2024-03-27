package jade.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefab;
import jade.components.GridLines;
import jade.components.MouseControls;
import jade.components.Sprite;
import jade.components.SpriteSheet;
import jade.input.MouseListener;
import jade.renderer.DebugDraw;
import jade.util.AssetPool;
import jade.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class LevelEditorScene extends Scene
{
  private final String DECORATIONS_AND_BLOCKS = "assets/images/sheets/decorationsAndBlocks.png";
  private final String DEFAULT_SHADER         = "assets/shaders/default.glsl";
  
  private SpriteSheet sprites;
  private GameObject  levelEditorGO = new GameObject("LevelEditor");
  
  public LevelEditorScene() {}
  
  private void loadResources()
  {
    AssetPool.getShader(DEFAULT_SHADER);
    AssetPool.addSpriteSheet(
      DECORATIONS_AND_BLOCKS,
      new SpriteSheet(AssetPool.getTexture(DECORATIONS_AND_BLOCKS), 16, 16, 81, 0)
    );
  }
  
  @Override
  public void init()
  {
    levelEditorGO.addComponent(new MouseControls());
    levelEditorGO.addComponent(new GridLines());
    
    loadResources();
    camera  = new Camera(new Vector2f());
    sprites = AssetPool.getSpritesheet(DECORATIONS_AND_BLOCKS);
    
    DebugDraw.addLine2D(new Vector2f(), new Vector2f(800, 800), new Vector3f(1, 0, 0), 120);
    
    if (levelLoaded)
    {
      activeGameObject = gameObjects.get(0);
      return;
    }
  }
  
  @Override
  public void update(float dt)
  {
    levelEditorGO.update(dt);
    
    MouseListener.findOrthoY();
    
    for (GameObject go : gameObjects)
    {
      go.update(dt);
    }
    
    renderer.render();
  }
  
  @Override
  public void imgui()
  {
    ImGui.begin("Test window");
    
    ImVec2 windowPos = new ImVec2();
    ImGui.getWindowPos(windowPos);
    ImVec2 windowSize = new ImVec2();
    ImGui.getWindowSize(windowSize);
    ImVec2 itemSpacing = new ImVec2();
    ImGui.getStyle().getItemSpacing(itemSpacing);
    
    float windowX2 = windowPos.x + windowSize.x;
    for (int i = 0; i < sprites.size(); i++)
    {
      Sprite     sprite       = sprites.getSprite(i);
      float      spriteWidth  = sprite.getWidth() * 2;
      float      spriteHeight = sprite.getHeight() * 2;
      int        id           = sprite.getTexId();
      Vector2f[] texCoords    = sprite.getTexCoords();
      
      ImGui.pushID(i);
      if (ImGui.imageButton(
        id,
        spriteWidth,
        spriteHeight,
        texCoords[2].x,
        texCoords[0].y,
        texCoords[0].x,
        texCoords[2].y
      ))
      {
        GameObject go = Prefab.generateSpriteObject(
          sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT
        );
        levelEditorGO.getComponent(MouseControls.class).pickupObject(go);
      }
      ImGui.popID();
      
      ImVec2 lastButtonPos = new ImVec2();
      ImGui.getItemRectMax(lastButtonPos);
      float lastButtonX2 = lastButtonPos.x;
      float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
      
      if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
      {
        ImGui.sameLine();
      }
    }
    
    ImGui.end();
  }
}
