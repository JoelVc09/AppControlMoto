package com.example.appcontrolmoto

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Traer Nombre de usuario
    private lateinit var tvUsuario: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Configurar el botón para navegar a LectorQr
        val btnScanner: Button = view.findViewById(R.id.btnScaner)

        // Agregar el log para verificar si el botón es null
        Log.d("HomeFragment", "Button is null: ${btnScanner == null}")

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicializar el TextView
        tvUsuario = view.findViewById(R.id.tvUsuario)

        // Recuperar el email y contraseña de SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val emailGuardado = sharedPreferences.getString("email", null)

        // Encuentra el botón por su ID

        val btnAlert: Button = view.findViewById(R.id.btnAlert)

        btnAlert.setOnClickListener {
            val intent = Intent(requireActivity(), InformacionConductor::class.java)
            startActivity(intent)
        }

        // ASIGNAR EL NOMBRE DEL USUARIO
        emailGuardado?.let { email -> // Verificar que el correo no sea null
            // Hacer la consulta en Firestore usando el correo guardado
            firestore.collection("Usuarios")
                .whereEqualTo("email", email) // Utiliza el correo guardado en SharedPreferences
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val nombre = document.getString("nombre") // Asumiendo que el campo es "nombre"
                        tvUsuario.text = nombre

                        // Guardar nombre de usuario en SharedPreferences para utilizar en el mensaje
                        val sharedPref = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref?.edit()
                        editor?.putString("username", tvUsuario.text.toString())
                        editor?.apply()
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejo del error
                    Log.e("FirestoreError", "Error al obtener el documento", exception)
                }
        }


        // Direccionando a Scaner
        btnScanner.setOnClickListener {
            val intent = Intent(requireActivity(), LectorQr::class.java)
            startActivity(intent)
        }

        val btnLogout: Button = view.findViewById(R.id.btnLogout)

// Acción para el botón de cerrar sesión
        btnLogout.setOnClickListener {
            // Crear un diálogo de confirmación
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Cierre de Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    // Código para cerrar sesión
                    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPreferences.edit()
                    editor.remove("email")    // Eliminar el correo
                    editor.remove("password") // Eliminar la contraseña
                    editor.apply() // Aplica los cambios

                    // Redirigir a la actividad de Login
                    val intent = Intent(requireActivity(), Loguin::class.java)
                    startActivity(intent)

                    // Finalizar la actividad actual si no quieres volver a ella
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Cerrar el diálogo si se elige "No"
                }
                .show() // Mostrar el diálogo
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}