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
import com.example.microstrujillo.model.MovimientoEntity
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Ruta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransportViewModel(private val repository: RouteRepository) : ViewModel() {

    companion object {
        const val TARIFA_PASAJE_URBANO = 1.50
    }

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

    private val _rutaSeleccionadaCatalogo = MutableStateFlow<RouteEntity?>(null)
    val rutaSeleccionadaCatalogo: StateFlow<RouteEntity?> = _rutaSeleccionadaCatalogo.asStateFlow()

    // --- DESTINOS FAVORITOS ---
    val destinosFavoritos: List<DestinoFavorito> = CatalogoFavoritosTrujillo.destinos

    private val _nombreDestinoFavorito = MutableStateFlow<String?>(null)
    val nombreDestinoFavorito: StateFlow<String?> = _nombreDestinoFavorito.asStateFlow()

    // --- ACCESIBILIDAD ---
    private val _modoAccesible = MutableStateFlow(false)
    val modoAccesible: StateFlow<Boolean> = _modoAccesible.asStateFlow()

    // --- BILLETERA PERSISTENTE EN ROOM E HISTORIAL DE MOVIMIENTOS ---
    val saldo: StateFlow<Double> = repository.saldoFlow
        .map { it ?: 10.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 10.0
        )

    val movimientos: StateFlow<List<MovimientoEntity>> = repository.movimientosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _mensajePagoExitoso = MutableStateFlow<String?>(null)
    val mensajePagoExitoso: StateFlow<String?> = _mensajePagoExitoso.asStateFlow()

    private val _mensajePagoError = MutableStateFlow<String?>(null)
    val mensajePagoError: StateFlow<String?> = _mensajePagoError.asStateFlow()

    // --- ESTADOS DEL MAPA ---
    private val _origen = MutableStateFlow<GeoPoint?>(null)
    val origen: StateFlow<GeoPoint?> = _origen.asStateFlow()

    private val _destino = MutableStateFlow<GeoPoint?>(null)
    val destino: StateFlow<GeoPoint?> = _destino.asStateFlow()

    private val _modoSeleccion = MutableStateFlow(0)
    val modoSeleccion: StateFlow<Int> = _modoSeleccion.asStateFlow()

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

    init {
        viewModelScope.launch {
            repository.inicializarSaldoSiNoExiste(obtenerFechaHoraActual())
        }
    }

    fun inicializarRutas(context: Context) {
        viewModelScope.launch {
            repository.inicializarSaldoSiNoExiste(obtenerFechaHoraActual())
            val rutasExistentes = repository.obtenerTodasLasRutasDirecto()
            val tieneOtrasEmpresas = rutasExistentes.any {
                it.nombreLinea.contains("Salaverry", ignoreCase = true) ||
                        it.nombreLinea.contains("Cortijo", ignoreCase = true)
            }
            if (rutasExistentes.isEmpty() || tieneOtrasEmpresas) {
                if (tieneOtrasEmpresas) {
                    repository.limpiarRutas()
                }
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

    // --- OPERACIONES DE BILLETERA Y PAGO SIN CONTACTO NFC ---

    fun recargar(monto: Double) {
        viewModelScope.launch {
            repository.registrarRecarga(
                monto = monto,
                fechaHora = obtenerFechaHoraActual()
            )
        }
    }

    fun pagarPasajeNFC() {
        viewModelScope.launch {
            val lineaActiva = rutaCalculada.value?.routeEntity?.nombreLinea
                ?: _rutaSeleccionadaCatalogo.value?.nombreLinea
                ?: "Validador Micro Urbano - Trujillo"

            val resultado = repository.registrarPagoPasajeNFC(
                tarifa = TARIFA_PASAJE_URBANO,
                lineaDetalle = lineaActiva,
                fechaHora = obtenerFechaHoraActual()
            )

            resultado.onSuccess { nuevoSaldo ->
                _mensajePagoExitoso.value =
                    "¡Pasaje pagado con éxito!\n\n" +
                            "• Monto descontado: S/ %.2f\n".format(TARIFA_PASAJE_URBANO) +
                            "• Unidad: $lineaActiva\n" +
                            "• Nuevo saldo disponible: S/ %.2f".format(nuevoSaldo)
            }.onFailure {
                val saldoActual = repository.obtenerSaldoActual()
                _mensajePagoError.value =
                    "Saldo insuficiente para pagar el pasaje.\n\n" +
                            "• Costo del pasaje: S/ %.2f\n".format(TARIFA_PASAJE_URBANO) +
                            "• Tu saldo actual: S/ %.2f\n\n".format(saldoActual) +
                            "Por favor, realiza una recarga antes de acercar tu tarjeta al validador."
            }
        }
    }

    fun limpiarMensajesPago() {
        _mensajePagoExitoso.value = null
        _mensajePagoError.value = null
    }

    // --- ACCIONES DEL MAPA ---

    fun establecerModoSeleccion(modo: Int) {
        _modoSeleccion.value = modo
    }

    fun obtenerUbicacionGPS(
        context: Context,
        onResultado: ((Boolean) -> Unit)? = null,
        onCoordenada: ((GeoPoint) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val ubicacion = LocationHelper.obtenerUbicacionActual(context)
            if (ubicacion != null) {
                _origen.value = ubicacion
                onResultado?.invoke(true)
                onCoordenada?.invoke(ubicacion)
            } else {
                onResultado?.invoke(false)
            }
        }
    }

    fun enClicMapa(coordenada: GeoPoint) {
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

    private fun obtenerFechaHoraActual(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
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