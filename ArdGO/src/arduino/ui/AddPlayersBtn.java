package arduino.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

abstract class AddPlayersBtn extends JButton {
  private boolean myStarted = false;

  AddPlayersBtn() {
    setAction(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        myStarted = !myStarted;
        updateText();
        pressed(myStarted);
      }
    });
    updateText();
  }

  protected abstract void pressed(boolean starting);

  private void updateText() {
    setText(myStarted ? "Done" : "Edit Players");
  }
}
