package cr.ac.una.lab4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cr.ac.una.lab4.entity.Transaction

class TransactionAdapter(context: Context, private val transactions: MutableList<Transaction>, private val viewModel: TransactionViewModel) : ArrayAdapter<Transaction>(context, 0, transactions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val transaction = transactions[position]

        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = transaction.toString()

        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar esta transacción?")
                .setPositiveButton("Sí") { dialog, which ->
                    viewModel.deleteTransaction(transaction)
                    transactions.remove(transaction)
                    notifyDataSetChanged()
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }
}