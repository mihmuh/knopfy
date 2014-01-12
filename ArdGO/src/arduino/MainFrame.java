package arduino;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
  private final JLabel myOutput;
  private List<JTextField> myNames = new ArrayList<JTextField>();

  private PortHandler ph = new PortHandler() {
    @Override
    protected void got(int arg) {
      if (arg == 0) {
        for (int i = 3; i > 0; i--) {
          myOutput.setText("" + i);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        myOutput.setText("GO!");
        start();
        return;
      }
      myOutput.setText(myNames.get(arg).getText());
    }
  };


  public MainFrame() throws HeadlessException {
    super("HelloArduino");
    setSize(400, 600);
    setLayout(new BorderLayout());

    JPanel base = new JPanel();
    base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
    add(base, BorderLayout.NORTH);

    addButtonControls(base, "A");
    addButtonControls(base, "B");
    addButtonControls(base, "C");
    addButtonControls(base, "D");
    addButtonControls(base, "E");
    addButtonControls(base, "F");

    myOutput = new JLabel("Press main button to start");
    myOutput.setFont(myOutput.getFont().deriveFont(75.0f));
    add(myOutput, BorderLayout.SOUTH);
    ph.init();
  }

  private void addButtonControls(JComponent base, String name) {
    JPanel p = new JPanel(new BorderLayout());

    JTextField tf = new JTextField(name);
    myNames.add(tf);
    p.add(tf, BorderLayout.CENTER);

    p.add(new JLabel("Player " + myNames.size()), BorderLayout.WEST);

    base.add(p);
  }

  @Override
  public void dispose() {
    ph.dispose();
    super.dispose();
  }
}
