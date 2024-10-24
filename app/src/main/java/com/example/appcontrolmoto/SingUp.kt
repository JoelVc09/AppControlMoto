package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        val txtterminos : TextView = findViewById(R.id.tvterminosycondiciones)
        val checx : CheckBox = findViewById(R.id.checkterminos)



        val btnguardarRegistro: Button = findViewById(R.id.btnGuardarRegistro)
        val btnIniciar: Button = findViewById(R.id.btnIniciar)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val collectionRef = db.collection("Usuarios")
        
        // DIrigira terminos y condiciones

        txtterminos.setOnClickListener {
            val intent = Intent(this, TerminosCondiciones::class.java)
            startActivity(intent)
        }

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

            val celularPattern = Regex("^[0-9]{9}\$")

            val namePattern = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$") // Patrón que permite solo letras y espacios

            val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$") // Patrón para validar la contraseña




            // Verificar si algún campo está vacío y mostrar el mensaje adecuado
            when {
                !password.matches(passwordPattern) -> {
                    txtPassword.error = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                !apellido.matches(namePattern) -> {
                    txtApellido.error = "El apellido no puede contener números ni caracteres especiales"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                !nombre.matches(namePattern) -> {
                    txtNombre.error = "El nombre no puede contener números ni caracteres especiales"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                nombre.isEmpty() -> {
                    txtNombre.error = "Por favor ingresa tu nombre"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                !celular.matches(celularPattern) -> {
                    txtCelular.error = "Por favor, ingrese un número de celular válido (9 dígitos)"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                apellido.isEmpty() -> {
                    txtApellido.error = "Por favor ingresa tu apellido"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                email.isEmpty() -> {
                    txtEmail2.error = "Por favor ingresa tu correo"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                celular.isEmpty() -> {
                    txtCelular.error = "Por favor ingresa tu número de celular"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                password.isEmpty() -> {
                    txtPassword.error = "Por favor ingresa una contraseña"
                    return@setOnClickListener // Salir si el formato de celular no es válido

                }
                password.length < 6 -> {
                    txtPassword.error = "La contraseña debe tener al menos 6 caracteres"
                    return@setOnClickListener // Salir si el formato de celular no es válido
                }
                password != passwordConfir -> {
                    txtPassword.error = "Las contraseñas no coinciden"
                    return@setOnClickListener // Salir si el formato de celular no es válido

                }
                !checx.isChecked -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Debes aceptar los términos y condiciones para registrarte",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    txtEmail2.error = "Por favor, ingrese un correo válido"
                    return@setOnClickListener // Salir si el formato de correo no es válido
                }

                else -> {
                    // Si todos los campos son válidos, proceder con la creación de usuario
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = auth.currentUser
                                val uid = user?.uid
                                sendEmailVerification()
                                val userModel = UserModel(nombre, apellido, celular, email, uid)
                                collectionRef.add(userModel)
                                    .addOnCompleteListener {
                                        Toast.makeText(this, "Registro exitoso, Verificar la cuenta.", Toast.LENGTH_LONG).show()
                                        // Limpiar los campos
                                        txtNombre.text.clear()
                                        txtApellido.text.clear()
                                        txtEmail2.text.clear()
                                        txtCelular.text.clear()
                                        txtPassword.text.clear()
                                        txtPasswordConfir.text.clear()
                                        checx.isChecked = false

                                        val intent = Intent(this, Loguin::class.java)
                                        startActivity(intent)

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

    val auth = FirebaseAuth.getInstance()

    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error en enviar correo",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}