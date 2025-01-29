package com.example.app_securitas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class paramEmploye extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_param_employe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        // Récupérer les extras
        String uuid = intent.getStringExtra("employeeUUID");
        String nom = intent.getStringExtra("nom");
        String prenom = intent.getStringExtra("prenom");

        // Utiliser les valeurs récupérées
        // Par exemple, afficher les valeurs dans des TextView

        TextView nomTextView = findViewById(R.id.nomTextView);
        TextView prenomTextView = findViewById(R.id.prenomTextView);

        nomTextView.setText(nom);
        prenomTextView.setText(prenom);

        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);


        // Listener pour checkBox1
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    navigateToAdminInterca();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                            clientSocket.sendMessage("paramEmployerTrue+"+uuid);
                        }
                    }).start();
                }
            }
        });

        // Listener pour checkBox2
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    navigateToAdminInterca();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                            clientSocket.sendMessage("paramEmployerFalse+"+uuid);
                        }
                    }).start();
                }
            }
        });
    }

    private void navigateToAdminInterca() {
        Intent intent = new Intent(paramEmploye.this, adminInterface.class);
        startActivity(intent);
    }
}