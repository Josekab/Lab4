package cr.ac.una.controlfinanciero.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import cr.ac.menufragment.CameraFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.AlertDialog
import android.os.Bundle

class MovimientoAdapter (context:Context, movimientos:List<Movimiento>):
    ArrayAdapter<Movimiento>(context,0,movimientos){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)
        val monto = view.findViewById<TextView>(R.id.monto)
        val tipo = view.findViewById<TextView>(R.id.tipo)
        val fecha = view.findViewById<TextView>(R.id.fecha)

        var movimiento = getItem(position)
        monto.text = movimiento?.monto.toString()
        tipo.text = movimiento?.tipo.toString()
        fecha.text = movimiento?.fecha.toString()

        var bottonDelete = view.findViewById<ImageButton>(R.id.button_delete)
        bottonDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmación de borrado")
                .setMessage("¿Estás seguro de que deseas borrar los datos?")
                .setPositiveButton("Sí") { dialog, which ->
                    val mainActivity = context as MainActivity
                    GlobalScope.launch(Dispatchers.Main) {
                        movimiento?.let { it1 -> mainActivity.movimientoController.deleteMovimiento(it1) }
                        clear()
                        addAll(mainActivity.movimientoController.listMovimientos())
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
        var buttonUpdate = view.findViewById<ImageButton>(R.id.button_update)
        buttonUpdate.setOnClickListener {
            val movimiento = getItem(position) // Asegúrate de que este método obtiene el objeto Movimiento correcto

            AlertDialog.Builder(context)
                .setTitle("Confirmación de edición")
                .setMessage("¿Estás seguro de que deseas editar los datos?")
                .setPositiveButton("Sí") { dialog, which ->
                    val fragment = EditControlFinancieroFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable("movimiento", movimiento)
                        }
                    }
                    val fragmentManager = (context as MainActivity).supportFragmentManager
                    fragmentManager.beginTransaction().apply {
                        replace(R.id.home_content, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }



        return view
    }
}