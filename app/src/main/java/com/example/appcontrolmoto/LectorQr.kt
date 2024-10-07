package com.example.appcontrolmoto

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebSettings.LayoutAlgorithm
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcontrolmoto.databinding.ActivityLectorQrBinding
import com.google.api.Billing
import com.google.api.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class LectorQr : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera() // Si se concede el permiso, muestra la cámara
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show() // Manejo si el permiso no es concedido
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            } else {
                setResult(result.contents)
            }
        }

    private lateinit var binding: ActivityLectorQrBinding

    private fun setResult(string: String) {
        binding.textResult.text = string
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escanea codigo QR")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initBinding()
        initViews()

        // Manejo de permisos de cámara al iniciar la actividad
        checkPermissionCamera(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        binding.fab.setOnClickListener {
            checkPermissionCamera(this) // Opción para volver a verificar el permiso al presionar el FAB
        }

        // Inicia la InformacionConductorActivity al hacer clic en btnPerfil
        binding.btnperfil.setOnClickListener {
            val placa = binding.textResult.text.toString() // Obtén el valor de la placa del TextView

            if (placa.isNotEmpty()) {
                // Iniciar la Activity InformacionConductor con el valor de la placa
                val intent = Intent(this, InformacionConductor::class.java)
                intent.putExtra("placa_moto", placa) // Pasar el valor de la placa
                startActivity(intent) // Iniciar la otra Activity
            } else {
                Toast.makeText(this, "No hay placa escaneada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionCamera(context: android.content.Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera() // Si el permiso ya ha sido concedido, muestra la cámara
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA) // Solicita el permiso
        }
    }

    private fun initBinding() {
        binding = ActivityLectorQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
