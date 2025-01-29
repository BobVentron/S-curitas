package com.example.app_securitas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Button;


public class newEmploye extends AppCompatActivity {

    private EditText uuidEditText, nameEditText, firstNameEditText, passwordEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_employe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uuidEditText = findViewById(R.id.uuid);
        nameEditText = findViewById(R.id.name);
        firstNameEditText = findViewById(R.id.firstName);
        passwordEditText = findViewById(R.id.password);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfo();
            }
        });
    }
    private void sendInfo() {
        String uuid = uuidEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String info = "newEmployerUUID:" + uuid + ",Nom:" + name + ",Prénom:" + firstName + ",MDP:" + password;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                clientSocket.sendMessage(info); // Envoie les informations
            }
        }).start();
        navigateToAdminInterca();
    }
    private void navigateToAdminInterca() {
        Intent intent = new Intent(newEmploye.this, adminInterface.class);
        startActivity(intent);
    }
}