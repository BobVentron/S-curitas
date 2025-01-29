#include <ShiftLcd.h>
#include <Servo.h>
#include <Keypad.h>

//configuration des pins pour l'écran LCD
const int LCD_EN = 2;
const int LCD_D7 = 3;
const int SHIFT_SER = 4;
const int SHIFT_SRCLK = 5;

//crait un instance de l'écran avec les pins parametrer 
ShiftLcd lcd(LCD_EN, LCD_D7, SHIFT_SER, SHIFT_SRCLK);

Servo servo;

//Constants
#define ROWS 4
#define COLS 4

//Parameters
const char kp4x4Keys[ROWS][COLS]  = {{'1', '2', '3', 'A'}, {'4', '5', '6', 'B'}, {'7', '8', '9', 'C'}, {'*', '0', '#', 'D'}};
byte rowKp4x4Pin [4] = {13, 12, 11, 10};
byte colKp4x4Pin [4] = {9, 8, 7, 6};

Keypad kp4x4  = Keypad(makeKeymap(kp4x4Keys), rowKp4x4Pin, colKp4x4Pin, ROWS, COLS);

bool PositionMoteurOuvert = false;
bool PositionLedVerte = false;
int codeUtilisateurErrone = 0;

void setup() {
  lcd.begin();//initialisation de l'écran
  pinMode(A5, OUTPUT); //led verte
  pinMode(A4, OUTPUT); //led rouge
  servo.attach(A0); // attache le servo au pin spécifié
  servo.write(0);

  Serial.begin(9600);
  Serial.println(F("Initialize System!!!"));
  AfficherMessage("Initialize", "System.");
  delay(1000);
  AfficherMessage("Initialize", "System..");
  delay(1000);
  AfficherMessage("Initialize", "System...");
  delay(1000);
}

void loop() {
  
  AfficherMessage("Entrez votre mot", "de passe");
  repeatMdp:
  String mdpUtilisateur = "";
  while(true){
    String mdpUser = readKp4x4();
    mdpUtilisateur = mdpUser;
    if(mdpUser != ""){break;}
  }
  AfficherMessage("Mot de passe :" , mdpUtilisateur);// --------------------------------------------------debug message

  repeatCheckUser:
  sendData("check", mdpUtilisateur);
  String dataRecu = readData();

  if (!checkErreur(dataRecu)){goto repeatCheckUser;}

  if (suprBourage(dataRecu.substring(0, 6)) == "accord"){
    if (suprBourage(dataRecu.substring(6, 18)) == "ouverte") {
      AfficherMessage("Porte ouverte");
      LedVerteControl();
      MoteurControl();
      delay(10000);
      LedVerteControl();
      MoteurControl();
    } else if (suprBourage(dataRecu.substring(6, 18)) == "validation") {
      AfficherMessage("Porte déverrouillée");
      LedVerteControl();
      MoteurControl();
      delay(10000);
      LedVerteControl();
      MoteurControl();
    } else {
      AfficherMessage("Une erreur est", "survenue");
      LedRougeControl();
    }
  }else if (suprBourage(dataRecu.substring(0, 6)) == "refus"){
    if (suprBourage(dataRecu.substring(6, 18)) == "bloquer") {
      AfficherMessage("Porte bloquée pour", "tout le monde");
      LedRougeControl();
    } else if (suprBourage(dataRecu.substring(6, 18)) == "mdperreur") {
      if (codeUtilisateurErrone == 2){
        AfficherMessage("Mot de passe", "definitivement erroneé");
        LedRougeControl();
      }else{
        AfficherMessage("Erreur dans le", "mot de passe");
        LedRougeControl();
        codeUtilisateurErrone = codeUtilisateurErrone +1;
        AfficherMessage("Veuillez réesayer : ");
        goto repeatMdp;
      }
    } else if (suprBourage(dataRecu.substring(6, 18)) == "a2ferreur") {
      AfficherMessage("Erreur dans la", "double authentification");
      LedRougeControl();
    } else if (suprBourage(dataRecu.substring(6, 18)) == "pasdroit") {
      AfficherMessage("Vous n'avez pas", "le droit");
      LedRougeControl();
    } else {
      AfficherMessage("Une erreur inattendue", "est survenue");
      LedRougeControl();
    }
  }else{
    AfficherMessage("Une erreur est" , "survenue");
    LedRougeControl();
  }
  delay(5000);
}

/**
 * Fonction qui permet l'affichiage d'un text
 * Sur la premier ligne de l'écran LCD
 */
void AfficherMessage(String text){
  lcd.clear();
  AfficherMessage(text, "");
}

/**
 * Fonction qui premet d'afficher du text
 * sur les 2 ligne de l'ecran LCD
 */
void AfficherMessage(String text1, String text2){
  lcd.clear();
  lcd.write(text1, text2);
}

