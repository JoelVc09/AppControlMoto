package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        val tvoldaste: TextView = findViewById(R.id.tvOlvidaste)

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
                    Toast.makeText(this, "Por favor ingrese los datos", Toast.LENGTH_LONG).show()
                }
                correo.isEmpty() -> {
                    Toast.makeText(this, "Por favor ingrese su correo", Toast.LENGTH_LONG).show()
                }
                clave.isEmpty() -> {
                    Toast.makeText(this, "Por favor ingrese su contraseña", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Si ambos campos están completos, proceder con la autenticación
                    db.signInWithEmailAndPassword(correo, clave).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = db.currentUser
                            val verifica = user?.isEmailVerified
                            if (verifica == true){
                                // Guardar el email y contraseña en SharedPreferences
                                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                                val editor = sharedPreferences.edit()
                                editor.putString("email", correo)
                                editor.putString("password", clave)
                                editor.apply() // Guardar los datos de manera asincrónica

                                Toast.makeText(this, "Inicio Satisfactorio", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, Menu::class.java)
                                startActivity(intent)

                            }
                            else {
                                Toast.makeText(this, "Correo no verificado, revisar bandeja de entrada", Toast.LENGTH_LONG).show()
                            }



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

        //Dirigir a recuperar Password

        tvoldaste.setOnClickListener{
            val intent  = Intent(this, RecuperarPassword::class.java)
            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()

        // Obtener SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")

        // Verificar si ya hay email y contraseña guardados
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            // Si ya hay una sesión activa, saltar el login y redirigir a Menu
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Menu::class.java))
            finish() // Termina la actividad de login
        }
    }

}