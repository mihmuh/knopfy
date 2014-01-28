package arduino.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstracts from button numbers and allows a high-level game control
 */
public class Game {
  private boolean myIsCountdown = false;
  private int myBestPlace = 0;
  private Map<Integer, Integer> myBtn2Player = new HashMap<Integer, Integer>();
  private GameNotifier myNotifier;
  private List<Integer> myNewBanned = new ArrayList<Integer>();
  private PortConnection myConnection = new PortConnection() {
    @Override
    protected void pressed(int btnNum) {
      buttonActuated(btnNum);
    }
  };

  public Game(String portName,GameNotifier myNotifier) {
    this.myNotifier = myNotifier;
    myConnection.init(portName);
  }

  public void dispose() {
    myConnection.dispose();
  }

  public synchronized void setIsCountdown(boolean isCountdown) {
    myIsCountdown = isCountdown;
    for (Integer btn : myNewBanned) {
      myConnection.clear(btn);
    }
    myNewBanned.clear();
  }

  public synchronized void allowPlayers(List<Integer> players) {
    for (Map.Entry<Integer, Integer> entry : myBtn2Player.entrySet()) {
      if (players == null || players.contains(entry.getValue())) {
        myConnection.clear(entry.getKey());
      }
    }
    myBestPlace = 0;
  }

  private synchronized void buttonActuated(int btnNum) {
    Integer playerNum = myBtn2Player.get(btnNum);
    if (playerNum == null) {
      if (myIsCountdown) {
        myNewBanned.add(btnNum);
        return;
      }
      //new player
      playerNum = myBtn2Player.size();
      myBtn2Player.put(btnNum, playerNum);
      myNotifier.newPlayer(playerNum);
      return;
    }

    if (myIsCountdown) {
      myNotifier.failed(playerNum);
    } else {
      myNotifier.won(playerNum, myBestPlace++);
    }
  }
}
