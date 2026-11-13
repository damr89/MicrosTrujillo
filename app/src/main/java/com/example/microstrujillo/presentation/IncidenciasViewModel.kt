package com.example.microstrujillo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microstrujillo.data.IncidenciasRepository
import com.example.microstrujillo.data.remote.AlertaIncidencia
import com.example.microstrujillo.data.remote.TipoIncidencia
import com.example.microstrujillo.domain.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface IncidenciasUiState {
    object Loading : IncidenciasUiState
    data class Success(val incidencias: List<AlertaIncidencia>) : IncidenciasUiState
    data class Error(val mensaje: String) : IncidenciasUiState
}

class IncidenciasViewModel(
    private val repository: IncidenciasRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncidenciasUiState>(IncidenciasUiState.Loading)
    val uiState: StateFlow<IncidenciasUiState> = _uiState.asStateFlow()

    val estaConectado: StateFlow<Boolean> = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = connectivityObserver.estaConectado()
        )

    private val _mensajeFeedback = MutableStateFlow<String?>(null)
    val mensajeFeedback: StateFlow<String?> = _mensajeFeedback.asStateFlow()

    init {
        cargarIncidencias()
    }

    fun cargarIncidencias() {
        viewModelScope.launch {
            _uiState.value = IncidenciasUiState.Loading
            val online = estaConectado.value
            val resultado = repository.obtenerIncidencias(online)

            resultado.onSuccess { lista ->
                _uiState.value = IncidenciasUiState.Success(lista)
            }.onFailure { error ->
                _uiState.value = IncidenciasUiState.Error(
                    error.message ?: "Error al conectar con la API REST de incidencias."
                )
            }
        }
    }

    fun crearIncidencia(linea: String, tipo: TipoIncidencia, descripcion: String) {
        if (linea.isBlank() || descripcion.isBlank()) {
            _mensajeFeedback.value = "Por favor, completa el nombre de la línea y la descripción."
            return
        }

        viewModelScope.launch {
            val online = estaConectado.value
            val resultado = repository.crearIncidencia(linea.trim(), tipo, descripcion.trim(), online)
            resultado.onSuccess {
                _mensajeFeedback.value = if (online) {
                    "✅ Alerta registrada en la API REST con éxito (POST 201)."
                } else {
                    "💾 Alerta guardada localmente (Modo fuera de línea)."
                }
                cargarIncidencias()
            }.onFailure {
                _mensajeFeedback.value = "❌ Error al crear la incidencia."
            }
        }
    }

    fun toggleResuelta(id: Int) {
        viewModelScope.launch {
            val online = estaConectado.value
            val resultado = repository.alternarEstadoResuelta(id, online)
            resultado.onSuccess { alerta ->
                val estado = if (alerta.resuelta) "marcada como resuelta" else "reabierta"
                _mensajeFeedback.value = "🔄 Alerta $estado en el servidor (PUT 200)."
                cargarIncidencias()
            }.onFailure {
                _mensajeFeedback.value = "❌ No se pudo actualizar el estado."
            }
        }
    }

    fun eliminarIncidencia(id: Int) {
        viewModelScope.launch {
            val online = estaConectado.value
            val resultado = repository.eliminarIncidencia(id, online)
            resultado.onSuccess {
                _mensajeFeedback.value = "🗑️ Incidencia eliminada de la API REST (DELETE 200)."
                cargarIncidencias()
            }.onFailure {
                _mensajeFeedback.value = "❌ No se pudo eliminar la alerta."
            }
        }
    }

    fun limpiarMensaje() {
        _mensajeFeedback.value = null
    }
}

class IncidenciasViewModelFactory(
    private val repository: IncidenciasRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncidenciasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IncidenciasViewModel(repository, connectivityObserver) as T
        }
        throw IllegalArgumentException("ViewModel class no reconocida")
    }
}
