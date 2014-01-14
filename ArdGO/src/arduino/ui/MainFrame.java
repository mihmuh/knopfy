package arduino.ui;

import arduino.connect.Connections;
import arduino.connect.handler.NumHandler;
import arduino.connect.handler.PlayerHandler;
import arduino.round.Round;
import arduino.words.WordStorage;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
  public static final float NAMES_FONT_SIZE = 50.0f;
  public static final float WORD_FONT_SIZE = 100.0f;
  public static final String BUTTONS_ID = "buttons";
  public static final String STATUS_ID = "status";
  public static final String EMPTY_ID = "empty";

  private final List<JTextField> myNames = new ArrayList<JTextField>();
  private Round myRound = null;
  private WordStorage myStorage = new WordStorage();
  private CardLayout myStatusControl;
  private JPanel myStatusPanel;

  public MainFrame() throws HeadlessException {
    super("Deutsche wörter");

    Toolkit tk = Toolkit.getDefaultToolkit();
    int xSize = ((int) tk.getScreenSize().getWidth());
    int ySize = ((int) tk.getScreenSize().getHeight());
    setSize(xSize, ySize);

    JComponent names = createNamesControl();
    names.setMinimumSize(new Dimension(300, 600));
    names.setBorder(new EtchedBorder());

    JPanel roundControl = createRoundControl();
    roundControl.setMinimumSize(new Dimension(500, 600));
    roundControl.setBorder(new EtchedBorder());

    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, names, roundControl));
  }

  public void dispose() {
    Connections.getInstance().dispose();
    super.dispose();
  }

  private JComponent createNamesControl() {
    final JPanel base = new JPanel();
    base.setLayout(new BorderLayout());

    final JPanel namesPanel = new JPanel();
    namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
    JButton setBtn = new JButton(new AbstractAction("Add Players") {
      public void actionPerformed(ActionEvent e) {
        Connections.getInstance().startNewSession(new NumHandler() {
          public void got(final int num) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                myStatusControl.show(myStatusPanel, BUTTONS_ID);
                PlayerHandler.ourMapping.put(num, myNames.size());

                JTextField tf = new JTextField("");
                tf.setFont(tf.getFont().deriveFont(NAMES_FONT_SIZE));
                myNames.add(tf);
                namesPanel.add(tf);

                //todo this is a hack
                namesPanel.doLayout();
                base.doLayout();

                tf.requestFocus();
              }
            });
          }
        });
      }
    });

    base.add(namesPanel, BorderLayout.NORTH);
    base.add(setBtn, BorderLayout.SOUTH);
    base.add(new JPanel(), BorderLayout.CENTER);

    return base;
  }

  private JPanel createRoundControl() {
    final JLabel word = new JLabel("Add players, start");
    word.setHorizontalAlignment(SwingConstants.CENTER);
    word.setFont(word.getFont().deriveFont(WORD_FONT_SIZE));

    final JLabel statusLabel = new JLabel(" ");
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statusLabel.setFont(statusLabel.getFont().deriveFont(WORD_FONT_SIZE));

    final JButton continueBtn = new JButton(new AbstractAction("Continue") {
      public void actionPerformed(ActionEvent e) {
        resumeGame(statusLabel);
      }
    });
    continueBtn.setEnabled(false);

    JButton nextRoundBtn = new JButton(new AbstractAction("Next Round") {
      public void actionPerformed(ActionEvent e) {
        word.setText(myStorage.getNextWord());
        continueBtn.setEnabled(true);
        resumeGame(statusLabel);
      }
    });
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
    base.add(word, BorderLayout.CENTER);
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
        myRound = new Round() {
          private int myFailedNum = 0;

          protected void acted(final int playerNum) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                myNames.get(playerNum).setBackground(Color.GREEN);
                myStatusControl.show(myStatusPanel, BUTTONS_ID);
              }
            });
          }

          protected void failed(final int playerNum) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                myNames.get(playerNum).setBackground(Color.RED);
              }
            });
            Beeper.playSound("fail.wav");

            myFailedNum++;
            if (myFailedNum == myNames.size()) {
              myStatusControl.show(myStatusPanel, BUTTONS_ID);
            }
          }
        };

        Connections.getInstance().startNewSession(new PlayerHandler() {
          protected void gotPlayer(int playerNum) {
            myRound.pressed(playerNum);
          }
        });

        //todo don't tick when interrupted
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

        myRound.go();
      }
    }.start();
  }
}
