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

        LinearLayout employeeListContainer = findViewById(R.id.employeeListContainer);
        String[] employeeNames = {
                "Jean Dupont", "Marie Curie", "Albert Einstein",
                "Isaac Newton", "Galileo Galilei", "Nikola Tesla",
                "Thomas Edison", "Leonardo da Vinci", "Stephen Hawking",
                "Ada Lovelace", "Grace Hopper", "Alan Turing",
                "Charles Babbage", "Johannes Kepler", "Michael Faraday",
                "James Clerk Maxwell", "Richard Feynman", "Marie Curie",
                "Rosalind Franklin", "Hedy Lamarr", "Katherine Johnson"
        };


        for (String employeeName : employeeNames) {
            Button button = new Button(this);
            button.setText(employeeName);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            button.setOnClickListener(v -> {
                Intent intent = new Intent(adminInterface.this, paramEmploye.class);
                intent.putExtra("employeeName", employeeName);
                startActivity(intent);
            });
            employeeListContainer.addView(button);
        }
        
    }

    private void navigateToparamSerrure() {
        Intent intent = new Intent(adminInterface.this, paramSerrure.class);
        startActivity(intent);
    }

    private void navigateTonewEmploye() {
        Intent intent = new Intent(adminInterface.this, newEmploye.class);
        startActivity(intent);
    }
}