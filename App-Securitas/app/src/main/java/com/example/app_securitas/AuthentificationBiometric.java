package com.example.app_securitas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class AuthentificationBiometric extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentification_biometric);

        // Ajustement des marges pour le système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser l'authentification biométrique
        initBiometricAuthentication();
    }

    private void initBiometricAuthentication() {
        BiometricManager biometricManager = BiometricManager.from(this);

        // Vérifiez si la biométrie est disponible
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt(); // Lancer l'authentification
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Aucun capteur biométrique détecté.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Le capteur biométrique est indisponible.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Aucune empreinte n'est enregistrée sur cet appareil.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void showBiometricPrompt() {
        // Executor pour exécuter les tâches liées à l'authentification
        Executor executor = ContextCompat.getMainExecutor(this);

        // Créer une instance de BiometricPrompt
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(AuthentificationBiometric.this, "Authentification réussie", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                        clientSocket.sendMessage("authGood");
                    }
                }).start();

                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(AuthentificationBiometric.this, "Authentification échouée", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(AuthentificationBiometric.this, "Erreur : " + errString, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Configurer les informations affichées dans le prompt
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification biométrique")
                .setSubtitle("Veuillez scanner votre empreinte digitale")
                .setNegativeButtonText("Annuler") // Bouton d'annulation
                .build();

        // Afficher le prompt
        biometricPrompt.authenticate(promptInfo);
    }
}
