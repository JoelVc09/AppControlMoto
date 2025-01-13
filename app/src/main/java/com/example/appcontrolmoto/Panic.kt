package com.example.appcontrolmoto

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Panic.newInstance] factory method to
 * create an instance of this fragment.
 */
class Panic : Fragment() {
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
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_panic2, container, false)

        // Definir los números
        val numeroPrincipal = "978478836"  // Número principal
        val numeroAlternativo = "917969460" // Número alternativo

        // Mostrar el cuadro de diálogo al ingresar al fragmento
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Seleccionar número para llamar")
        builder.setMessage("¿A qué número deseas realizar la llamada?")

        // Opción para el número principal
        builder.setPositiveButton("Llamar al número principal") { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$numeroPrincipal")
            }
            startActivity(intent)

            cerrarAplicacionConRetraso()
        }

        // Opción para el número alternativo
        builder.setNegativeButton("Llamar al número alternativo") { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$numeroAlternativo")
            }
            startActivity(intent)

            cerrarAplicacionConRetraso()
        }

        // Mostrar el cuadro de diálogo
        builder.setCancelable(false) // Para forzar la selección de un número
        builder.show()

        return view
    }

    // Método auxiliar para cerrar la aplicación después de un retraso
    private fun cerrarAplicacionConRetraso() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            requireActivity().finishAffinity() // Cierra todas las actividades de la aplicación
        }, 1000) // Retraso de 1 segundo
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Panic.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Panic().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}