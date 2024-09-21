package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RecuperarPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnrecuperar: Button = findViewById(R.id.recuperarBoton)
        val valcorreo: EditText = findViewById(R.id.recuperarcorreo)
        val reingresar: TextView = findViewById(R.id.entrarNuevamente)


        val auth = FirebaseAuth.getInstance()

        //Regresar a loguin

        reingresar.setOnClickListener{
            val intent = Intent(this, Loguin::class.java)
            startActivity(intent)
        }

        btnrecuperar.setOnClickListener {
            val sEmail = valcorreo.text.toString().trim() // Elimina espacios en blanco adicionales

            // Validación de campos vacíos
            if (sEmail.isEmpty()) {
                valcorreo.error = "El campo de correo no puede estar vacío"
                return@setOnClickListener // Salir de la función si no se cumple esta condición
            }

            // Validación del formato de correo
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                valcorreo.error = "Por favor, ingrese un correo válido"
                return@setOnClickListener // Salir si el formato de correo no es válido
            }

            // Si pasa todas las validaciones, se envía el correo de recuperación
            auth.sendPasswordResetEmail(sEmail)
                .addOnSuccessListener {
                    Toast.makeText(this, "Por favor revise su correo", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
}