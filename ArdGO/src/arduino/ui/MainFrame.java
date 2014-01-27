package arduino.ui;

import arduino.game.Game;
import arduino.game.GameNotifier;
import arduino.words.WordStorage;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
  public static final float NAMES_FONT_SIZE = 50.0f;
  public static final float WORD_FONT_SIZE = 100.0f;
  public static final String BUTTONS_ID = "buttons";
  public static final String STATUS_ID = "status";
  public static final String EMPTY_ID = "empty";
  public static final float BTN_TEXT_SIZE = 50.0f;
  private final List<JTextField> myNames = new ArrayList<JTextField>();
  private WordStorage myStorage = new WordStorage();
  private Game myGame = new Game(new MyGameNotifier());
  private CardLayout myStatusControl;
  private JPanel myStatusPanel;
  private JPanel myNamesPanel;
  private JLabel myWordLabel;

  public MainFrame() throws HeadlessException {
    super("Knopfy");

//    JMenuBar menu = new JMenuBar();
//    menu.add(new JMenuItem(new AbstractAction("Neue Spiel") {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        for (JTextField name:myNames){
//          name.getParent().remove(name);
//        }
//        myWordLabel.setText("Drücken die Knopfe");
//        myNames.clear();
//        myStorage = new WordStorage();
//        myGame.dispose();
//        myGame = new Game(new MyGameNotifier());
//      }
//    }));
//    setJMenuBar(menu);

    Toolkit tk = Toolkit.getDefaultToolkit();
    int xSize = ((int) tk.getScreenSize().getWidth());
    int ySize = ((int) tk.getScreenSize().getHeight());
    setSize(xSize, ySize);

    myNamesPanel = new JPanel();
    myNamesPanel.setLayout(new BoxLayout(myNamesPanel, BoxLayout.Y_AXIS));
    JComponent names = new JPanel(new BorderLayout());
    names.add(myNamesPanel,BorderLayout.NORTH);
    names.add(new JPanel(),BorderLayout.SOUTH);
    names.setMinimumSize(new Dimension(300, 600));
    names.setBorder(new EtchedBorder());

    JPanel roundControl = createRoundControl();
    roundControl.setMinimumSize(new Dimension(500, 600));
    roundControl.setBorder(new EtchedBorder());

    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, names, roundControl));
  }

  public void dispose() {
    myGame.dispose();
    super.dispose();
  }

  private JPanel createRoundControl() {
    myWordLabel = new JLabel("Drücken die Knopfe");
    myWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
    myWordLabel.setFont(myWordLabel.getFont().deriveFont(WORD_FONT_SIZE));

    final JLabel statusLabel = new JLabel(" ");
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statusLabel.setFont(statusLabel.getFont().deriveFont(WORD_FONT_SIZE));

    final JButton continueBtn = new JButton(new AbstractAction("Weiter") {
      public void actionPerformed(ActionEvent e) {
        resumeGame(statusLabel);
      }
    });
    continueBtn.setFont(continueBtn.getFont().deriveFont(BTN_TEXT_SIZE));
    continueBtn.setEnabled(false);

    JButton nextRoundBtn = new JButton(new AbstractAction("Nächste Runde") {
      public void actionPerformed(ActionEvent e) {
        myWordLabel.setText(myStorage.getNextWord());
        continueBtn.setEnabled(true);
        resumeGame(statusLabel);
      }
    });
    nextRoundBtn.setFont(nextRoundBtn.getFont().deriveFont(BTN_TEXT_SIZE));
    nextRoundBtn.setFocusable(true);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(nextRoundBtn);
    buttonPanel.add(continueBtn);

    JPanel btnOuter = new JPanel(new GridBagLayout());
    btnOuter.add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    myStatusControl = new CardLayout();
    myStatusPanel = new JPanel(myStatusControl);
    myStatusPanel.add(btnOuter, BUTTONS_ID);
    myStatusPanel.add(statusLabel, STATUS_ID);
    myStatusPanel.add(new JPanel(), EMPTY_ID);
    myStatusControl.show(myStatusPanel, EMPTY_ID);

    JPanel base = new JPanel();
    base.setLayout(new BorderLayout());
    base.add(myWordLabel, BorderLayout.CENTER);
    base.add(myStatusPanel, BorderLayout.SOUTH);
    return base;
  }

  private void resumeGame(final JLabel status) {
    for (JTextField t : myNames) {
      t.setEditable(false);
      t.setFocusable(false);
    }

    myStatusControl.show(myStatusPanel, STATUS_ID);

    for (int i = 0; i < myNames.size(); i++) {
      myNames.get(i).setBackground(Color.WHITE);
    }

    new Thread("game thread") {
      public void run() {
        myGame.allowPlayers(null);
        myGame.setIsCountdown(true);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          //nothing
        }
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
            status.setText(" "); //clear not to show next time
            myStatusControl.show(myStatusPanel, BUTTONS_ID);
          }
        });
        Beeper.playSound("start.wav");
        myGame.setIsCountdown(false);
      }
    }.start();
  }

  private class MyGameNotifier implements GameNotifier {
    @Override
    public void failed(final int playerNum) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          myNames.get(playerNum).setBackground(Color.RED);
        }
      });
      Beeper.playSound("fail.wav");

      boolean gameEnded = true;
      for (JTextField player : myNames) {
        if (player.getBackground() != Color.RED) {
          gameEnded = false;
        }
      }
      if (gameEnded) {
        myStatusControl.show(myStatusPanel, BUTTONS_ID);
      }
    }

    @Override
    public void won(final int playerNum, final int place) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JTextField p = myNames.get(playerNum);
          switch (place) {
            case 0:
              p.setBackground(Color.GREEN);
              break;
            case 1:
              p.setBackground(Color.YELLOW);
              break;
            case 2:
              p.setBackground(Color.ORANGE);
              break;
            default:
          }
        }
      });
    }

    @Override
    public void newPlayer(int newPlayerNum) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          myStatusControl.show(myStatusPanel, BUTTONS_ID);

          JTextField tf = new JTextField("");
          tf.setFont(tf.getFont().deriveFont(NAMES_FONT_SIZE));
          myNames.add(tf);
          myNamesPanel.add(tf);

          //todo this is a hack
          myNamesPanel.doLayout();
          myNamesPanel.getParent().doLayout();

          tf.requestFocus();
        }
      });
    }
  }
}
