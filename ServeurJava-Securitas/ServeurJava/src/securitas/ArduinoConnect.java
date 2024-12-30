package securitas;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.fazecast.jSerialComm.*;

public class ArduinoConnect {
    private SerialPort connectionArduino;
    private volatile boolean running = true; // Contrôle global du thread
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(); // File pour les messages à envoyer
    private ArduinoMessageListener listener;

    public ArduinoConnect(String serialPortName, int baudRate) throws IOException, InterruptedException {
        connectionArduino = SerialPort.getCommPort(serialPortName);
        connectionArduino.setComPortParameters(baudRate, 8, 1, 0);

        if (!connectionArduino.openPort()) {
            throw new IOException("Erreur : Impossible d'ouvrir le port série.");
        }

        System.out.println("Port série ouvert avec succès.");
        connectionArduino.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);
        Thread.sleep(2000); // Donne le temps à la connexion série de s'initialiser
    }

    public void setListener(ArduinoMessageListener listener) {
        this.listener = listener;
    }

    public void startCommunication() {
        new Thread(() -> {
            try {
                communicateWithSerial();
            } catch (IOException e) {
                System.err.println("Erreur pendant la communication série : " + e.getMessage());
            }
        }).start();
    }

    private void communicateWithSerial() throws IOException {
        InputStream serialInput = connectionArduino.getInputStream();
        OutputStream serialOutput = connectionArduino.getOutputStream();
        byte[] buffer = new byte[22];

        while (running) {
            // Lecture depuis l'Arduino
            if (serialInput.available() > 21) {
                int bytesRead = serialInput.read(buffer);
                if (bytesRead > 0) {
                    String receivedMessage = new String(buffer, 0, bytesRead).trim();
                    System.out.println("Message reçu de l'Arduino : " + receivedMessage);

                    if (listener != null) {
                        listener.onMessageReceived(receivedMessage); // Remonte le message au thread principal
                    }
                }
            }

            // Écriture vers l'Arduino
            String messageToSend = messageQueue.poll(); // Récupère un message dans la file (ou null si vide)
            if (messageToSend != null) {
                serialOutput.write(messageToSend.getBytes());
                serialOutput.flush();
                System.out.println("Message envoyé à l'Arduino : " + messageToSend);
            }

            try {
                Thread.sleep(50); // Pause légère pour éviter une boucle trop rapide
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void sendToSerial(String message) {
        messageQueue.offer(message); // Ajoute le message à la file
    }

    public void stop() {
        running = false;
        if (connectionArduino != null && connectionArduino.isOpen()) {
            connectionArduino.closePort();
            System.out.println("Port série fermé.");
        }
    }

    public interface ArduinoMessageListener {
        void onMessageReceived(String message); // Interface callback
    }
}
