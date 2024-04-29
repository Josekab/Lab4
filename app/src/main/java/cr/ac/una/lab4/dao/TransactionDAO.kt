package cr.ac.una.lab4.dao;

import cr.ac.una.lab4.entity.Transaction;
import cr.ac.una.lab4.entity.Transactions;
import retrofit2.http.*;
/*import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path*/

interface TransactionDAO {

    @GET("Transactions")
    suspend fun getItems(): Transactions //List<Item>

    @GET("Transactions/{uuid}")
    suspend fun getItem(@Path("uuid") uuid: String): Transaction

    @POST("Transactions")
    suspend fun createItem( @Body transactions: List<Transaction>): Transactions

    @PUT("Transactions/{uuid}")
    suspend fun updateItem(
        @Path("uuid") uuid: String,
        @Body item: Transaction): Transaction

    @DELETE("Transactions/{uuid}")
    suspend fun deleteItem(@Path("uuid") uuid: String)
}