package cr.ac.una.controlfinancierocamera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.text.InputFilter
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.util.*
import cr.ac.menufragment.ListControlFinancieroFragment

class EditControlFinancieroFragment : Fragment() {

    lateinit var captureButton: Button
    lateinit var imageView: ImageView
    lateinit var elementoSeleccionado: String
    lateinit var fechaButton: Button
    lateinit var fechaText: TextView
    lateinit var foto: Bitmap

    private val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val selectedDate = String.format("%d/%d/%d", day, month + 1, year)
        fechaText.text = selectedDate
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
            foto = imageBitmap!!.copy(imageBitmap.config, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val botonNuevo = view.findViewById<Button>(R.id.saveMovimientoButton)
        val monto = view.findViewById<TextView>(R.id.textMonto)
        fechaText = view.findViewById(R.id.textFecha)
        fechaButton = view.findViewById(R.id.fecha)


        monto.filters = arrayOf<InputFilter>(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.isEmpty()) {
                return@InputFilter null
            }
            val enteringText = source.toString()
            val resultingText = dest.toString().substring(0, dstart) + enteringText + dest.toString().substring(dend)
            if (resultingText.contains(".")) {
                val decimalParts = resultingText.split(".")
                if (decimalParts.size >= 2 && decimalParts[1].length > 2) {
                    return@InputFilter ""
                }
            }
            null
        })
        botonNuevo.setOnClickListener {
            val movimiento = Movimiento(null,
                monto.text.toString().toDouble(),
                elementoSeleccionado, fechaText.text.toString(),
                foto
                )
            val actividad = activity as MainActivity
            GlobalScope.launch(Dispatchers.IO) {
                actividad.movimientoController.insertMovimiento(movimiento)
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }

        val spinner: Spinner = view.findViewById(R.id.tipoMovimientoSpinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.tiposMovimiento,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val elementos = resources.getStringArray(R.array.tiposMovimiento)
                elementoSeleccionado = elementos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        captureButton = view.findViewById(R.id.captureButton)
        imageView = view.findViewById(R.id.imageView)

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

        fechaButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        val salirButton = view.findViewById<Button>(R.id.Salir)
        salirButton.setOnClickListener {
            val fragment = ListControlFinancieroFragment()
            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.home_content, fragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_control_financiero, container, false)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }
}