#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#define WIFI_SSID "Dogs'Home"   
#define WIFI_PASSWORD "cal"
#define FIREBASE_HOST "smarthome-d09c4.firebaseio.com"
#define FIREBASE_AUTH ""   //Không dùng xác thực nên không đổi



//LED, MOTOR
#define FAN 5
#define LED 14
#define LEDNOTIFY  2

#define ledValuePath "/LED"
#define fanValuePath "/FAN"

int ledValue;
int fanValue;

void connectFirebase() {
  WiFi.disconnect();
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("connecting");
  while (WiFi.waitForConnectResult() != WL_CONNECTED) {
     Serial.print(".");
     digitalWrite(LEDNOTIFY, HIGH);
     delay(500);
     digitalWrite(LEDNOTIFY, LOW);
     delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  digitalWrite(LEDNOTIFY, LOW);
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
 
}

void checkOthers(){

 ledValue = Firebase.getInt(ledValuePath);
 fanValue = Firebase.getInt(fanValuePath);
 

  digitalWrite(LED,ledValue);
  digitalWrite(FAN, fanValue); 
 
}

void setup() {
 
  Serial.begin(115200, SERIAL_8N1, SERIAL_TX_ONLY);
  Serial.println("Start Setup");

  pinMode(FAN, OUTPUT);
  pinMode(LEDNOTIFY, OUTPUT);
  pinMode(LED, OUTPUT);

  digitalWrite(FAN, LOW);
  digitalWrite(LEDNOTIFY, LOW);
  digitalWrite(LED, LOW);

  connectFirebase();

  Serial.println("End Setup");
}

void loop() {
  if (Firebase.failed()) {
      Serial.print("Failed to connect Firebase:");
      Serial.println(Firebase.error());  
      return;
  } 
    checkOthers();
}
