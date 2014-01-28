package arduino.game;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

abstract class PortConnection implements SerialPortEventListener {
  public static final int ORD0 = 48 /*ord('0')*/;
  private static final int DATA_RATE = 9600;
  SerialPort serialPort;
  private InputStream input;
  private OutputStream output;

  public void init(String portName) {
    CommPortIdentifier portId = null;
    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

    // iterate through, looking for the port
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
      if (currPortId.getName().equals(portName)) {
        portId = currPortId;
        break;
      }
    }

    if (portId == null) {
      System.out.print("Could not find COM port.\nAvailable COM ports:\n");
      portEnum = CommPortIdentifier.getPortIdentifiers();

      // iterate through, looking for the port
      while (portEnum.hasMoreElements()) {
        CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
        System.out.println(currPortId.getName());
      }
      return;
    }

    try {
      // open serial port, and use class name for the appName.
      serialPort = (SerialPort) portId.open(this.getClass().getName(),
              2000);

      // set port parameters
      serialPort.setSerialPortParams(DATA_RATE,
              SerialPort.DATABITS_8,
              SerialPort.STOPBITS_1,
              SerialPort.PARITY_NONE);

      // open the streams
      input = serialPort.getInputStream();
      output = serialPort.getOutputStream();

      // add event listeners
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  public void dispose() {
    serialPort.close();
  }

  public synchronized void serialEvent(SerialPortEvent oEvent) {
    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      try {
        pressed(input.read() - ORD0);
      } catch (Exception e) {
        System.err.println(e.toString());
      }
    }
  }

  public void clear(int btnNum) {
    try {
      output.write(btnNum + ORD0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected abstract void pressed(int btnNum);
}
