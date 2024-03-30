package jade.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.Window;
import jade.input.MouseListener;
import org.joml.Vector2f;


public class GameViewWindow
{
  private static float leftX;
  private static float rightX;
  private static float topY;
  private static float bottomY;
  
  public static void imgui()
  {
    ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
    );
    
    ImVec2 windowSize = getLargestSizeForViewport();
    ImVec2 windowPos  = getCenteredPositionForViewport(windowSize);
    
    ImGui.setCursorPos(windowPos.x, windowPos.y);
    
    ImVec2 topLeft = new ImVec2();
    ImGui.getCursorScreenPos(topLeft);
    topLeft.x -= ImGui.getScrollX();
    topLeft.y -= ImGui.getScrollY();
    // imgui and opengl have y flipped
    bottomY = topLeft.y;
    leftX   = topLeft.x;
    topY    = bottomY + windowSize.y;
    rightX  = leftX + windowSize.x;
    
    int textureId = Window.getFramebuffer().getTextureId();
    ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
    
    MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
    MouseListener.setGetGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
    
    ImGui.end();
  }
  
  public static boolean getWantCaptureMouse()
  {
    float mouseX = MouseListener.getX();
    float mouseY = MouseListener.getY();
    return mouseX > leftX && mouseX < rightX && mouseY > topY && mouseY < bottomY;
  }
  
  private static ImVec2 getLargestSizeForViewport()
  {
    ImVec2 windowSize = new ImVec2();
    ImGui.getContentRegionAvail(windowSize);
    windowSize.x -= ImGui.getScrollX();
    windowSize.y -= ImGui.getScrollY();
    
    float aspectWidth  = windowSize.x;
    float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
    
    if (aspectHeight > windowSize.y)
    {
      aspectHeight = windowSize.y;
      aspectWidth  = aspectHeight * Window.getTargetAspectRatio();
    }
    
    return new ImVec2(aspectWidth, aspectHeight);
  }
  
  private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
  {
    ImVec2 windowSize = new ImVec2();
    ImGui.getContentRegionAvail(windowSize);
    windowSize.x -= ImGui.getScrollX();
    windowSize.y -= ImGui.getScrollY();
    
    float viewportX = (windowSize.x / 2) - (aspectSize.x / 2);
    float viewportY = (windowSize.y / 2) - (aspectSize.y / 2);
    
    // add cursor pos to account for the ImGui title bar
    return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
  }
  
}
