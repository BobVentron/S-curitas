package com.example.app_securitas;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import java.util.concurrent.Executor;

public class paramSerrure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_param_serrure);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        CheckBox checkBox3 = findViewById(R.id.checkBox3);

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
                            clientSocket.sendMessage("paramSerrure1");
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
                            clientSocket.sendMessage("paramSerrure2");
                        }
                    }).start();
                }
            }
        });

        // Listener pour checkBox3
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    navigateToAdminInterca();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ClientSocket clientSocket = SocketManager.getInstance().getClientSocket();
                            clientSocket.sendMessage("paramSerrure3");
                        }
                    }).start();
                }
            }
        });
    }

    private void navigateToAdminInterca() {
        Intent intent = new Intent(paramSerrure.this, adminInterface.class);
        startActivity(intent);
    }

}