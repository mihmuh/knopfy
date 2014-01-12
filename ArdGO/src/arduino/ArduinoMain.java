package arduino;

import arduino.ui.MainFrame;

import javax.swing.*;

public class ArduinoMain {
  public static void main(String[] args) throws Exception {
    MainFrame mf = new MainFrame();
    mf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mf.setVisible(true);
  }
}

