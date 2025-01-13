package com.example.appcontrolmoto

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlertaPeligro.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlertaPeligro : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view: View = inflater.inflate(R.layout.fragment_alerta_peligro, container, false)

        // Referencia al botón
        val btnLLamada: Button = view.findViewById(R.id.btnLLamada)

        // Configura el OnClickListener para el botón
        btnLLamada.setOnClickListener {
            // Definir los números
            val numeroOriginal = "978478836"
            val numeroReemplazo = "917969460"

            // Crear el diálogo
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Seleccionar número para llamar")
            builder.setMessage("¿A qué número deseas realizar la llamada?")

            // Opción para el número original
            builder.setPositiveButton("Llamar al Número principal") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numeroOriginal")
                }
                startActivity(intent)
            }

            // Opción para el número de reemplazo
            builder.setNegativeButton("Llamar al Número alternativo") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numeroReemplazo")
                }
                startActivity(intent)
            }

            // Mostrar el diálogo
            builder.show()
        }


        val btnEnviarMensaje = view.findViewById<Button>(R.id.btnEnviarMensaje)
        btnEnviarMensaje.setOnClickListener {
            enviarMensajeWhatsApp()
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
         * @return A new instance of fragment AlertaPeligro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlertaPeligro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun enviarMensajeWhatsApp() {
        // Recuperar el mensaje del TextView
        val mensaje = view?.findViewById<TextView>(R.id.tvmensaje)?.text.toString()

        // Verificar si el mensaje no está vacío
        if (mensaje.isNotEmpty()) {
            // Recuperar el nombre de usuario de SharedPreferences
            val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val usuario = sharedPref.getString("username", "Usuario Desconocido") // Valor por defecto

            // Crear URL para enviar el mensaje a WhatsApp
            val numeroTelefono = "+51917969460" // Reemplaza con tu número
            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${Uri.encode("$mensaje\n\nEnviado por: $usuario")}"

            // Iniciar WhatsApp
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } else {
            // Manejar el caso donde el mensaje está vacío
            Toast.makeText(requireContext(), "Por favor, ingresa un mensaje antes de enviar.", Toast.LENGTH_SHORT).show()
        }
    }
}