package com.example.microstrujillo.presentation

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microstrujillo.domain.LocationHelper
import com.example.microstrujillo.domain.RutaCalculada
import com.example.microstrujillo.model.CatalogoFavoritosTrujillo
import com.example.microstrujillo.model.DestinoFavorito
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Ruta
import com.example.microstrujillo.ui.theme.Ambar
import com.example.microstrujillo.ui.theme.Azul
import com.example.microstrujillo.ui.theme.AzulOscuro
import com.example.microstrujillo.ui.theme.Rojo
import com.example.microstrujillo.ui.theme.Verde
import org.osmdroid.util.GeoPoint

private val AmarilloAC = Color(0xFFFFD600)
private val NegroAC = Color.Black

private fun colorEspera(min: Int) = when {
    min <= 5 -> Verde
    min <= 10 -> Ambar
    else -> Rojo
}

private fun estadoEspera(min: Int) = when {
    min <= 5 -> "Llega pronto"
    min <= 10 -> "En camino"
    else -> "Demora"
}

/** Botón gigante, alto contraste (mín. 120dp) para modo accesible. */
@Composable
fun BotonGigante(texto: String, descripcion: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .semantics {
                contentDescription = descripcion
                role = Role.Button
            },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AmarilloAC, contentColor = NegroAC),
        border = BorderStroke(4.dp, NegroAC)
    ) {
        Text(texto, fontSize = 26.sp, fontWeight = FontWeight.Black, lineHeight = 32.sp)
    }
}

@Composable
private fun Cabecera(titulo: String, subtitulo: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Azul, AzulOscuro)))
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 24.dp)
    ) {
        Column {
            Text(titulo, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text(subtitulo, color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)
        }
    }
}

