package com.example.microstrujillo.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microstrujillo.data.remote.AlertaIncidencia
import com.example.microstrujillo.data.remote.TipoIncidencia
import com.example.microstrujillo.ui.theme.Ambar
import com.example.microstrujillo.ui.theme.Azul
import com.example.microstrujillo.ui.theme.AzulOscuro
import com.example.microstrujillo.ui.theme.Rojo
import com.example.microstrujillo.ui.theme.Verde

@Composable
fun PantallaIncidencias(
    viewModel: IncidenciasViewModel,
    accesible: Boolean = false,
    onVolver: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val conectado by viewModel.estaConectado.collectAsState()
    val mensajeFeedback by viewModel.mensajeFeedback.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoCrear by remember { mutableStateOf(false) }

    LaunchedEffect(mensajeFeedback) {
        mensajeFeedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoCrear = true },
                containerColor = Azul,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📢", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reportar (POST)", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // CABECERA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Azul)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Text("⬅️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Alertas en Ruta (API REST)",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Reportes y demoras de micros con Retrofit",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // BANNER DE CONECTIVIDAD (NetworkConnectivityObserver)
            BannerConectividad(conectado = conectado)

            // CONTENIDO SEGÚN ESTADO DE LA UI
            when (val state = uiState) {
                is IncidenciasUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Azul)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Consultando API REST con Retrofit 2...",
                                fontWeight = FontWeight.SemiBold,
                                color = AzulOscuro
                            )
                            Text(
                                text = "GET /posts (JSON)",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                is IncidenciasUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("⚠️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Error de comunicación",
                                    fontWeight = FontWeight.Bold,
                                    color = Rojo,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.mensaje,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.cargarIncidencias() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Azul)
                                ) {
                                    Text("Reintentar conexión")
                                }
                            }
                        }
                    }
                }

                is IncidenciasUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Incidencias activas en Trujillo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = AzulOscuro
                                )
                                TextButton(onClick = { viewModel.cargarIncidencias() }) {
                                    Text("🔄 Actualizar", color = Azul, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (state.incidencias.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("✅", fontSize = 40.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No hay incidencias reportadas",
                                            fontWeight = FontWeight.Bold,
                                            color = AzulOscuro
                                        )
                                        Text(
                                            text = "El tránsito en Trujillo fluye con normalidad.",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        } else {
                            items(state.incidencias, key = { it.id }) { alerta ->
                                ItemIncidenciaCard(
                                    alerta = alerta,
                                    onToggleResuelta = { viewModel.toggleResuelta(alerta.id) },
                                    onEliminar = { viewModel.eliminarIncidencia(alerta.id) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA CREAR INCIDENCIA (POST)
    if (mostrarDialogoCrear) {
        DialogoCrearIncidencia(
            onDismiss = { mostrarDialogoCrear = false },
            onCrear = { linea, tipo, descripcion ->
                viewModel.crearIncidencia(linea, tipo, descripcion)
                mostrarDialogoCrear = false
            }
        )
    }
}

@Composable
private fun BannerConectividad(conectado: Boolean) {
    val bg = if (conectado) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val colorTexto = if (conectado) Verde else Rojo
    val icono = if (conectado) "🟢" else "🔴"
    val mensaje = if (conectado) {
        "En línea • Conectado a la API REST (Retrofit 2)"
    } else {
        "Modo fuera de línea • Sin internet (Mostrando datos locales)"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icono, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = mensaje,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}

@Composable
private fun ItemIncidenciaCard(
    alerta: AlertaIncidencia,
    onToggleResuelta: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.resuelta) Color(0xFFF5F5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alerta.resuelta) 0.dp else 2.dp),
        border = if (alerta.resuelta) BorderStroke(1.dp, Color.LightGray) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(alerta.tipo.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = alerta.linea,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (alerta.resuelta) Color.Gray else AzulOscuro
                        )
                        Text(
                            text = "${alerta.tipo.etiqueta} • ${alerta.fechaHora}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // CHIP DE ESTADO
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (alerta.resuelta) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (alerta.resuelta) "✅ Resuelta" else "⏳ En curso",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alerta.resuelta) Verde else Ambar
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alerta.descripcion,
                fontSize = 14.sp,
                color = if (alerta.resuelta) Color.Gray else Color(0xFF333333),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // BOTONES DE ACCIÓN CRUD (PUT / DELETE)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleResuelta,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (alerta.resuelta) Azul else Verde
                    )
                ) {
                    Text(
                        text = if (alerta.resuelta) "Reabrir (PUT)" else "Resolver (PUT)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEliminar,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                ) {
                    Text("Eliminar (DELETE)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DialogoCrearIncidencia(
    onDismiss: () -> Unit,
    onCrear: (String, TipoIncidencia, String) -> Unit
) {
    var linea by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoIncidencia.DEMORA) }

    val sugerenciasLineas = listOf("Nuevo California (V)", "Nuevo California (A)", "Nuevo California (B1)", "Nuevo California (C)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📢 ", fontSize = 22.sp)
                Text("Reportar Incidencia (POST)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Línea o empresa de micro:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = linea,
                    onValueChange = { linea = it },
                    placeholder = { Text("Ej. Nuevo California") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sugerenciasLineas) { sug ->
                        FilterChip(
                            selected = linea == sug,
                            onClick = { linea = sug },
                            label = { Text(sug, fontSize = 11.sp) }
                        )
                    }
                }

                Text(
                    text = "Tipo de problema:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TipoIncidencia.values().take(2).forEach { tipo ->
                        FilterChip(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo },
                            label = { Text("${tipo.emoji} ${tipo.etiqueta}", fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TipoIncidencia.values().drop(2).forEach { tipo ->
                        FilterChip(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo },
                            label = { Text("${tipo.emoji} ${tipo.etiqueta}", fontSize = 11.sp) }
                        )
                    }
                }

                Text(
                    text = "Detalle del reporte:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = { Text("Ej. Micro varado en Av. Larco, demora estimada 20 min.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCrear(linea, tipoSeleccionado, descripcion) },
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publicar en API REST", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
