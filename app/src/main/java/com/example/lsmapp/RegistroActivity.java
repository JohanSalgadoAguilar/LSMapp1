package com.example.lsmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etEmail = findViewById(R.id.etRegistroEmail);
        EditText etPassword = findViewById(R.id.etRegistroPassword);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!nombre.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            String userId = authResult.getUser() != null ? authResult.getUser().getUid() : "";

                            // Datos que se guardarán en la colección "usuarios"
                            Map<String, Object> usuarioMap = new HashMap<>();
                            usuarioMap.put("nombre", nombre);
                            usuarioMap.put("correo", email);
                            usuarioMap.put("fechaRegistro", Timestamp.now());

                            // Guardar en Firestore usando el UID del usuario como ID del documento
                            db.collection("usuarios").document(userId)
                                    .set(usuarioMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, MinijuegoActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al guardar en base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}