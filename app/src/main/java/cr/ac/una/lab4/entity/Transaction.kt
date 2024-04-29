package cr.ac.una.lab4.entity

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.Serializable

data class Transaction(
    var _uuid: String?,
    var monto: Double,
    var tipoTarjeta: String,
    var fecha: String,
    var foto: Bitmap
) : Serializable {
    val fotoBase64: String
        get() {
            val byteArrayOutputStream = ByteArrayOutputStream()
            foto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

    override fun toString(): String {
        return "Monto: $monto, Tipo de Tarjeta: $tipoTarjeta, Fecha: $fecha"
    }
}