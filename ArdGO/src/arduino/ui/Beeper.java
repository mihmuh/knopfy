package arduino.ui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Beeper {
  public static void playSound(final String url) {
    new Thread(new Runnable() {
      // The wrapper thread is unnecessary, unless it blocks on the
      // Clip finishing; see comments.
      public void run() {
        try {
          Clip clip = AudioSystem.getClip();
          AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                  Beeper.class.getResourceAsStream(url));
          clip.open(inputStream);
          clip.start();
        } catch (Exception e) {
          System.err.println(e.getMessage());
        }
      }
    }).start();
  }
}