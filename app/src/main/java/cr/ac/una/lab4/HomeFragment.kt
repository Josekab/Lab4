package cr.ac.una.lab4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.app.AlertDialog
import cr.ac.una.lab4.entity.Transaction

class HomeFragment : Fragment() {
    private val transactions = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter
    private lateinit var listView: ListView

    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        listView = view.findViewById(R.id.listaMovimientos)
        adapter = TransactionAdapter(requireContext(), transactions, viewModel)
        listView.adapter = adapter

        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        botonNuevo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_content, CameraFragment())
                .commit()
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedTransaction = transactions[position]
            AlertDialog.Builder(context)
                .setTitle("Confirmar edición")
                .setMessage("¿Estás seguro de que quieres editar esta transacción?")
                .setPositiveButton("Sí") { dialog, which ->
                    val cameraFragment = CameraFragment.newInstance(selectedTransaction)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_content, cameraFragment)
                        .commit()
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { newTransactions ->
            transactions.clear()
            transactions.addAll(newTransactions)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val transaction = data?.getSerializableExtra("transaction") as Transaction
            transactions.add(transaction)
            adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
        }
    }


    companion object {
        private const val ARG_TRANSACTION = "transaction"

        @JvmStatic
        fun newInstance(transaction: Transaction) = CameraFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TRANSACTION, transaction)
            }
        }
    }
}