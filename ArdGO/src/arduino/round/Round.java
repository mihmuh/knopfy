package arduino.round;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Round {
  private State myState = State.NOT_STARTED;

  public void go() {
    myState = State.IS_ON;
  }

  public void pressed(int playerNum) {
    if (myState == State.NOT_STARTED) {
      failed(playerNum);
    } else if (myState == State.IS_ON) {
      myState = State.PAUSED;
      acted(playerNum);
    } else {
      //do nothing
    }
  }

  protected abstract void acted(int playerNum);

  protected abstract void failed(int playerNum);

  private enum State {NOT_STARTED, IS_ON, PAUSED}
}
