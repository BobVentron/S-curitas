package com.example.app_securitas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private OnMessageReceivedListener listener;

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    public void connectToServer(String serverIP, int serverPort) {
        new Thread(() -> {
            try {
                socket = new Socket(serverIP, serverPort);
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println("Connexion réussie au serveur : " + serverIP + ":" + serverPort);

                listenServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void listenServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Message reçu du serveur : " + response);
                if (listener != null) {
                    listener.onMessageReceived(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Message envoyé : " + message);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
