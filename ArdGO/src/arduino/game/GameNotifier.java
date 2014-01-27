package arduino.game;

public interface GameNotifier {
  void failed(int playerNum);
  void won(int playerNum, int place);
  void newPlayer(int newPlayerNum);
}