/**
 * Fonction qui permet de controler le moteur 
 * qui simule la sérrure. 
 */
void MoteurControl(){
  if(PositionMoteurOuvert){//si c'est ouvert
    servo.write(0);//on ferme
    PositionMoteurOuvert = false;
  }else { // sinon (si c'est fermé)
    servo.write(90); // on ouvre
    PositionMoteurOuvert = true;
  }
}

void LedVerteControl(){
  if(PositionLedVerte){
    digitalWrite(A5, LOW);
    PositionLedVerte = false;
  }else{
    digitalWrite(A5, HIGH);
    PositionLedVerte = true;
  }
}

void LedRougeControl(){
  digitalWrite(A4, HIGH);
  delay(5000);
  digitalWrite(A4, LOW);
}

/**
 * fonction permetant de lire le code taper 
 * sur le numpad par l'utilisateur
 */
String readKp4x4() {
  String message = ""; // Chaîne pour stocker le message
  unsigned long lastKeyTime = millis(); // Variable pour mémoriser le dernier moment où une touche a été pressée
  unsigned long timeout = 5000; // Délai de 5 secondes
  int maxLength = 12; // Limite de 12 caractères

  while (message.length() < maxLength) {
    char customKey = kp4x4.getKey(); // Lire l'état du clavier
    if (customKey) {
      message += customKey; // Ajouter la touche à la chaîne
      lastKeyTime = millis(); // Mettre à jour le moment de la dernière touche pressée
    }

    // Vérifier si le délai de 5 secondes est écoulé sans nouvelle touche
    if (millis() - lastKeyTime > timeout) {
      return message;
    }
  }

  return message; // Retourner le message reçu
}

/** 
 * fonction qui permet de lire des données
 * envoyer sur la liaison serie, notament
 * par le serveur metier JAVA
 */
String readData(){
  String recvData = "";
  while(true){
    if (Serial.available() > 0) {
      String data = Serial.readStringUntil('\n'); // Lire les données reçues
      if (data.length() > 0) {
        recvData = data;
        break;
      }
   }
  }
  return recvData;
}

/**
 * Fonction qui permet d'anvoyer des données
 * sur la liaison série. Les données sont
 * formaté correctement avant d'être envoyer
 */
void sendData(String ordre, String contenu){
  while (ordre.length() < 6) {
    ordre += "#";
  }

  while (contenu.length() < 12) {
    contenu += "#";
  }

  String binaryOrdre = stringToBBinaire(ordre);
  String binaryContenu = stringToBBinaire(contenu);

  String FianalBinaire = "";
  if (checkParite(binaryOrdre)){
    FianalBinaire +=0;
  }else{
    FianalBinaire += 1;
  }

  if (checkParite(binaryContenu)){
    FianalBinaire +=0;
  }else{
    FianalBinaire += 1;
  }

  String message = ordre + contenu + FianalBinaire;
  Serial.println(message);
}

/** 
 * Fonction qui permet de convertir un chaine
 * en binaire
 */
String stringToBBinaire(String chaine) {
  String binaryRepresentation = "";
  for (int i = 0; i < chaine.length(); i++) {
    byte c = chaine.charAt(i);
    String b = "";
    for (int i = 7; i >= 0; i--) {
      b += bitRead(c, i);  // Lit le i-ème bit
    }
    binaryRepresentation += b; 
  }
  return binaryRepresentation;
}

/**
 * Fonction qui renvoie True si la chaine
 * est paire et false si elle est impaire
 */
bool checkParite(String chaineBinaire){
  int count = 0;
  for (int i = 0; i < chaineBinaire.length(); i++) {
    if (chaineBinaire[i] == '1') {
      count++;
    }
  }

  return (count % 2 == 0);
}

/**
 * Fonction qui permet de confirmer que le message 
 * recu n'a pas été déformer grâce a la partié 
 */
bool checkErreur(String dataRecu) {
  String ordre =  dataRecu.substring(0, 6);
  String contenu =  dataRecu.substring(6, 18);
  String parite1 =  dataRecu.substring(18, 19);
  String parite2 =  dataRecu.substring(19, 20);
  AfficherMessage(ordre, contenu); // --------------------------------------------------debug message
  if (((checkParite(ordre) && parite1 == "0") || (!checkParite(ordre) && parite1 == "1")) && ((checkParite(contenu) && parite2 == "0") || (!checkParite(contenu) && parite2 == "1"))) {
    return true;
  }else{
    return false;
  }
}

/**
 * Fonction qui permet d'enlever les #
 * dans les messages recus
 */
String suprBourage(String message){
  while (message.endsWith("#")) {
      message.remove(message.length() - 1);
    }
  return message;
}

//https://github.com/eb1992/ShiftLcd
//https://arduino-france.site/servo-arduino/