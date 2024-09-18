package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcontrolmoto.UserModel.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SingUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre: EditText = findViewById(R.id.txtNombre)
        val txtApellido: EditText = findViewById(R.id.txtApellido)
        val txtEmail2: EditText = findViewById(R.id.txtEmail2)
        val txtCelular: EditText = findViewById(R.id.txtCelular)
        val txtPassword: EditText = findViewById(R.id.txtpassword2)
        val txtPasswordConfir: EditText = findViewById(R.id.txtPasswordConfir)

        val btnguardarRegistro: Button = findViewById(R.id.btnGuardarRegistro)
        val btnIniciar: Button = findViewById(R.id.btnIniciar)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val collectionRef = db.collection("Usuarios")

        //IR INICIAR SESION

        btnIniciar.setOnClickListener {
            val intent = Intent(this, Loguin::class.java)
            startActivity(intent)
        }

        // REGISTRARSE

        btnguardarRegistro.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val apellido = txtApellido.text.toString().trim()
            val email = txtEmail2.text.toString().trim()
            val celular = txtCelular.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val passwordConfir = txtPasswordConfir.text.toString().trim()


            // Verificar si algún campo está vacío y mostrar el mensaje adecuado
            when {
                nombre.isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor ingresa tu nombre",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                apellido.isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor ingresa tu apellido",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                email.isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor ingresa tu correo",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                celular.isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor ingresa tu número de celular",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                password.isEmpty() -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Por favor ingresa una contraseña",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                password.length < 6 -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "La contraseña debe tener al menos 6 caracteres",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                password.length !== passwordConfir.length -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Las contraseñas no coinciden",
                        Snackbar.LENGTH_LONG
                    ).show()

                }

                else -> {
                    // Si todos los campos son válidos, proceder con la creación de usuario
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = auth.currentUser
                                val uid = user?.uid

                                val userModel = UserModel(nombre, apellido, celular, email, uid)
                                collectionRef.add(userModel)
                                    .addOnCompleteListener {
                                        Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "Registro exitoso",
                                            Snackbar.LENGTH_LONG
                                        ).show()

                                        // Limpiar los campos
                                        txtNombre.text.clear()
                                        txtApellido.text.clear()
                                        txtEmail2.text.clear()
                                        txtCelular.text.clear()
                                        txtPassword.text.clear()


                                    }.addOnFailureListener { error ->
                                        Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "Ocurrió un error al registrar el usuario: ${error.message}",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Snackbar.make(
                                    findViewById(android.R.id.content),
                                    "Ocurrió un error al registrar el usuario: ${task.exception.toString()}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }.addOnFailureListener(this) { error ->
                            Log.e("ErrorFirebase", error.message.toString())
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                "Ocurrió un error inesperado: ${error.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }
    }
}