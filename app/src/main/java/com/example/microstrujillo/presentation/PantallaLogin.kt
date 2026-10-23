package com.example.microstrujillo.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microstrujillo.R
import com.example.microstrujillo.data.AppDatabase
import com.example.microstrujillo.data.UserRepository
import com.example.microstrujillo.ui.theme.Azul

@Composable
fun PantallaLogin(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel? = null
) {
    val context = LocalContext.current
    val actualViewModel: LoginViewModel = viewModel ?: viewModel(
        factory = LoginViewModelFactory(
            UserRepository(AppDatabase.obtenerBaseDatos(context).userDao())
        )
    )

    val usuario by actualViewModel.usuario.collectAsState()
    val contrasena by actualViewModel.contrasena.collectAsState()
    val recordar by actualViewModel.recordarSesion.collectAsState()
    val passwordVisible by actualViewModel.passwordVisible.collectAsState()
    val cargando by actualViewModel.cargando.collectAsState()
    val mensajeError by actualViewModel.mensajeError.collectAsState()
    val mensajeExito by actualViewModel.mensajeExito.collectAsState()
    val mostrarDialogoRegistro by actualViewModel.mostrarDialogoRegistro.collectAsState()
    val mostrarDialogoRecuperar by actualViewModel.mostrarDialogoRecuperar.collectAsState()
    val loginExitoso by actualViewModel.loginExitoso.collectAsState()

    // Manejo reactivo de navegación cuando el login es exitoso
    LaunchedEffect(loginExitoso) {
        if (loginExitoso) {
            onLoginSuccess()
            actualViewModel.resetLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_micro_real),
                contentDescription = "Logo MicrosTrujillo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título de la app
        Text(
            text = "MicrosTrujillo",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Azul
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo de texto de Usuario
        OutlinedTextField(
            value = usuario,
            onValueChange = actualViewModel::onUsuarioChange,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto de Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = actualViewModel::onContrasenaChange,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            enabled = !cargando,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = actualViewModel::togglePasswordVisible) {
                    Text(
                        if (passwordVisible) "Ocultar" else "Ver",
                        color = Azul,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Casilla Recordar y Botón de Olvidaste contraseña
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = recordar,
                    onCheckedChange = actualViewModel::onRecordarChange,
                    enabled = !cargando
                )
                Text(text = "Recordar", fontSize = 14.sp)
            }

            TextButton(
                onClick = { actualViewModel.abrirDialogoRecuperar() },
                enabled = !cargando
            ) {
                Text(text = "¿Olvidaste tu contraseña?", fontSize = 14.sp, color = Azul)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón principal de Iniciar Sesión
        Button(
            onClick = { actualViewModel.iniciarSesion() },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Azul)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Crear Cuenta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "¿No tienes cuenta?", fontSize = 15.sp, color = Color.Gray)
            TextButton(
                onClick = { actualViewModel.abrirDialogoRegistro() },
                enabled = !cargando
            ) {
                Text(text = "Crea cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Azul)
            }
        }
    }

    // --- DIÁLOGOS EMERGENTES ---

    // 1. Diálogo de Crear Cuenta
    if (mostrarDialogoRegistro) {
        var nuevoUsuario by remember { mutableStateOf("") }
        var nuevoPass by remember { mutableStateOf("") }
        var confirmarPass by remember { mutableStateOf("") }
        var passVisibleDialogo by remember { mutableStateOf(false) }
        var errorLocal by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { actualViewModel.cerrarDialogoRegistro() },
            title = {
                Text(text = "Crear nueva cuenta", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Azul)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ingresa tus datos para registrar una nueva cuenta en MicrosTrujillo.",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = nuevoUsuario,
                        onValueChange = {
                            nuevoUsuario = it
                            errorLocal = null
                        },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = nuevoPass,
                        onValueChange = {
                            nuevoPass = it
                            errorLocal = null
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passVisibleDialogo) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passVisibleDialogo = !passVisibleDialogo }) {
                                Text(if (passVisibleDialogo) "Ocultar" else "Ver", color = Azul, fontSize = 12.sp)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = confirmarPass,
                        onValueChange = {
                            confirmarPass = it
                            errorLocal = null
                        },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passVisibleDialogo) VisualTransformation.None else PasswordVisualTransformation()
                    )
                    if (errorLocal != null) {
                        Text(
                            text = errorLocal ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoUsuario.isBlank() || nuevoPass.isBlank() || confirmarPass.isBlank()) {
                            errorLocal = "Por favor completa todos los campos"
                        } else if (nuevoPass != confirmarPass) {
                            errorLocal = "Las contraseñas no coinciden"
                        } else if (nuevoPass.length < 4) {
                            errorLocal = "La contraseña debe tener al menos 4 caracteres"
                        } else {
                            actualViewModel.registrarUsuario(nuevoUsuario, nuevoPass, confirmarPass)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { actualViewModel.cerrarDialogoRegistro() }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // 2. Diálogo de Restablecer Contraseña
    if (mostrarDialogoRecuperar) {
        var usuarioRecup by remember { mutableStateOf("") }
        var nuevaPass by remember { mutableStateOf("") }
        var confirmarPass by remember { mutableStateOf("") }
        var passVisibleDialogo by remember { mutableStateOf(false) }
        var errorLocal by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { actualViewModel.cerrarDialogoRecuperar() },
            title = {
                Text(text = "Restablecer contraseña", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Azul)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ingresa tu nombre de usuario y tu nueva contraseña para actualizarla.",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = usuarioRecup,
                        onValueChange = {
                            usuarioRecup = it
                            errorLocal = null
                        },
                        label = { Text("Usuario registrado") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = nuevaPass,
                        onValueChange = {
                            nuevaPass = it
                            errorLocal = null
                        },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passVisibleDialogo) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passVisibleDialogo = !passVisibleDialogo }) {
                                Text(if (passVisibleDialogo) "Ocultar" else "Ver", color = Azul, fontSize = 12.sp)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = confirmarPass,
                        onValueChange = {
                            confirmarPass = it
                            errorLocal = null
                        },
                        label = { Text("Confirmar nueva contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passVisibleDialogo) VisualTransformation.None else PasswordVisualTransformation()
                    )
                    if (errorLocal != null) {
                        Text(
                            text = errorLocal ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (usuarioRecup.isBlank() || nuevaPass.isBlank() || confirmarPass.isBlank()) {
                            errorLocal = "Por favor completa todos los campos"
                        } else if (nuevaPass != confirmarPass) {
                            errorLocal = "Las contraseñas no coinciden"
                        } else if (nuevaPass.length < 4) {
                            errorLocal = "La contraseña debe tener al menos 4 caracteres"
                        } else {
                            actualViewModel.restablecerContrasena(usuarioRecup, nuevaPass, confirmarPass)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { actualViewModel.cerrarDialogoRecuperar() }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // 3. Diálogo de Notificación de Error
    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = { actualViewModel.limpiarError() },
            title = {
                Text(text = "Aviso", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            },
            text = {
                Text(text = mensajeError ?: "")
            },
            confirmButton = {
                Button(
                    onClick = { actualViewModel.limpiarError() },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // 4. Diálogo de Notificación de Éxito
    if (mensajeExito != null) {
        AlertDialog(
            onDismissRequest = { actualViewModel.limpiarExito() },
            title = {
                Text(text = "¡Operación Exitosa!", fontWeight = FontWeight.Bold, color = Azul)
            },
            text = {
                Text(text = mensajeExito ?: "")
            },
            confirmButton = {
                Button(
                    onClick = { actualViewModel.limpiarExito() },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}
