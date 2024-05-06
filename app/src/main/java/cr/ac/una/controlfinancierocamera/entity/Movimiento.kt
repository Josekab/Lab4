package cr.ac.una.controlfinancierocamera.entity
import java.io.Serializable
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

data class Movimiento(
    var _uuid :String?,
    var monto : Double,
    var tipo: String,
    var fecha :String,
    var foto: Bitmap
) : Serializable {
    val fotoBase64: String
        get() {
            val byteArrayOutputStream = ByteArrayOutputStream()
            foto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
}

