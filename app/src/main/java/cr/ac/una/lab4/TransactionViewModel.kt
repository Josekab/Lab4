package cr.ac.una.lab4

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.lab4.entity.Transaction


class TransactionViewModel : ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()

    fun addTransaction(transaction: Transaction) {
        val currentList = transactions.value ?: emptyList()
        transactions.value = currentList + transaction
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val currentList = transactions.value ?: emptyList()
        val index = currentList.indexOfFirst { it._uuid == updatedTransaction._uuid }
        if (index != -1) {
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedTransaction
            transactions.value = updatedList
        }
    }
    fun deleteTransaction(transaction: Transaction) {
        val currentList = transactions.value ?: emptyList()
        val updatedList = currentList.filter { it._uuid != transaction._uuid }
        transactions.value = updatedList
    }

}