package arduino.connect;

import arduino.connect.handler.NumHandler;

public class Connections {
  private static Connections instance = null;
  private NumHandler myHandler;

  public static Connections getInstance(){
     if (instance==null){
       instance = new Connections();
     }
    return instance;
  }

  private Connections() {

  }

  //-----------------------------------------------

  private PortConnection myOpenConnection = null;

  public void startNewSession(final NumHandler handler){
    if (myOpenConnection==null){
      myOpenConnection = new PortConnection() {
        @Override
        protected void got(int num) {
          myHandler.got(num);
        }
      };
      myOpenConnection.init();
    }

    myHandler = handler;
    myOpenConnection.start();
  }

  public void dispose(){
    if (myOpenConnection != null) {
      myOpenConnection.dispose();
    }
  }
}
