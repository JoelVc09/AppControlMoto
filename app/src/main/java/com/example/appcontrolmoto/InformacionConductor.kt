package com.example.appcontrolmoto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InformacionConductor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_informacion_conductor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Guardar comentarios y calificacion

        // Obtenemos referencias de Firebase y SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val db = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etComentario = findViewById<EditText>(R.id.et_comentario)
        val repPlaca = findViewById<TextView>(R.id.repPlaca)
        val btnGuardarComentario = findViewById<Button>(R.id.btnGuardarComentario)

        btnGuardarComentario.setOnClickListener {
            val comentario = etComentario.text.toString().trim()
            val placa = repPlaca.text.toString()
            val puntuacion = ratingBar.rating

            // Validaciones
            if (puntuacion == 0f) {
                Toast.makeText(this, "Por favor, marca una puntuación.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (comentario.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener la fecha actual en el formato "dd/MM/yyyy"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            // Crear el objeto de datos
            val calificacionData = hashMapOf(
                "comentario" to comentario,
                "placa" to placa,
                "puntuacion" to puntuacion,
                "usuario" to username,
                "fecha" to currentDate // Agregar la fecha actual en formato deseado
            )

            // Guardar en Firebase Firestore
            db.collection("Calificacion")
                .add(calificacionData)
                .addOnSuccessListener {
                    mostrarDialogo("Éxito", "Calificación guardada exitosamente")
                }
                .addOnFailureListener { e ->
                    mostrarDialogo("Error", "Error al guardar: ${e.message}")
                }
        }




        // Traer datos del conductor
        //val placaMoto = intent.getStringExtra("placa_moto")

        // Obtén una instancia de SharedPreferences con el mismo nombre utilizado para guardar el dato
        val sharedPref = getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)

        // Recupera el valor de "placa_guardada"
        val placaMoto = sharedPref.getString("placa_guardada", null) // Segundo parámetro es el valor por defecto



        db.collection("Conductores")
            .whereEqualTo("placa", placaMoto) // Filtrar por el campo placa
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Si encontramos un documento, obtén los datos
                    for (document in documents) {
                        val nombre = document.getString("nombre")
                        val estado = document.getString("estado")
                        val licencia = document.getString("licencia")
                        val mobilcolor = document.getString("mobilcolor")
                        val soat = document.getString("soat")
                        val placa = document.getString("placa")
                        val foto = document.getString("foto") // Campo de referencia de la imagen

                        // Asignar los valores a los TextView
                        findViewById<TextView>(R.id.txtName).text = nombre
                        findViewById<TextView>(R.id.repEsta).text = estado
                        findViewById<TextView>(R.id.repLicen).text = licencia
                        findViewById<TextView>(R.id.repColor).text = mobilcolor
                        findViewById<TextView>(R.id.repSoat).text = soat
                        findViewById<TextView>(R.id.repPlaca).text = placa

                        // Cargar la imagen en el ImageView usando Picasso
                        if (foto != null && foto.isNotEmpty()) {
                            val imageView = findViewById<ImageView>(R.id.fotoconduct)
                            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foto)

                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                // Usar Picasso para cargar la imagen
                                Picasso.get()
                                    .load(uri)
                                    .into(imageView)
                            }.addOnFailureListener { exception ->
                                Log.e("Firebase", "Error al cargar la imagen: ${exception.message}")
                            }
                        }
                    }
                } else {
                    // Manejar el caso donde no se encuentra ningún documento
                    Log.d("Firebase", "No hay documentos que coincidan con la placa")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error al obtener documentos: ", exception)
            }

        val btnvolver: Button = findViewById(R.id.btnregresarcoduct)

        btnvolver.setOnClickListener {
            // Crear un Intent para volver a la actividad LectorQr
            val intent = Intent(this, Menu::class.java)
            startActivity(intent) // Iniciar la actividad LectorQr
            finish() // Opcional: cerrar la actividad actual
        }



    }

    // Función para mostrar un diálogo de alerta
// Función para mostrar un diálogo de alerta
    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Cerrar el diálogo

                // Redirigir al menú
                val intent = Intent(this, Menu::class.java)
                startActivity(intent) // Iniciar la actividad del menú
                finish() // Cerrar la actividad actual (opcional)
            }
            .show()
    }
}