package com.example.appcontrolmoto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.appcontrolmoto.databinding.FragmentPanicBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.location.Location
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ubicacion : Fragment() {

    private lateinit var binding: FragmentPanicBinding
    private lateinit var fuseLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    // Manejador para la solicitud de permisos (solo ACCESS_FINE_LOCATION)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermisosConcedidos()
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPanicBinding.inflate(inflater, container, false)
        verificarPermisos()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.Enviarubicacionactual.setOnClickListener {
            enviarUbicacionWhatsApp()
        }
    }

    private fun verificarPermisos() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Ya tienes el permiso, puedes acceder a la ubicación
            onPermisosConcedidos()
        } else {
            // Solicitar el permiso
            solicitarPermisos()
        }
    }

    private fun onPermisosConcedidos() {
        fuseLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            fuseLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    imprimirUbicacion(it)
                } else {
                    Toast.makeText(requireContext(), "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        imprimirUbicacion(location)
                    }
                }
            }

            fuseLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (_: SecurityException) {
            // Manejo de la excepción
        }
    }

    private fun solicitarPermisos() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun imprimirUbicacion(ubicacion: Location) {
        binding.tvLatitud.text = "${ubicacion.latitude}"
        binding.tvLongitud.text = "${ubicacion.longitude}"
        Log.d("GPS", "LAT: ${ubicacion.latitude} - LON: ${ubicacion.longitude}")
    }

    private fun enviarUbicacionWhatsApp() {
        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la ubicación actual
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitud = location.latitude
                    val longitud = location.longitude

                    // Recuperar el nombre de usuario de SharedPreferences
                    val sharedPref = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val usuario = sharedPref?.getString("username", "Usuario Desconocido") // Valor por defecto

                    // Crear URL para enviar la ubicación a WhatsApp
                    val numeroTelefono = "+51969456783"  // Reemplaza con tu número
                    val mensaje = "Hola mi nombre es $usuario Estoy en peligro, mi ubicación actual es: https://www.google.com/maps?q=$latitud,$longitud"
                    val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${Uri.encode(mensaje)}"

                    // Iniciar WhatsApp
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    // Manejar el caso cuando no se puede obtener la ubicación
                    //Snackbar.make(this, "No se pudo obtener la ubicación actual.", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ubicacion().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
