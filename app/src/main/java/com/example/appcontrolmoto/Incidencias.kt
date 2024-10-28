package com.example.appcontrolmoto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Incidencias : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout para este fragmento
        val view: View = inflater.inflate(R.layout.fragment_incidencias, container, false)

        // Referencia al RecyclerView
        val rvComents: RecyclerView = view.findViewById(R.id.rvComents)
        var lstComentarios: List<Coment> // Lista para almacenar los comentarios

        // Instancia de Firestore
        val db = FirebaseFirestore.getInstance()

        // Obtén una instancia de SharedPreferences con el mismo nombre utilizado para guardar el dato
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)

        // Recupera el valor de "placa_guardada"
        val placaMoto = sharedPref.getString("placa_guardada", null) // Segundo parámetro es el valor por defecto

        // Consultar la colección "Calificacion" en Firestore y actualizar la lista de comentarios en tiempo real
        placaMoto?.let { placa ->
            // Consultar la colección "Calificacion" filtrando por la placa
            db.collection("Calificacion")
                .whereEqualTo("placa", placa) // Filtrar solo por documentos que coincidan con la placa guardada
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ERROR-FIREBASE", "Detalle del error: ${error.message}")
                        return@addSnapshotListener
                    }

                    // Si no hay errores, mapear los documentos a instancias de Coment
                    val lstComentarios = snapshot!!.documents.map { document ->
                        Coment(
                            document.getString("usuario").orEmpty(),
                            document.getString("comentario").orEmpty(),
                            document.getString("fecha").orEmpty()
                        )
                    }

                    // Configurar el RecyclerView con el AdapterComent y el LinearLayoutManager
                    rvComents.adapter = AdapterComent(lstComentarios)
                    rvComents.layoutManager = LinearLayoutManager(requireContext())
                }
        }



        // Realizar la consulta a la colección "Conductores" usando la placa guardada
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
                        view.findViewById<TextView>(R.id.txtName).text = nombre
                        view.findViewById<TextView>(R.id.repEsta).text = estado
                        view.findViewById<TextView>(R.id.repLicen).text = licencia
                        view.findViewById<TextView>(R.id.repColor).text = mobilcolor
                        view.findViewById<TextView>(R.id.repSoat).text = soat
                        view.findViewById<TextView>(R.id.repPlaca).text = placa

                        // Cargar la imagen en el ImageView usando Picasso
                        if (!foto.isNullOrEmpty()) {
                            val imageView = view.findViewById<ImageView>(R.id.fotoconduct)
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

        return view
    }
}