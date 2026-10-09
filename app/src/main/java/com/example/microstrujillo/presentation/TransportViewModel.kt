package com.example.microstrujillo.presentation

import androidx.lifecycle.ViewModel
import com.example.microstrujillo.model.Ruta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TransportViewModel : ViewModel() {

    // TODO (ensamble): reemplazar por repository.obtenerRutasGuardadas().stateIn(...)
    private val _rutas = MutableStateFlow(
        listOf(
            Ruta(1, "Línea 10 - Nuevo California", "Centro", "Huanchaco", 12),
            Ruta(2, "Línea 3 - El Porvenir", "Plaza de Armas", "El Porvenir", 5),
            Ruta(3, "Línea 14 - La Esperanza", "Mercado Mayorista", "La Esperanza", 8),
            Ruta(4, "Línea 7 - Alto Trujillo", "Centro", "Alto Trujillo", 15),
            Ruta(5, "Línea 21 - Buenos Aires", "Terminal", "Buenos Aires", 3)
        )
    )
    val rutas: StateFlow<List<Ruta>> = _rutas.asStateFlow()

    // TODO (ensamble): leer/guardar con UserPreferences (DataStore) vía repositorio
    private val _modoAccesible = MutableStateFlow(false)
    val modoAccesible: StateFlow<Boolean> = _modoAccesible.asStateFlow()

    private val _saldo = MutableStateFlow(10.0)
    val saldo: StateFlow<Double> = _saldo.asStateFlow()

    fun setModoAccesible(activo: Boolean) {
        _modoAccesible.value = activo
    }

    fun recargar(monto: Double) {
        _saldo.update { it + monto }
    }
}
