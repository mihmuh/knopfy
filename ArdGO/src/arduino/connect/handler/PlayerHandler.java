package arduino.connect.handler;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerHandler extends NumHandler {
  public static Map<Integer, Integer> ourMapping = new HashMap<Integer, Integer>();

  @Override
  public final void got(int num) {
    gotPlayer(ourMapping.get(num));
  }

  protected abstract void gotPlayer(int playerNum);
}
