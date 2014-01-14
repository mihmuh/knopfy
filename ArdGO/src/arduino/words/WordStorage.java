package arduino.words;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordStorage {
  private int index = 0;
  private List<String> myWords = new ArrayList<String>();

  public WordStorage() {
    try {
      BufferedReader in = new BufferedReader(new FileReader("words.txt"));
      List<String> words = new ArrayList<String>();

      String s;
      while ((s = in.readLine()) != null) {
        words.add(s);
      }
      reshuffle(words);
    } catch (IOException e) {
      e.printStackTrace();
      myWords.clear();
      myWords.add("No words loaded");
    }
  }

  private void reshuffle(List<String> words) {
    for (String word : words) {
      myWords.add(
              (int) Math.round(Math.random() * myWords.size()),
              word
      );
    }
  }

  public String getNextWord() {
    if (index == myWords.size()) {
      index = 0;
      reshuffle(new ArrayList<String>(myWords));
    }
    return myWords.get(index++);
  }
}
