package securitas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServeurThread  extends Thread {
	private Socket service;
	private ArduinoConnect arduinoConnect;
	private String UUID;
	private SecuritasServeur serveur;
	
	public ServeurThread(Socket s, ArduinoConnect arduinoConnect, SecuritasServeur serveur) {
		this.setService(s);
		this.setArduinoConnect(arduinoConnect);
		this.setUUID(null);
		this.setServeur(serveur);
	}
	
	public Socket getService() {
		return service;
	}

	public void setService(Socket service) {
		this.service = service;
	}
	
	public void printtext(String text) throws IOException {
		PrintStream out =  new PrintStream (service.getOutputStream(), true) ;
		out.println(text);
		System.out.println(text);
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader (service.getInputStream ()));
			PrintStream out =  new PrintStream (service.getOutputStream(), true);
			
			boolean quit = false;
			while (!quit )
			{
				
				// on attend une requête
				String requete = in.readLine();
				System.out.println("requete : " + requete);
				if(requete != null) {
					if (requete.startsWith("UUID")) {
						
						this.setUUID(requete.replace("UUID:", ""));
						this.serveur.checkUUID(this.UUID);
					}
					if (requete.equals("authGood")) {
						arduinoConnect.sendToSerial("accordouverte#####00");
					}
					if (requete.equals("getautorisations")) {
						this.serveur.getautorisations(this.UUID);
					}
					if (requete.equals("getinfouser")) {
						this.serveur.getinfouser(this.UUID);
					}
					if (requete.startsWith("paramSerrure")) {
						this.serveur.setdroitSerrure(requete.replace("paramSerrure", ""));
					}
					if (requete.startsWith("newEmployer")) {
						String[] dataParts = requete.split(",");


				        String uuid = "";
				        String name = "";
				        String firstName = ""; 
				        String password = "";


				        for (String part : dataParts) {
				            String[] keyValue = part.split(":");
				            if (keyValue.length == 2) {
				                switch (keyValue[0]) {
				                    case "newEmployerUUID":
				                        uuid = keyValue[1];
				                        break;
				                    case "Nom":
				                        name = keyValue[1];
				                        break;
				                    case "Prénom":
				                        firstName = keyValue[1];
				                        break;
				                    case "MDP":
				                        password = keyValue[1];
				                        break;
				                    default:

				                        break;
				                }
				            }
				        }
				        
				        this.serveur.newEmployer(uuid, name, firstName, password);
					}
					if (requete.startsWith("paramEmployer")) {
			        	int plusIndex = requete.indexOf('+');


			            String paramEmployer = requete.substring(0, plusIndex);
			            String uuid1 = requete.substring(plusIndex + 1);
			            String trueOrFalse = paramEmployer.substring("paramEmployer".length());
			            
			            this.serveur.paramEmployer(trueOrFalse, uuid1);
			            System.out.println("uuid : " + uuid1);
			        }

				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArduinoConnect getArduinoConnect() {
		return arduinoConnect;
	}

	public void setArduinoConnect(ArduinoConnect arduinoConnect) {
		this.arduinoConnect = arduinoConnect;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public SecuritasServeur getServeur() {
		return serveur;
	}

	public void setServeur(SecuritasServeur serveur) {
		this.serveur = serveur;
	}
}
