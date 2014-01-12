package arduino.words;

import java.util.ArrayList;
import java.util.List;

public class WordStorage {
  private int index = 0;
  private List<String> myWords = new ArrayList<String>();
  private final List<String> words;

  public WordStorage() {
    //todo read
    words = new ArrayList<String>();
    words.add("word 1");
    words.add("word 2");
    words.add("word 3");

    reshuffle(words);
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
      reshuffle(words);
    }
    return myWords.get(index++);
  }
}
