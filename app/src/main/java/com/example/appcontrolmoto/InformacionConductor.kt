package com.example.appcontrolmoto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

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

        val placaMoto = intent.getStringExtra("placa_moto")
        val db = FirebaseFirestore.getInstance()

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
}