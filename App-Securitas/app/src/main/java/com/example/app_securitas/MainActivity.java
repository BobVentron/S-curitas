package com.example.app_securitas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private ClientSocket clientSocket;
    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    private TextView tvValeur;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String uniqueID = getUniqueID(this);
        String textUUID = "Votre UUID: " + uniqueID;

        tvValeur = findViewById(R.id.uuid);
        tvValeur.setText(textUUID);

        ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
        clientSocket.setOnMessageReceivedListener(message -> runOnUiThread(() -> {
            if (message.equals("UUIDRegister")) {
                navigateToSuccessPage();
            } else if (message.equals("UUIDRegisterAdmin")) {
                navigateToSuccessPageAdmin();
            } else if (message.equals("UUIDUnRegister")) {
                Toast.makeText(this, "UUID non enregistré", Toast.LENGTH_SHORT).show();
            }
        }));

        // Démarrer la connexion
        Executors.newSingleThreadExecutor().submit(() -> {
            String serverIP = "192.168.157.117";  // Adresse IP du serveur
            int serverPort = 10001;            // Port du serveur
            clientSocket.connectToServer(serverIP, serverPort);

            try {
                Thread.sleep(100);  // Pause pour éviter les conflits de démarrage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            clientSocket.sendMessage("UUID:" + uniqueID);
        });
    }

    private void navigateToSuccessPage() {
        Intent intent = new Intent(MainActivity.this, registerUser.class);
        startActivity(intent);
        finish(); // Facultatif si vous voulez fermer MainActivity
    }

    private void navigateToSuccessPageAdmin() {
        Intent intent = new Intent(MainActivity.this, adminInterface.class);
        startActivity(intent);
        finish(); // Facultatif si vous voulez fermer MainActivity
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Déconnexion lors de la fermeture de l'application
//        clientSocket.disconnect();
//        executorService.shutdown();  // Ferme le thread pool lorsque l'activité est détruite
//    }

    public static String getUniqueID(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String uniqueID = sharedPrefs.getString(PREFS_DEVICE_ID, null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(PREFS_DEVICE_ID, uniqueID);
            editor.apply();
        }
        return uniqueID;
    }
}