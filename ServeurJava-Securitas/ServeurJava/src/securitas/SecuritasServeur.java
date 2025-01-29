package securitas;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class SecuritasServeur {
	private SecuritasDatabaseManager M;
	private ArrayList<ServeurThread> client;
	
	public SecuritasServeur() {
		this.setClient(new ArrayList<ServeurThread>());
	}
	
    public void runServeur(int numPort, String serialPortName, int baudRate, String driverClassName, String databaseURL, String username, String password) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
		
        try (ServerSocket serverSocket = new ServerSocket(numPort)) {
			System.out.println("Serveur en écoute sur le port " + numPort);

			M = new SecuritasDatabaseManager(driverClassName, databaseURL, username, password);

			ArduinoConnect arduinoConnect = new ArduinoConnect(serialPortName, baudRate);

			// Implémente le callback pour traiter les messages reçus
			arduinoConnect.setListener(message -> {
			    System.out.println("Message remonté au thread principal : " + message);
			    if(!message.equals("Initialize System")) {

						String ordre = message.substring(0, 6).replace("#", "");
					    String contenu =  message.substring(6, 18).replace("#", "");
					    String parite1 =  message.substring(18, 19).replace("#", "");
					    String parite2 =  message.substring(19, 20).replace("#", "");
					    
					    if (ordre.equals("check")) {
					    	System.out.println("ohohoho1");
					    	try {
								if(M.checkmdp(contenu)) {
									System.out.println("ohohoho2");
									for (ServeurThread clientThread : this.client) {
							    		if(clientThread.getUUID().equals(M.getUUID(contenu))) {
							    			clientThread.printtext("auth");
							    		}else {System.out.println("ohohoho"+ (M.getUUID(contenu)));}
							    	}
								}
							} catch (UserPasDroitException e) {
								arduinoConnect.sendToSerial("refus#pasdroit####00");
								e.printStackTrace();
							} catch (MDPErronerException e) {
								arduinoConnect.sendToSerial("refus#mdperreur###00");
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }

			    }
			});

			// Lance la communication série en arrière-plan
			arduinoConnect.startCommunication();
			
			
			while (true) {
			    Socket clientSocket = serverSocket.accept();
			    System.out.println("Client connecté : " + clientSocket);

			    // Passer la connexion Arduino à chaque thread client
			    ServeurThread thread = new ServeurThread(clientSocket, arduinoConnect, this);
			    this.client.add(thread);
			    thread.start();
			}
		}
    }

    public void checkUUID(String UUID) throws IOException {
    	for (ServeurThread clientThread : this.client) {
    		if(clientThread.getUUID() == UUID) {
    			if(M.checkUUID(UUID)) {
    				if(M.checkAdmin(UUID).equals("true")) {
    					clientThread.printtext("UUIDRegisterAdmin");
    				}else {
    					clientThread.printtext("UUIDRegister");
    				}
    			}else {
    				clientThread.printtext("UUIDUUnRegister");
    			}
    		}
    	}
    }
    
    public void getautorisations(String UUID) throws IOException {
    	for (ServeurThread clientThread : this.client) {
    		if(clientThread.getUUID() == UUID) {
    			if((M.getautorisations(UUID)).equals("true")) {
    				clientThread.printtext("autorisationsTrue");
    			}else {
    				clientThread.printtext("autorisationsFalse");
    			}
    		}
    	}
    }
    
    public void getinfouser(String UUID) throws IOException {
    	for (ServeurThread clientThread : this.client) {
    		if(clientThread.getUUID() == UUID) {
    			clientThread.printtext("infouser:" + M.getInfoUser());
    		}
    	}
    }
    
    public void setdroitSerrure(String param) {
    	M.setdroitSerrure(param);
    }
    
    public void newEmployer(String UUID, String nom, String prenom, String mdp) {
    	M.insertEmployer(UUID, nom, prenom, mdp);
    }
    
    public void paramEmployer(String bool, String uuid) {
    	M.paramEmployer(bool, uuid);
    }
    
    public ArrayList<ServeurThread> getClient() {
		return client;
	}

	public void setClient(ArrayList<ServeurThread> client) {
		this.client = client;
	}
    
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        int numPort = 10001;
        String serialPortName = "COM7"; 
        int baudRate = 9600;
        String driverClassName = "com.mckoi.JDBCDriver";
		String databaseURL = "jdbc:mckoi://127.0.0.1/";
		String username = "admin";
		String password = "pass";

        SecuritasServeur serveur = new SecuritasServeur();
        serveur.runServeur(numPort, serialPortName, baudRate, driverClassName, databaseURL, username, password);
    }

	
}