
int master = 2;
int players [] = {
  3, 4, 5, 6, 7};
int playersCount = 5;

void setup() {                
//  pinMode(13, OUTPUT);
  pinMode(master, INPUT_PULLUP);
  for(int i = 0; i < playersCount; i++) {
    pinMode(players[i], INPUT_PULLUP);
  }
  Serial.begin(9600);
}

boolean hasWinner;
void loop() {
  int val = digitalRead(master);
  if(val==LOW){
    hasWinner=false;
    Serial.print(0);
    while(!Serial.available()){
    }
    Serial.read();
    
    while (!hasWinner){
      for(int i = 0; i < playersCount; i++) {
        int w = digitalRead(players[i]);
        if (w == LOW) {
          Serial.print(i+1);
          hasWinner=true;
        }
      }
    }
  }
  //delay(10);
}

