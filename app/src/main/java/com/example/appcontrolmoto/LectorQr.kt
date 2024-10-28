package com.example.appcontrolmoto

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebSettings.LayoutAlgorithm
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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

        binding.btnperfil.setOnClickListener {
            val placa = binding.textResult.text.toString()
            if (placa.isNotEmpty()) {
                verificarPlaca(placa) // Llama a la función para verificar la placa
            } else {
                Toast.makeText(this, "No hay placa escaneada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        // Configura el click listener para el FAB
        binding.fab.setOnClickListener {
            checkPermissionCamera(this) // Verifica los permisos de la cámara al presionar el FAB
        }

        // Configura el click listener para el botón btnperfil
        binding.btnperfil.setOnClickListener {
            val placa = binding.textResult.text.toString() // Obtén el valor de la placa del TextView

            if (placa.isNotEmpty()) {
                verificarPlaca(placa) // Llama a la función para verificar si la placa está registrada en Firestore
            } else {
                Toast.makeText(this, "No hay placa escaneada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarPlaca(placa: String) {
        val db = FirebaseFirestore.getInstance()

        // Consulta en la colección "Conductores" para ver si existe un documento con el campo "placa" igual a la placa escaneada
        db.collection("Conductores")
            .whereEqualTo("placa", placa)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Si el documento existe (hay al menos un documento con la placa escaneada)

                    // Guardar la placa en SharedPreferences
                    val sharedPref = getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("placa_guardada", placa) // Guardar la placa con la clave "placa_guardada"
                    editor.apply() // Aplicar los cambios

                    // Iniciar la actividad InformacionConductor
                    val intent = Intent(this, InformacionConductor::class.java)
                    intent.putExtra("placa_moto", placa) // Pasar el valor de la placa
                    startActivity(intent) // Iniciar la otra Activity

                } else {
                    // Obtén una instancia de SharedPreferences
                    val sharedPref = getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)

                    // Abre el editor para realizar cambios
                    val editor = sharedPref.edit()

                    // Elimina el valor almacenado con la clave "placa_guardada"
                    editor.remove("placa_guardada")

                    // Aplica los cambios
                    editor.apply()
                    // Si no existe, muestra un diálogo
                    mostrarDialogPlacaNoRegistrada()
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores en caso de que la consulta falle
                Toast.makeText(this, "Error al verificar la placa: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun mostrarDialogPlacaNoRegistrada() {
        AlertDialog.Builder(this)
            .setTitle("Placa no registrada")
            .setMessage("Esta placa no está registrada en la base de datos.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Cerrar el diálogo

                // Redirigir al menú
                val intent = Intent(this, Menu::class.java)
                startActivity(intent) // Iniciar la actividad del menú
                finish() // Cerrar la actividad actual (opcional)
            }
            .show()
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
