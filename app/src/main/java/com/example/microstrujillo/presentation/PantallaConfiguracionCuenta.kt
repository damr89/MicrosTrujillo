package com.example.microstrujillo.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microstrujillo.ui.theme.Azul
import com.example.microstrujillo.ui.theme.AzulOscuro
import com.example.microstrujillo.ui.theme.Rojo
import com.example.microstrujillo.ui.theme.Verde

@Composable
fun PantallaConfiguracionCuenta(
    viewModel: LoginViewModel,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val recordarSesion by viewModel.recordarSesion.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()

    var mostrarDialogoCambiarClave by remember { mutableStateOf(false) }
    var mostrarDialogoConfirmarLogout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Azul, AzulOscuro)))
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onVolver) {
                        Text("← Volver", color = Color.White, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configuración de Cuenta",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Administra tu perfil, credenciales y preferencias",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Información de Usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Azul.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 32.sp)
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = usuarioActual?.usuario ?: "Usuario",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Azul
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Verde)
                            )
                            Text(
                                text = " Sesión activa",
                                fontSize = 13.sp,
                                color = Verde,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Almacenado localmente en Room (SQLite)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Sección: Seguridad
            Text(
                text = "Seguridad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contraseña de acceso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Puedes cambiar tu contraseña actual por una nueva cuando lo desees.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    OutlinedButton(
                        onClick = { mostrarDialogoCambiarClave = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cambiar mi contraseña", color = Azul, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sección: Preferencias de Sesión
            Text(
                text = "Preferencias de Inicio de Sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recordar sesión en este dispositivo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (recordarSesion) "Activo: tus credenciales se mantendrán guardadas" else "Inactivo: deberás ingresar tus datos cada vez",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = recordarSesion,
                        onCheckedChange = { viewModel.actualizarRecordarPerfil(it) }
                    )
                }
            }

            // Sección: Acciones de Sesión
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarDialogoConfirmarLogout = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rojo.copy(alpha = 0.9f))
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // --- DIÁLOGOS ---

    // Diálogo: Cambiar Contraseña desde el Perfil
    if (mostrarDialogoCambiarClave) {
        var claveActual by remember { mutableStateOf("") }
        var nuevaClave by remember { mutableStateOf("") }
        var confirmarClave by remember { mutableStateOf("") }
        var errorLocal by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoCambiarClave = false },
            title = {
                Text(text = "Cambiar Contraseña", fontWeight = FontWeight.Bold, color = Azul)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = claveActual,
                        onValueChange = {
                            claveActual = it
                            errorLocal = null
                        },
                        label = { Text("Contraseña actual") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = nuevaClave,
                        onValueChange = {
                            nuevaClave = it
                            errorLocal = null
                        },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = confirmarClave,
                        onValueChange = {
                            confirmarClave = it
                            errorLocal = null
                        },
                        label = { Text("Confirmar nueva contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (errorLocal != null) {
                        Text(
                            text = errorLocal ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (claveActual.isBlank() || nuevaClave.isBlank() || confirmarClave.isBlank()) {
                            errorLocal = "Por favor completa todos los campos"
                        } else if (nuevaClave != confirmarClave) {
                            errorLocal = "Las nuevas contraseñas no coinciden"
                        } else if (nuevaClave.length < 4) {
                            errorLocal = "La contraseña debe tener al menos 4 caracteres"
                        } else {
                            viewModel.cambiarContrasenaPerfil(claveActual, nuevaClave, confirmarClave)
                            mostrarDialogoCambiarClave = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCambiarClave = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Diálogo: Confirmar Cerrar Sesión
    if (mostrarDialogoConfirmarLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmarLogout = false },
            title = {
                Text(text = "Cerrar sesión", fontWeight = FontWeight.Bold, color = Rojo)
            },
            text = {
                Text(text = "¿Estás seguro de que deseas cerrar tu sesión en este dispositivo?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoConfirmarLogout = false
                        viewModel.cerrarSesion {
                            onCerrarSesion()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmarLogout = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Diálogo de Error
    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            title = { Text("Aviso", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text(mensajeError ?: "") },
            confirmButton = {
                Button(
                    onClick = { viewModel.limpiarError() },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de Éxito
    if (mensajeExito != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarExito() },
            title = { Text("¡Éxito!", fontWeight = FontWeight.Bold, color = Azul) },
            text = { Text(mensajeExito ?: "") },
            confirmButton = {
                Button(
                    onClick = { viewModel.limpiarExito() },
                    colors = ButtonDefaults.buttonColors(containerColor = Azul)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}
