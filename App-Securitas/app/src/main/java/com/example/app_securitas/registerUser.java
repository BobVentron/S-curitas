package com.example.app_securitas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

import java.util.concurrent.Executors;

public class registerUser extends AppCompatActivity {
    //private ClientSocket clientSocket;
    private TextView listeAuto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listeAuto = findViewById(R.id.autorisation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                clientSocket.sendMessage("getautorisations");
            }
        }).start();

        ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
        if (clientSocket == null) {
            Toast.makeText(this, "Erreur : ClientSocket est null.", Toast.LENGTH_SHORT).show();
            return; // Quittez la méthode pour éviter un crash
        }
        clientSocket.setOnMessageReceivedListener(message -> runOnUiThread(() -> {
            if (message.equals("auth")) {
                navigateToAuthentification(); // Navigue vers une nouvelle activité
            }
            if (message.equals("autorisationsTrue")){
                listeAuto.setText("Vous avez le droit de devéroullier la serrure");
            }
            if (message.equals("autorisationsFalse")){
                listeAuto.setText("Vous n'avez pas le droit de devéroullier la serrure");
            }
        }));



    }

    private void navigateToAuthentification() {
        Intent intent = new Intent(registerUser.this, AuthentificationBiometric.class);
        startActivity(intent);
    }
}