@Composable
private fun TarjetaAccion(emoji: String, titulo: String, detalle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Azul.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 26.sp) }
            Column(Modifier.padding(start = 16.dp)) {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(detalle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun PantallaInicio(
    accesible: Boolean,
    onToggleAccesible: (Boolean) -> Unit,
    onIrMapa: () -> Unit,
    onIrRadar: () -> Unit,
    onIrBilletera: () -> Unit,
    onIrConfiguracion: () -> Unit = {},
    favoritos: List<DestinoFavorito> = CatalogoFavoritosTrujillo.destinos,
    onSeleccionarFavorito: (DestinoFavorito) -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Cabecera("Micros Trujillo", "¿A dónde quieres ir hoy?")
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECCIÓN: DESTINOS FRECUENTES / FAVORITOS
            Text(
                text = "⭐ Destinos frecuentes (1 toque)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AzulOscuro
            )

            if (accesible) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    favoritos.forEach { fav ->
                        BotonGigante(
                            texto = "${fav.emoji} IR A ${fav.nombre.uppercase()}",
                            descripcion = "Fijar destino a ${fav.nombre}, ubicado en ${fav.detalle}",
                            onClick = { onSeleccionarFavorito(fav) }
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    items(favoritos, key = { it.id }) { fav ->
                        Card(
                            onClick = { onSeleccionarFavorito(fav) },
                            modifier = Modifier.width(155.dp),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(3.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Azul.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(fav.emoji, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = fav.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = fav.detalle,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Menú principal",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AzulOscuro
            )

            TarjetaAccion("🗺️", "Mapa de Rutas", "Calcula tu micro según origen y destino", onIrMapa)
            TarjetaAccion("📡", "Catálogo de Líneas", "Busca por empresa o destino y ve su ruta", onIrRadar)
            TarjetaAccion("💳", "Mi billetera", "Saldo y recargas sin contacto", onIrBilletera)
            TarjetaAccion("⚙️", "Configuración de cuenta", "Perfil, seguridad y sesión", onIrConfiguracion)

            // Switch de Modo Accesible
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (accesible) AmarilloAC else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "♿ Modo accesible",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (accesible) NegroAC else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Switch(
                        checked = accesible,
                        onCheckedChange = onToggleAccesible
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRadar(
    rutas: List<Ruta>,
    accesible: Boolean,
    busqueda: String = "",
    onBusquedaChange: (String) -> Unit = {},
    onSeleccionarRuta: (Ruta) -> Unit = {}
) {
    val filtrosRapidos = listOf("Todos", "Nuevo California", "Salaverry", "El Cortijo", "Esperanza", "Larco")

    Column(Modifier.fillMaxSize()) {
        Cabecera("Catálogo de Líneas", "${rutas.size} rutas disponibles en Trujillo")

        // Barra de búsqueda y chips de empresa/destino
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = onBusquedaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar empresa (ej. Nuevo California) o destino...") },
                leadingIcon = { Text("🔍", fontSize = 18.sp) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        TextButton(onClick = { onBusquedaChange("") }) {
                            Text("✕", color = Azul, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Azul,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filtrosRapidos.forEach { filtro ->
                    val seleccionado = (filtro == "Todos" && busqueda.isBlank()) ||
                            (filtro != "Todos" && busqueda.equals(filtro, ignoreCase = true))

                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            if (filtro == "Todos") onBusquedaChange("")
                            else onBusquedaChange(filtro)
                        },
                        label = { Text(filtro) }
                    )
                }
            }
        }

        if (rutas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🚌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No se encontraron líneas para \"$busqueda\"",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Intenta buscar 'Nuevo California', 'Salaverry', 'El Cortijo' o 'Larco'.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rutas, key = { it.id }) { ruta ->
                    if (accesible) {
                        RutaAccesible(ruta = ruta, onClick = { onSeleccionarRuta(ruta) })
                    } else {
                        RutaNormal(ruta = ruta, onClick = { onSeleccionarRuta(ruta) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RutaNormal(ruta: Ruta, onClick: () -> Unit) {
    val color = colorEspera(ruta.tiempoEspera)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${ruta.tiempoEspera}", color = color, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("min", color = color, fontSize = 12.sp)
                    }
                }
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(ruta.nombreLinea, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AzulOscuro)
                    Spacer(Modifier.height(4.dp))
                    Text("🟢 Origen: ${ruta.origen}", style = MaterialTheme.typography.bodyMedium)
                    Text("📍 Destino: ${ruta.destino}", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(estadoEspera(ruta.tiempoEspera), color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Azul.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toca para dibujar recorrido en el mapa",
                    fontSize = 12.sp,
                    color = Azul,
                    fontWeight = FontWeight.SemiBold
                )
                Text("🗺️ →", fontSize = 13.sp, color = Azul, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RutaAccesible(ruta: Ruta, onClick: () -> Unit) {
    val desc = "${ruta.nombreLinea}. Sale de ${ruta.origen} hacia ${ruta.destino}. " +
            "Llega en ${ruta.tiempoEspera} minutos. Toca para ver en el mapa."
    BotonGigante(
        texto = "${ruta.nombreLinea}\n${ruta.origen} → ${ruta.destino}\n${ruta.tiempoEspera} MIN (VER MAPA)",
        descripcion = desc,
        onClick = onClick
    )
}

@Composable
fun PantallaBilletera(saldo: Double, accesible: Boolean, onRecargar: (Double) -> Unit) {
    val saldoTxt = "S/ %.2f".format(saldo)
    Column(Modifier.fillMaxSize()) {
        Cabecera("Billetera", "Paga tu pasaje sin efectivo")
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (accesible) Brush.linearGradient(listOf(AmarilloAC, AmarilloAC))
                        else Brush.linearGradient(listOf(Azul, Color(0xFF6C3BFF)))
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Saldo disponible",
                        color = if (accesible) NegroAC else Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        saldoTxt,
                        color = if (accesible) NegroAC else Color.White,
                        fontSize = if (accesible) 56.sp else 44.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.semantics { contentDescription = "Tu saldo es $saldoTxt" }
                    )
                }
            }
            Text("Recargar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (accesible) {
                BotonGigante("RECARGAR S/ 5", "Recargar cinco soles", { onRecargar(5.0) })
                BotonGigante("RECARGAR S/ 10", "Recargar diez soles", { onRecargar(10.0) })
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onRecargar(5.0) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("+ S/ 5") }
                    OutlinedButton(
                        onClick = { onRecargar(10.0) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("+ S/ 10") }
                }
            }
        }
    }
}

@Composable
fun PantallaMapa(
    origen: GeoPoint?,
    destino: GeoPoint?,
    rutaCalculada: RutaCalculada?,
    modoSeleccion: Int,
    onEstablecerModo: (Int) -> Unit,
    onMapClick: (GeoPoint) -> Unit,
    onGpsRequest: (android.content.Context) -> Unit,
    rutaCatalogo: RouteEntity? = null,
    onLimpiarRutaCatalogo: () -> Unit = {},
    nombreDestinoFavorito: String? = null
) {
    val context = LocalContext.current
    val trujillo = GeoPoint(-8.11599, -79.0258)
    var mapViewRef by remember { mutableStateOf<org.osmdroid.views.MapView?>(null) }

    // 4. GESTIÓN DE PERMISOS DE UBICACIÓN EN TIEMPO DE EJECUCIÓN
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {
            Toast.makeText(context, "Permiso GPS concedido. Obteniendo ubicación...", Toast.LENGTH_SHORT).show()
            onGpsRequest(context)
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de ubicación para fijar tu origen por GPS.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Encuadrar cámara cuando se selecciona una línea desde el Catálogo (PantallaRadar)
    LaunchedEffect(rutaCatalogo) {
        rutaCatalogo?.let { rc ->
            val pts = rc.shapePoints.ifEmpty { rc.recorrido }
            if (pts.size >= 2) {
                val box = org.osmdroid.util.BoundingBox.fromGeoPoints(pts)
                mapViewRef?.post {
                    mapViewRef?.zoomToBoundingBox(box, true, 120)
                }
            }
        }
    }

    // Encuadrar cámara automáticamente en la ruta calculada (Origen -> Destino)
    LaunchedEffect(rutaCalculada) {
        if (rutaCatalogo == null) {
            rutaCalculada?.let { rc ->
                val puntosAEncuadrar = mutableListOf<GeoPoint>().apply {
                    origen?.let { add(it) }
                    destino?.let { add(it) }
                    add(rc.paraderoSubida)
                    add(rc.paraderoBajada)
                    addAll(rc.viajeMicro)
                }
                if (puntosAEncuadrar.size >= 2) {
                    val box = org.osmdroid.util.BoundingBox.fromGeoPoints(puntosAEncuadrar)
                    mapViewRef?.post {
                        mapViewRef?.zoomToBoundingBox(box, true, 120)
                    }
                }
            }
        }
    }

    // Si el usuario fija solo el origen (por GPS) o solo un destino favorito, animar cámara al punto
    LaunchedEffect(origen, destino) {
        if (rutaCatalogo == null && rutaCalculada == null) {
            val puntoFoco = destino ?: origen
            if (puntoFoco != null) {
                mapViewRef?.controller?.animateTo(puntoFoco)
                mapViewRef?.controller?.setZoom(15.5)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.ui.viewinterop.AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                org.osmdroid.views.MapView(ctx).apply {
                    val hotTileSource = org.osmdroid.tileprovider.tilesource.XYTileSource(
                        "OpenStreetMap_HOT",
                        0, 19, 256, ".png",
                        arrayOf(
                            "https://a.tile.openstreetmap.fr/hot/",
                            "https://b.tile.openstreetmap.fr/hot/"
                        )
                    )
                    setTileSource(hotTileSource)
                    setUseDataConnection(true)
                    setMultiTouchControls(true)

                    val boundingBox = org.osmdroid.util.BoundingBox(
                        -7.95, -78.90, -8.22, -79.15
                    )

                    setScrollableAreaLimitDouble(boundingBox)
                    minZoomLevel = 11.0
                    maxZoomLevel = 19.0

                    controller.setZoom(14.0)
                    controller.setCenter(destino ?: origen ?: trujillo)
                    mapViewRef = this
                }
            },
            update = { mapView ->
                mapViewRef = mapView
                mapView.overlays.clear()

                val mReceive = object : org.osmdroid.events.MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        onMapClick(p)
                        return true
                    }
                    override fun longPressHelper(p: GeoPoint): Boolean = false
                }
                mapView.overlays.add(org.osmdroid.views.overlay.MapEventsOverlay(mReceive))

                // MODO 1: Si el usuario seleccionó una línea del Catálogo (PantallaRadar), dibujamos su recorrido
                if (rutaCatalogo != null) {
                    val pts = rutaCatalogo.shapePoints.ifEmpty { rutaCatalogo.recorrido }
                    if (pts.size >= 2) {
                        val polylineCatalogo = org.osmdroid.views.overlay.Polyline(mapView).apply {
                            setPoints(pts)
                            outlinePaint.color = android.graphics.Color.parseColor("#0B5FFF")
                            outlinePaint.strokeWidth = 14f
                        }
                        mapView.overlays.add(polylineCatalogo)

                        val markerInicio = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = pts.first()
                            title = "Inicio: ${rutaCatalogo.origen}"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        val markerFin = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = pts.last()
                            title = "Fin: ${rutaCatalogo.destino}"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        mapView.overlays.addAll(listOf(markerInicio, markerFin))
                    }
                } else {
                    // MODO 2: Ruteo Inteligente Origen -> Destino
                    origen?.let {
                        val m = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = it
                            title = "Origen"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        mapView.overlays.add(m)
                    }

                    destino?.let {
                        val m = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = it
                            title = if (nombreDestinoFavorito != null) "Destino: $nombreDestinoFavorito" else "Destino"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        mapView.overlays.add(m)
                    }

                    rutaCalculada?.let { rc ->
                        val trazadoCompleto = rc.routeEntity.shapePoints.ifEmpty { rc.routeEntity.recorrido }
                        val rutaCompleta = org.osmdroid.views.overlay.Polyline(mapView).apply {
                            setPoints(trazadoCompleto)
                            outlinePaint.color = android.graphics.Color.parseColor("#90A4AE")
                            outlinePaint.strokeWidth = 6f
                        }
                        val caminataInicio = org.osmdroid.views.overlay.Polyline(mapView).apply {
                            setPoints(rc.caminataInicio)
                            outlinePaint.color = android.graphics.Color.DKGRAY
                            outlinePaint.strokeWidth = 8f
                            outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 20f), 0f)
                        }
                        val viajeMicro = org.osmdroid.views.overlay.Polyline(mapView).apply {
                            setPoints(rc.viajeMicro)
                            outlinePaint.color = android.graphics.Color.BLUE
                            outlinePaint.strokeWidth = 14f
                        }
                        val caminataFin = org.osmdroid.views.overlay.Polyline(mapView).apply {
                            setPoints(rc.caminataFin)
                            outlinePaint.color = android.graphics.Color.DKGRAY
                            outlinePaint.strokeWidth = 8f
                            outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 20f), 0f)
                        }

                        mapView.overlays.addAll(listOf(rutaCompleta, caminataInicio, viajeMicro, caminataFin))

                        val markerSubida = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = rc.paraderoSubida
                            title = "Sube aquí"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        val markerBajada = org.osmdroid.views.overlay.Marker(mapView).apply {
                            position = rc.paraderoBajada
                            title = "Baja aquí"
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                            )
                        }
                        mapView.overlays.addAll(listOf(markerSubida, markerBajada))
                    }
                }

                mapView.postInvalidate()
            }
        )

        // Panel de controles superior
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (rutaCatalogo != null) {
                    // Vista cuando se está inspeccionando una línea del Catálogo
                    Text(
                        text = "🚌 ${rutaCatalogo.nombreLinea}",
                        color = Azul,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🟢 Sale de: ${rutaCatalogo.origen}", fontSize = 13.sp, color = Color.DarkGray)
                    Text("📍 Llega a: ${rutaCatalogo.destino}", fontSize = 13.sp, color = Color.DarkGray)
                    Text("⏱️ Frecuencia aprox.: cada ${rutaCatalogo.tiempoEspera} min", fontSize = 13.sp, color = Verde)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onLimpiarRutaCatalogo,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar recorrido y volver al buscador de viajes", color = Azul)
                    }
                } else {
                    // Selector de origen
                    OutlinedButton(
                        onClick = { onEstablecerModo(1) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            if (modoSeleccion == 1) 2.dp else 1.dp,
                            if (modoSeleccion == 1) Azul else Color.LightGray
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟢", modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = if (origen != null) "Origen fijado en mapa" else "Toca para elegir origen (o usa 📍 GPS)",
                                color = if (modoSeleccion == 1) Azul else Color.DarkGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de destino
                    OutlinedButton(
                        onClick = { onEstablecerModo(2) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            if (modoSeleccion == 2) 2.dp else 1.dp,
                            if (modoSeleccion == 2) Rojo else Color.LightGray
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = when {
                                    nombreDestinoFavorito != null -> "Destino: $nombreDestinoFavorito ⭐"
                                    destino != null -> "Destino fijado en mapa"
                                    else -> "¿A dónde vamos?"
                                },
                                color = if (modoSeleccion == 2) Rojo else Color.DarkGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Información de la ruta calculada
                    rutaCalculada?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0F4FF), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    "🚌 Toma: ${it.routeEntity.nombreLinea}",
                                    color = Azul,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "🚍 Recorrido en micro: %.1f km".format(it.distanciaMicroTotal / 1000.0),
                                    color = Color.DarkGray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "🚶 Caminata a paraderos: ${it.distanciaCaminataTotal.toInt()} metros",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "⏳ Tiempo total estimado: ${it.tiempoTotalMinutos} min",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    if (modoSeleccion != 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Toca cualquier parte del mapa para marcar el punto",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Botón flotante para GPS con verificación de permisos en tiempo de ejecución
        Button(
            onClick = {
                if (LocationHelper.tienePermisosUbicacion(context)) {
                    onGpsRequest(context)
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Azul)
        ) {
            Text("📍", fontSize = 20.sp)
        }
    }
}