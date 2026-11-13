package com.example.microstrujillo.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microstrujillo.data.NuevoCaliforniaData
import com.example.microstrujillo.data.RouteRepository
import com.example.microstrujillo.domain.LocationHelper
import com.example.microstrujillo.domain.RoutingEngine
import com.example.microstrujillo.domain.RutaCalculada
import com.example.microstrujillo.model.CatalogoFavoritosTrujillo
import com.example.microstrujillo.model.DestinoFavorito
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Ruta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.text.Normalizer

class TransportViewModel(private val repository: RouteRepository) : ViewModel() {

    // Entidades completas desde Room (incluyen shapePoints y stops)
    private val _todasLasEntidades: StateFlow<List<RouteEntity>> = repository.todasLasRutas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista de modelos ligeros para la vista de catálogo
    val rutas: StateFlow<List<Ruta>> = _todasLasEntidades
        .map { entidades ->
            entidades.map { entidad ->
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

    // --- BUSCADOR Y CATÁLOGO DE LÍNEAS ---
    private val _busquedaLinea = MutableStateFlow("")
    val busquedaLinea: StateFlow<String> = _busquedaLinea.asStateFlow()

    val rutasFiltradas: StateFlow<List<Ruta>> = combine(rutas, _busquedaLinea) { listaRutas, query ->
        if (query.isBlank()) {
            listaRutas
        } else {
            val queryNormalizada = normalizarTexto(query)
            listaRutas.filter { ruta ->
                normalizarTexto(ruta.nombreLinea).contains(queryNormalizada) ||
                        normalizarTexto(ruta.origen).contains(queryNormalizada) ||
                        normalizarTexto(ruta.destino).contains(queryNormalizada)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Línea seleccionada desde el catálogo (PantallaRadar) para visualizar su recorrido completo
    private val _rutaSeleccionadaCatalogo = MutableStateFlow<RouteEntity?>(null)
    val rutaSeleccionadaCatalogo: StateFlow<RouteEntity?> = _rutaSeleccionadaCatalogo.asStateFlow()

    // --- DESTINOS FAVORITOS ---
    val destinosFavoritos: List<DestinoFavorito> = CatalogoFavoritosTrujillo.destinos

    private val _nombreDestinoFavorito = MutableStateFlow<String?>(null)
    val nombreDestinoFavorito: StateFlow<String?> = _nombreDestinoFavorito.asStateFlow()

    // --- ACCESIBILIDAD Y BILLETERA ---
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

    // Ruta óptima calculada reactivamente cuando hay origen y destino
    val rutaCalculada: StateFlow<RutaCalculada?> = combine(
        _origen,
        _destino,
        repository.todasLasRutas
    ) { o, d, rutasEntidad ->
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
                val rutasIniciales = NuevoCaliforniaData.obtenerRutas(context)
                repository.guardarRutaPrueba(rutasIniciales)
            }
        }
    }

    fun actualizarBusquedaLinea(nuevoTexto: String) {
        _busquedaLinea.value = nuevoTexto
    }

    fun seleccionarLineaCatalogo(rutaId: Int) {
        val entidad = _todasLasEntidades.value.find { it.id == rutaId }
        if (entidad != null) {
            _rutaSeleccionadaCatalogo.value = entidad
            _modoSeleccion.value = 0
        }
    }

    fun limpiarRutaCatalogo() {
        _rutaSeleccionadaCatalogo.value = null
    }

    fun seleccionarDestinoFavorito(favorito: DestinoFavorito) {
        _rutaSeleccionadaCatalogo.value = null
        _destino.value = favorito.toGeoPoint()
        _nombreDestinoFavorito.value = favorito.nombre
        _modoSeleccion.value = 0
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

    fun obtenerUbicacionGPS(context: Context, onResultado: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val ubicacion = LocationHelper.obtenerUbicacionActual(context)
            if (ubicacion != null) {
                _origen.value = ubicacion
                onResultado?.invoke(true)
            } else {
                onResultado?.invoke(false)
            }
        }
    }

    fun enClicMapa(coordenada: GeoPoint) {
        // Si estaba viendo una línea del catálogo, pasamos al modo de ruteo normal al tocar el mapa
        if (_rutaSeleccionadaCatalogo.value != null && _modoSeleccion.value != 0) {
            _rutaSeleccionadaCatalogo.value = null
        }

        when (_modoSeleccion.value) {
            1 -> {
                _origen.value = coordenada
                _modoSeleccion.value = 0
            }
            2 -> {
                _destino.value = coordenada
                _nombreDestinoFavorito.value = null
                _modoSeleccion.value = 0
            }
            else -> {
                if (_rutaSeleccionadaCatalogo.value == null) {
                    if (_origen.value == null) {
                        _origen.value = coordenada
                    } else {
                        _destino.value = coordenada
                        _nombreDestinoFavorito.value = null
                    }
                }
            }
        }
    }

    private fun normalizarTexto(input: String): String {
        val temp = Normalizer.normalize(input.lowercase().trim(), Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(temp, "")
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
