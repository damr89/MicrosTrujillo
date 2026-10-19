package com.example.microstrujillo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microstrujillo.domain.RutaCalculada
import com.example.microstrujillo.domain.RoutingEngine
import com.example.microstrujillo.domain.LocationHelper
import com.example.microstrujillo.data.RouteRepository
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Ruta
import org.osmdroid.util.GeoPoint
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransportViewModel(private val repository: RouteRepository) : ViewModel() {

    // 1. LEEMOS DE LA BASE DE DATOS REAL (ROOM)
    val rutas: StateFlow<List<Ruta>> = repository.todasLasRutas
        .map { entidadesDeBaseDeDatos ->
            entidadesDeBaseDeDatos.map { entidad ->
                Ruta(
                    id = entidad.id,
                    nombreLinea = entidad.nombreLinea,
                    origen = entidad.origen,
                    destino = entidad.destino,
                    tiempoEspera = entidad.tiempoEspera
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _modoAccesible = MutableStateFlow(false)
    val modoAccesible: StateFlow<Boolean> = _modoAccesible.asStateFlow()

    private val _saldo = MutableStateFlow(10.0)
    val saldo: StateFlow<Double> = _saldo.asStateFlow()

    // --- ESTADOS DEL MAPA ---
    private val _origen = MutableStateFlow<GeoPoint?>(null)
    val origen: StateFlow<GeoPoint?> = _origen.asStateFlow()

    private val _destino = MutableStateFlow<GeoPoint?>(null)
    val destino: StateFlow<GeoPoint?> = _destino.asStateFlow()

    // Modo de selección en el mapa (0: Nada, 1: Seleccionando Origen, 2: Seleccionando Destino)
    private val _modoSeleccion = MutableStateFlow(0)
    val modoSeleccion: StateFlow<Int> = _modoSeleccion.asStateFlow()

    // RUTA CALCULADA REACTIVA: Se re-calcula de forma automática apenas los paraderos cargan en SQLite o el origen/destino cambian
    val rutaCalculada: StateFlow<RutaCalculada?> = combine(_origen, _destino, repository.todasLasRutas) { o, d, rutasEntidad ->
        if (o != null && d != null && rutasEntidad.isNotEmpty()) {
            RoutingEngine.calcularMejorRuta(o, d, rutasEntidad)
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun inicializarRutas(context: Context) {
        viewModelScope.launch {
            val baseSize = repository.obtenerTodasLasRutasDirecto().size
            if (baseSize == 0) {
                val rutas = com.example.microstrujillo.data.NuevoCaliforniaData.obtenerRutas(context)
                repository.guardarRutaPrueba(rutas)
            }
        }
    }

    fun setModoAccesible(activo: Boolean) {
        _modoAccesible.value = activo
    }

    fun recargar(monto: Double) {
        _saldo.update { it + monto }
    }

    // --- ACCIONES DEL MAPA ---
    
    fun establecerModoSeleccion(modo: Int) {
        _modoSeleccion.value = modo
    }

    fun obtenerUbicacionGPS(context: Context) {
        viewModelScope.launch {
            val ubicacion = LocationHelper.obtenerUbicacionActual(context)
            if (ubicacion != null) {
                _origen.value = ubicacion
            }
        }
    }

    fun enClicMapa(coordenada: GeoPoint) {
        when (_modoSeleccion.value) {
            1 -> {
                _origen.value = coordenada
                _modoSeleccion.value = 0
            }
            2 -> {
                _destino.value = coordenada
                _modoSeleccion.value = 0
            }
            else -> {
                // Si el usuario toca el mapa directamente:
                if (_origen.value == null) {
                    _origen.value = coordenada
                } else {
                    _destino.value = coordenada
                }
            }
        }
    }
}

// FACTORY
class TransportViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransportViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
