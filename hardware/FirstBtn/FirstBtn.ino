
const int playersCount = 8;
const int playerPorts [playersCount]= {
  2, 3, 4, 5, 6, 7, 8, 9};

boolean wasPressed [playersCount];

void setup() {           
  for (int i=0;i<playersCount;i++){
    wasPressed[i] = false;
  }
  for(int i = 0; i < playersCount; i++) {
    pinMode(playerPorts[i], INPUT_PULLUP);
  }
  Serial.begin(9600);
}

void loop() {
  //PC can clear button statuses
  while (Serial.available()){
    wasPressed[Serial.read()-48] = false;
  }

  for(int i = 0; i < playersCount; i++) {
    int w = digitalRead(playerPorts[i]);
    if (w == LOW && !wasPressed[i]) {
      Serial.print(i);
      wasPressed[i]=true;
    }
  }
}











