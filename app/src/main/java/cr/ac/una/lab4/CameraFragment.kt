package cr.ac.una.lab4

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.Calendar
import android.graphics.drawable.BitmapDrawable
import java.util.UUID
import android.widget.Toast
import android.text.InputFilter
import cr.ac.una.lab4.entity.Transaction

class CameraFragment : Fragment() {
    lateinit var captureButton : Button
    lateinit var imageView : ImageView
    lateinit var datePickerButton : Button
    lateinit var dateEditText: EditText
    lateinit var typeSpinner: Spinner
    lateinit var amountEditText: EditText
    lateinit var insertButton: Button
    lateinit var exitButton: Button

    private val viewModel: TransactionViewModel by activityViewModels()

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
// Permiso denegado, manejar la situación aquí si es necesario
        }
    }
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else {
// Manejar el caso en el que no se haya podido capturar la imagen
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        captureButton = view.findViewById(R.id.foto)
        imageView = view.findViewById(R.id.imageView)
        datePickerButton = view.findViewById(R.id.fecha)
        dateEditText = view.findViewById(R.id.dateEditText)
        typeSpinner = view.findViewById(R.id.TIpoTarjeta)
        amountEditText = view.findViewById(R.id.Monto)
        insertButton = view.findViewById(R.id.insertarlista)
        exitButton = view.findViewById(R.id.Salir)


        // Agregar filtro para limitar a dos decimales
        amountEditText.filters = arrayOf<InputFilter>(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.isEmpty()) {
                return@InputFilter null
            }
            val temp = dest.toString() + source.toString()
            if (temp.matches("^\\d*(\\.\\d{0,2})?$".toRegex())) {
                return@InputFilter source
            }
            ""
        })


        val items = resources.getStringArray(R.array.opciones_spinner)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        typeSpinner.adapter = adapter

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

        datePickerButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateEditText.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)

            dpd.show()
        }

        val transaction = arguments?.getSerializable("transaction") as? Transaction
        if (transaction != null) {
            amountEditText.setText(transaction.monto.toString())
            typeSpinner.setSelection((typeSpinner.adapter as ArrayAdapter<String>).getPosition(transaction.tipoTarjeta))
            dateEditText.setText(transaction.fecha)
            imageView.setImageBitmap(transaction.foto)
        }

        insertButton.setOnClickListener {
            val monto = amountEditText.text.toString()
            val tipoTarjeta = typeSpinner.selectedItem.toString()
            val fecha = dateEditText.text.toString()
            val foto = if (imageView.drawable != null) (imageView.drawable as BitmapDrawable).bitmap else null

            if (monto.isEmpty() || tipoTarjeta.isEmpty() || fecha.isEmpty() || foto == null) {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (transaction != null) {
                    transaction.monto = monto.toDouble()
                    transaction.tipoTarjeta = tipoTarjeta
                    transaction.fecha = fecha
                    transaction.foto = foto
                    viewModel.updateTransaction(transaction)
                    Toast.makeText(context, "Transacción modificada con exito", Toast.LENGTH_SHORT).show()
                } else {
                    val newTransaction = Transaction(UUID.randomUUID().toString(), monto.toDouble(), tipoTarjeta, fecha, foto)
                    viewModel.addTransaction(newTransaction)
                    Toast.makeText(context, "Transacción realizada con exito", Toast.LENGTH_SHORT).show()
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.home_content, HomeFragment())
                    .commit()
            }
        }

        exitButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_content, HomeFragment())
                .commit()
        }


        arguments?.let {
            val transaction = it.getSerializable("transaction") as Transaction
            amountEditText.setText(transaction.monto.toString())
            typeSpinner.setSelection((typeSpinner.adapter as ArrayAdapter<String>).getPosition(transaction.tipoTarjeta))
            dateEditText.setText(transaction.fecha)
            imageView.setImageBitmap(transaction.foto)
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
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

    companion object {
        fun newInstance(transaction: Transaction): CameraFragment {
            val fragment = CameraFragment()
            val args = Bundle()
            args.putSerializable("transaction", transaction)
            fragment.arguments = args
            return fragment
        }
    }
}