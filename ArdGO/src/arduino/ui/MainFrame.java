package arduino.ui;

import arduino.connect.Connections;
import arduino.connect.handler.NumHandler;
import arduino.connect.handler.PlayerHandler;
import arduino.round.Round;
import arduino.words.WordStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
  public static final float NAMES_FONT_SIZE = 50.0f;
  public static final float WORD_FONT_SIZE = 90.0f;

  private final List<JTextField> myNames = new ArrayList<JTextField>();
  private Round myRound = null;
  private WordStorage myStorage = new WordStorage();
  private JButton myNextRoundBtn;
  private JButton myContinueBtn;

  public MainFrame() throws HeadlessException {
    super("Deutsche wörter");
    setSize(800, 600);

    JComponent names = createNamesControl();
    names.setMinimumSize(new Dimension(300, 600));

    Component roundControl = createRoundControl();
    roundControl.setMinimumSize(new Dimension(500, 600));

    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, names, roundControl));
  }

  public void dispose() {
    Connections.getInstance().dispose();
    super.dispose();
  }

  private JComponent createNamesControl() {
    final JPanel namesPanel = new JPanel();
    namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
    JButton setBtn = new AddPlayersBtn() {
      protected void pressed(boolean starting) {
        for (int i=0;i<myNames.size();i++){
          myNames.get(i).setEditable(starting);
        }

        if (!starting) return;
        Connections.getInstance().startNewSession(new NumHandler() {
          public void got(final int num) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                PlayerHandler.ourMapping.put(num, myNames.size());

                JTextField tf = new JTextField("");
                tf.setFont(tf.getFont().deriveFont(NAMES_FONT_SIZE));
                myNames.add(tf);
                namesPanel.add(tf);
                namesPanel.doLayout();
                tf.requestFocus();
              }
            });
          }
        });
      }
    };

    JPanel base = new JPanel();
    base.setLayout(new BorderLayout());

    base.add(namesPanel, BorderLayout.CENTER);
    base.add(setBtn, BorderLayout.SOUTH);

    return base;
  }

  private Component createRoundControl() {
    final JLabel word = new JLabel();
    word.setFont(word.getFont().deriveFont(WORD_FONT_SIZE));

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

    final JLabel status = new JLabel();
    status.setFont(status.getFont().deriveFont(WORD_FONT_SIZE));

    myNextRoundBtn = new JButton(new AbstractAction("Next Round") {
      public void actionPerformed(ActionEvent e) {
        word.setText(myStorage.getNextWord());
        resumeGame(status);
      }
    });
    buttonPanel.add(myNextRoundBtn);
    myContinueBtn = new JButton(new AbstractAction("Continue") {
      public void actionPerformed(ActionEvent e) {
        resumeGame(status);
      }
    });
    buttonPanel.add(myContinueBtn);

    JPanel base = new JPanel();
    base.setLayout(new BorderLayout());
    base.add(word, BorderLayout.CENTER);
    base.add(buttonPanel, BorderLayout.NORTH);
    base.add(status, BorderLayout.SOUTH);
    return base;
  }

  private void resumeGame(final JLabel status) {
    myNextRoundBtn.setEnabled(false);
    myContinueBtn.setEnabled(false);

    for (int i = 0; i < myNames.size(); i++) {
      myNames.get(i).setBackground(Color.WHITE);
    }

    new Thread("game thread") {
      public void run() {
        myRound = new Round() {
          protected void acted(final int playerNum) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                myNames.get(playerNum).setBackground(Color.GREEN);
                myNextRoundBtn.setEnabled(true);
                myContinueBtn.setEnabled(true);
              }
            });
          }

          protected void failed(final int playerNum) {
            //todo if all of them fail, enable controls
            //todo save names on restart
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                myNames.get(playerNum).setBackground(Color.RED);
              }
            });
            Beeper.playSound("fail.wav");
          }
        };

        Connections.getInstance().startNewSession(new PlayerHandler() {
          protected void gotPlayer(int playerNum) {
            myRound.pressed(playerNum);
          }
        });

        for (int i = 3; i > 0; i--) {
          Beeper.playSound("tick.wav");
          final int finalI = i;
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              status.setText("" + finalI);
            }
          });
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            //nothing
          }
        }
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            status.setText("0");
          }
        });
        Beeper.playSound("start.wav");

        myRound.go();
      }
    }.start();
  }
}
