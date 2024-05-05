package cr.ac.una.controlfinancierocamera.controller

import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import cr.ac.una.controlfinancierocamera.service.MovimientoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovimientoController {
    var movimientoService= MovimientoService()



    suspend fun insertMovimiento(movimiento: Movimiento){

            var movimientos: ArrayList<Movimiento> = arrayListOf()
            movimientos.add(movimiento)
            movimientoService.apiService.createItem(movimientos)

    }
    suspend fun  deleteMovimiento(movimiento: Movimiento){
            movimiento._uuid?.let { movimientoService.apiService.deleteItem(it) }
    }
    suspend fun  listMovimientos():ArrayList<Movimiento>{
            return movimientoService.apiService.getItems().items as ArrayList<Movimiento>
    }


}