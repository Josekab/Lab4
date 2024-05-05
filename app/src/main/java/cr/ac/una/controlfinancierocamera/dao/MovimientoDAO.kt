package cr.ac.una.jsoncrud.dao

import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import retrofit2.http.*

interface MovimientoDAO {

        @GET("movimiento")
        suspend fun getItems(): Movimientos

        @GET("movimiento/{uuid}")
        suspend fun getItem(@Path("uuid") uuid: String): Movimiento

        @POST("movimiento")
        suspend fun createItem( @Body items: List<Movimiento>): Movimientos

        @PUT("movimiento/{uuid}")
        suspend fun updateItem(@Path("uuid") uuid: String, @Body item: Movimiento): Movimiento

        @DELETE("movimiento/{uuid}")
        suspend fun deleteItem(@Path("uuid") uuid: String)
}
