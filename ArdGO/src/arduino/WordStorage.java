package arduino;

import java.util.ArrayList;
import java.util.List;

public class WordStorage {
  private int index;
  private List<String> myWords = new ArrayList<String>();

  public WordStorage() {
    List<String> words = new ArrayList<String>();
    words.add("word 1");
    words.add("word 2");
    words.add("word 3");

    for (String word : words) {
      myWords.set(
              (int) Math.round(Math.random() * (myWords.size() - 1)),
              word
      );
    }
  }

  public String getNextWord() {
    return myWords.get(index++);
  }
}
