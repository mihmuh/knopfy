
const int playersCount = 6;
const int playerPorts [playersCount]= {
  2, 3, 4, 5, 6, 7};

boolean wasPressed [playersCount];

void setup() {           
  clearBtnStatuses();  
  for(int i = 0; i < playersCount; i++) {
    pinMode(playerPorts[i], INPUT_PULLUP);
  }
  Serial.begin(9600);
}

void loop() {
  //PC can clear button statuses
  if (Serial.available()){
    Serial.read();
    clearBtnStatuses();
  }

  for(int i = 0; i < playersCount; i++) {
    int w = digitalRead(playersPorts[i]);
    if (w == LOW && !wasPressed[i]) {
      Serial.print(i);
      wasPressed[i]=true;
    }
  }
}

void clearBtnStatuses(){
  for (int i=0;i<playersCount;i++){
    wasPressed[i] = false;
  }
}







