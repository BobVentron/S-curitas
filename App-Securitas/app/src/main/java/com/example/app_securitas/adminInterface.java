package com.example.app_securitas;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class adminInterface extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_interface);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                clientSocket.sendMessage("getinfouser");
            }
        }).start();

        ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
        if (clientSocket == null) {
            Toast.makeText(this, "Erreur : ClientSocket est null.", Toast.LENGTH_SHORT).show();
            return; // Quittez la méthode pour éviter un crash
        }

        List<List<String>> resultList = new ArrayList<>();

        clientSocket.setOnMessageReceivedListener(message -> runOnUiThread(() -> {
            if (message.equals("auth")) {
                navigateToAuthentification(); // Navigue vers une nouvelle activité
            }
            if (message.startsWith("infouser:")) {
                String infouser = message.replace("infouser:", "");
                String[] blocks = infouser.split("\\+");
                LinearLayout employeeListContainer = findViewById(R.id.employeeListContainer); // Conteneur des boutons

                for (String block : blocks) {
                    // Découper les informations
                    String[] parts = block.split("/");
                    if (parts.length == 3) { // Vérifie le format UUID/Nom/Prénom
                        String uuid = parts[0];
                        String nom = parts[1];
                        String prenom = parts[2];

                        // Ajouter à la liste (optionnel si besoin de conserver les données ailleurs)
                        resultList.add(Arrays.asList(uuid, nom, prenom));

                        // Créer et configurer le bouton
                        Button button = new Button(this);
                        button.setText(prenom + " " + nom);
                        button.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Ajouter un écouteur qui transmet l'UUID à l'activité suivante
                        button.setOnClickListener(v -> {
                            Intent intent = new Intent(adminInterface.this, paramEmploye.class);
                            intent.putExtra("employeeUUID", uuid);
                            intent.putExtra("nom", nom);
                            intent.putExtra("prenom", prenom);
                            startActivity(intent);
                        });

                        // Ajouter le bouton au conteneur
                        employeeListContainer.addView(button);
                    }
                }
            }
        }));



        Button buttonParamSerrure = findViewById(R.id.paramSerrure);
        buttonParamSerrure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToparamSerrure();
            }
        });

        Button buttonNewEmploye = findViewById(R.id.newEmploye);
        buttonNewEmploye.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateTonewEmploye();
            }
        });

    }

    private void navigateToparamSerrure() {
        Intent intent = new Intent(adminInterface.this, paramSerrure.class);
        startActivity(intent);
    }

    private void navigateTonewEmploye() {
        Intent intent = new Intent(adminInterface.this, newEmploye.class);
        startActivity(intent);
    }

    private void navigateToAuthentification() {
        Intent intent = new Intent(adminInterface.this, AuthentificationBiometric.class);
        startActivity(intent);
    }
}