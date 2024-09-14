package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class Loguin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loguin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtEmail: EditText = findViewById(R.id.txtEmail)
        val txtPassword: EditText = findViewById(R.id.txtPassword)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegistro: Button = findViewById(R.id.btnRegistro)

        val db = FirebaseAuth.getInstance()

        // INICIAR SESION
        btnLogin.setOnClickListener {
            var correo: String = txtEmail.text.toString()
            var clave: String = txtPassword.text.toString()

            // Verificar si los campos están vacíos y mostrar el mensaje adecuado
            when {
                correo.isEmpty() && clave.isEmpty() -> {
                    Toast.makeText(this, "Por favor ingresa los datos", Toast.LENGTH_LONG).show()
                }
                correo.isEmpty() -> {
                    Toast.makeText(this, "Por favor ingresa tu correo", Toast.LENGTH_LONG).show()
                }
                clave.isEmpty() -> {
                    Toast.makeText(this, "Por favor ingresa tu contraseña", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Si ambos campos están completos, proceder con la autenticación
                    db.signInWithEmailAndPassword(correo, clave).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Inicio Satisfactorio", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Email o Password incorrecto", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        // REGISTRO DE USUARIO

        btnRegistro.setOnClickListener {
            val intent = Intent(this, SingUp::class.java)
            startActivity(intent)
        }


    }
}