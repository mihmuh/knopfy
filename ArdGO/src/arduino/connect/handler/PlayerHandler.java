package arduino.connect.handler;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerHandler extends NumHandler {
  public static Map<Integer, Integer> ourMapping = new HashMap<Integer, Integer>();

  @Override
  public final void got(int num) {
    Integer realNum = ourMapping.get(num);
    if (realNum == null) return;
    gotPlayer(realNum);
  }

  protected abstract void gotPlayer(int playerNum);
}
