package com.example.microstrujillo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microstrujillo.data.UserRepository
import com.example.microstrujillo.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _usuario = MutableStateFlow("")
    val usuario: StateFlow<String> = _usuario.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    private val _recordarSesion = MutableStateFlow(false)
    val recordarSesion: StateFlow<Boolean> = _recordarSesion.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito: StateFlow<String?> = _mensajeExito.asStateFlow()

    private val _mostrarDialogoRegistro = MutableStateFlow(false)
    val mostrarDialogoRegistro: StateFlow<Boolean> = _mostrarDialogoRegistro.asStateFlow()

    private val _mostrarDialogoRecuperar = MutableStateFlow(false)
    val mostrarDialogoRecuperar: StateFlow<Boolean> = _mostrarDialogoRecuperar.asStateFlow()

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso.asStateFlow()

    // Usuario actualmente autenticado en la sesión
    private val _usuarioActual = MutableStateFlow<UserEntity?>(null)
    val usuarioActual: StateFlow<UserEntity?> = _usuarioActual.asStateFlow()

    init {
        inicializar()
    }

    fun inicializar() {
        viewModelScope.launch {
            // 1. Crear usuario semilla (admin / 1234) si la base de datos está vacía
            userRepository.inicializarUsuarioPorDefecto()

            // 2. Comprobar si existe una sesión recordada previamente
            val recordado = userRepository.obtenerUsuarioRecordado()
            if (recordado != null) {
                _usuario.value = recordado.usuario
                _contrasena.value = recordado.contrasena
                _recordarSesion.value = true
                _usuarioActual.value = recordado
            }
        }
    }

    fun onUsuarioChange(nuevoUsuario: String) {
        _usuario.value = nuevoUsuario
    }

    fun onContrasenaChange(nuevaContrasena: String) {
        _contrasena.value = nuevaContrasena
    }

    fun onRecordarChange(recordar: Boolean) {
        _recordarSesion.value = recordar
    }

    fun togglePasswordVisible() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun iniciarSesion(onSuccess: (() -> Unit)? = null) {
        val u = _usuario.value.trim()
        val c = _contrasena.value.trim()

        if (u.isEmpty() || c.isEmpty()) {
            _mensajeError.value = "Por favor ingresa tu usuario y contraseña."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                val user = userRepository.autenticar(u, c)
                if (user != null) {
                    userRepository.guardarPreferenciaRecordar(user.id, _recordarSesion.value)
                    _usuarioActual.value = user.copy(recordarSesion = _recordarSesion.value)
                    _loginExitoso.value = true
                    onSuccess?.invoke()
                } else {
                    _mensajeError.value = "Credenciales incorrectas. Verifica tu usuario y contraseña."
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al iniciar sesión: ${e.localizedMessage ?: e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun abrirDialogoRegistro() {
        _mensajeError.value = null
        _mostrarDialogoRegistro.value = true
    }

    fun cerrarDialogoRegistro() {
        _mostrarDialogoRegistro.value = false
    }

    fun registrarUsuario(usuarioNuevo: String, pass: String, confirmarPass: String) {
        val u = usuarioNuevo.trim()
        val p = pass.trim()
        val cp = confirmarPass.trim()

        if (u.isEmpty() || p.isEmpty() || cp.isEmpty()) {
            _mensajeError.value = "Por favor completa todos los campos del registro."
            return
        }

        if (p != cp) {
            _mensajeError.value = "Las contraseñas no coinciden."
            return
        }

        if (p.length < 4) {
            _mensajeError.value = "La contraseña debe tener al menos 4 caracteres."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                if (userRepository.existeUsuario(u)) {
                    _mensajeError.value = "El usuario '$u' ya se encuentra registrado."
                } else {
                    val nuevoUsuarioEntity = UserEntity(
                        usuario = u,
                        contrasena = p,
                        recordarSesion = false
                    )
                    userRepository.registrarUsuario(nuevoUsuarioEntity)
                    _mostrarDialogoRegistro.value = false
                    _usuario.value = u
                    _contrasena.value = p
                    _mensajeExito.value = "¡Cuenta creada exitosamente para '$u'! Ya puedes iniciar sesión."
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al registrar: ${e.localizedMessage ?: e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun abrirDialogoRecuperar() {
        _mensajeError.value = null
        _mostrarDialogoRecuperar.value = true
    }

    fun cerrarDialogoRecuperar() {
        _mostrarDialogoRecuperar.value = false
    }

    fun restablecerContrasena(usuarioRecup: String, nuevaPass: String, confirmarPass: String) {
        val u = usuarioRecup.trim()
        val p = nuevaPass.trim()
        val cp = confirmarPass.trim()

        if (u.isEmpty() || p.isEmpty() || cp.isEmpty()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        if (p != cp) {
            _mensajeError.value = "Las contraseñas no coinciden."
            return
        }

        if (p.length < 4) {
            _mensajeError.value = "La nueva contraseña debe tener al menos 4 caracteres."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                if (!userRepository.existeUsuario(u)) {
                    _mensajeError.value = "No se encontró ningún usuario con el nombre '$u'."
                } else {
                    val actualizada = userRepository.restablecerContrasena(u, p)
                    if (actualizada) {
                        _mostrarDialogoRecuperar.value = false
                        _usuario.value = u
                        _contrasena.value = p
                        _mensajeExito.value = "Contraseña restablecida exitosamente para '$u'."
                    } else {
                        _mensajeError.value = "No se pudo actualizar la contraseña. Inténtalo nuevamente."
                    }
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al restablecer contraseña: ${e.localizedMessage ?: e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // --- CONFIGURACIÓN DE LA CUENTA ---

    fun cambiarContrasenaPerfil(claveActual: String, nuevaClave: String, confirmarClave: String) {
        val u = _usuarioActual.value
        if (u == null) {
            _mensajeError.value = "No hay una sesión de usuario activa."
            return
        }

        if (claveActual.isBlank() || nuevaClave.isBlank() || confirmarClave.isBlank()) {
            _mensajeError.value = "Por favor completa todos los campos."
            return
        }

        if (claveActual != u.contrasena) {
            _mensajeError.value = "La contraseña actual no es correcta."
            return
        }

        if (nuevaClave != confirmarClave) {
            _mensajeError.value = "Las nuevas contraseñas no coinciden."
            return
        }

        if (nuevaClave.length < 4) {
            _mensajeError.value = "La nueva contraseña debe tener al menos 4 caracteres."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                val ok = userRepository.restablecerContrasena(u.usuario, nuevaClave)
                if (ok) {
                    val usuarioActualizado = u.copy(contrasena = nuevaClave)
                    _usuarioActual.value = usuarioActualizado
                    _contrasena.value = nuevaClave
                    _mensajeExito.value = "¡Contraseña actualizada correctamente!"
                } else {
                    _mensajeError.value = "No se pudo actualizar la contraseña."
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al cambiar contraseña: ${e.localizedMessage ?: e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarRecordarPerfil(activo: Boolean) {
        val u = _usuarioActual.value ?: return
        _recordarSesion.value = activo
        viewModelScope.launch {
            userRepository.guardarPreferenciaRecordar(u.id, activo)
            _usuarioActual.value = u.copy(recordarSesion = activo)
        }
    }

    fun cerrarSesion(onLogout: () -> Unit) {
        viewModelScope.launch {
            userRepository.guardarPreferenciaRecordar(0, false)
            _usuarioActual.value = null
            _loginExitoso.value = false
            _contrasena.value = ""
            _recordarSesion.value = false
            onLogout()
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun limpiarExito() {
        _mensajeExito.value = null
    }

    fun resetLoginExitoso() {
        _loginExitoso.value = false
    }
}

// FACTORY
class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
