package cr.ac.una.controlfinancierocamera.entity
import android.graphics.Bitmap
import java.io.Serializable
import java.util.Base64
import java.util.Date
data class Movimiento(var _uuid :String?,
                      var monto : Double,
                      var tipo: String,
                      var fecha :String,
                      var foto :Bitmap?) : Serializable


