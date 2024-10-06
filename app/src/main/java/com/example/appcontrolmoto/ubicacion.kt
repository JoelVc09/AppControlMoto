package com.example.appcontrolmoto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ubicacion : Fragment() {

    private lateinit var binding: FragmentPanicBinding
    private lateinit var fuseLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Manejador para la solicitud de permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            onPermisosConcedidos()
        } else {
            Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_SHORT).show()
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
        val permisos = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisos.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val permisosArray = permisos.toTypedArray()

        if (tienePermisos(permisosArray)) {
            onPermisosConcedidos()
        } else {
            solicitarPermisos(permisosArray)
        }
    }

    private fun tienePermisos(permisos: Array<String>): Boolean {
        return permisos.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
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

    private fun solicitarPermisos(permisos: Array<String>) {
        requestPermissionLauncher.launch(permisos)
    }

    private fun imprimirUbicacion(ubicacion: Location) {
        binding.tvLatitud.text = "${ubicacion.latitude}"
        binding.tvLongitud.text = "${ubicacion.longitude}"
        Log.d("GPS", "LAT: ${ubicacion.latitude} - LON: ${ubicacion.longitude}")
    }





    private fun enviarUbicacionWhatsApp() {

        //OBTENER EL NOMBRE DE HOME
        val sharedPref = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "Usuario Desconocido")


        val numeroTelefono = "+51969456783"  // Reemplaza con tu número
        val latitud = binding.tvLatitud.text.toString()
        val longitud = binding.tvLongitud.text.toString()
        val mensaje = "Hola mi nombre es $username y siento que estoy en peligro mi ubicación es: Latitud: $latitud, Longitud: $longitud"
        val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${Uri.encode(mensaje)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
