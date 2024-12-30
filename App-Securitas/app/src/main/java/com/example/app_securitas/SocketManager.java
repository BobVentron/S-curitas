package com.example.app_securitas;

public class SocketManager {
    private static SocketManager instance;
    private ClientSocket clientSocket;

    // Singleton : Obtenir l'instance unique
    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    private SocketManager() {
        clientSocket = new ClientSocket();
    }

    public ClientSocket getClientSocket() {
        return clientSocket;
    }
}
