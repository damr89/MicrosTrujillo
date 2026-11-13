package com.example.microstrujillo.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.microstrujillo.data.AppDatabase
import com.example.microstrujillo.data.UserRepository

object Destinos {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val MAPA = "mapa"
    const val RADAR = "radar"
    const val BILLETERA = "billetera"
    const val AJUSTES = "ajustes"
}

private data class Item(val ruta: String, val etiqueta: String, val emoji: String)

private val items = listOf(
    Item(Destinos.INICIO, "Inicio", "🏠"),
    Item(Destinos.MAPA, "Mapa", "🗺️"),
    Item(Destinos.RADAR, "Líneas", "📡"),
    Item(Destinos.BILLETERA, "Billetera", "💳")
)

@Composable
fun TransportApp(
    vm: TransportViewModel = viewModel(),
    loginVm: LoginViewModel? = null
) {
    val context = LocalContext.current
    val actualLoginVm: LoginViewModel = loginVm ?: viewModel(
        factory = LoginViewModelFactory(
            UserRepository(AppDatabase.obtenerBaseDatos(context).userDao())
        )
    )

    val nav = rememberNavController()
    val rutasFiltradas by vm.rutasFiltradas.collectAsState()
    val busquedaLinea by vm.busquedaLinea.collectAsState()
    val accesible by vm.modoAccesible.collectAsState()
    val saldo by vm.saldo.collectAsState()
    val movimientos by vm.movimientos.collectAsState()
    val mensajePagoExitoso by vm.mensajePagoExitoso.collectAsState()
    val mensajePagoError by vm.mensajePagoError.collectAsState()
    val actual = nav.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (actual != Destinos.LOGIN && actual != Destinos.AJUSTES) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = actual == item.ruta,
                            onClick = {
                                nav.navigate(item.ruta) {
                                    popUpTo(Destinos.INICIO) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.emoji) },
                            label = { Text(item.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = Destinos.LOGIN, modifier = Modifier.padding(padding)) {
            composable(Destinos.LOGIN) {
                PantallaLogin(
                    viewModel = actualLoginVm,
                    onLoginSuccess = {
                        nav.navigate(Destinos.INICIO) {
                            popUpTo(Destinos.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Destinos.INICIO) {
                PantallaInicio(
                    accesible = accesible,
                    onToggleAccesible = vm::setModoAccesible,
                    onIrMapa = {
                        vm.limpiarRutaCatalogo()
                        nav.navigate(Destinos.MAPA)
                    },
                    onIrRadar = { nav.navigate(Destinos.RADAR) },
                    onIrBilletera = { nav.navigate(Destinos.BILLETERA) },
                    onIrConfiguracion = { nav.navigate(Destinos.AJUSTES) },
                    favoritos = vm.destinosFavoritos,
                    onSeleccionarFavorito = { favorito ->
                        vm.seleccionarDestinoFavorito(favorito)
                        nav.navigate(Destinos.MAPA)
                    }
                )
            }
            composable(Destinos.MAPA) {
                val origen by vm.origen.collectAsState()
                val destino by vm.destino.collectAsState()
                val rutaCalculada by vm.rutaCalculada.collectAsState()
                val modoSeleccion by vm.modoSeleccion.collectAsState()
                val rutaCatalogo by vm.rutaSeleccionadaCatalogo.collectAsState()
                val nombreFavorito by vm.nombreDestinoFavorito.collectAsState()

                PantallaMapa(
                    origen = origen,
                    destino = destino,
                    rutaCalculada = rutaCalculada,
                    modoSeleccion = modoSeleccion,
                    onEstablecerModo = vm::establecerModoSeleccion,
                    onMapClick = vm::enClicMapa,
                    onGpsRequest = { ctx -> vm.obtenerUbicacionGPS(ctx) },
                    rutaCatalogo = rutaCatalogo,
                    onLimpiarRutaCatalogo = vm::limpiarRutaCatalogo,
                    nombreDestinoFavorito = nombreFavorito
                )
            }
            composable(Destinos.RADAR) {
                PantallaRadar(
                    rutas = rutasFiltradas,
                    accesible = accesible,
                    busqueda = busquedaLinea,
                    onBusquedaChange = vm::actualizarBusquedaLinea,
                    onSeleccionarRuta = { rutaSeleccionada ->
                        vm.seleccionarLineaCatalogo(rutaSeleccionada.id)
                        nav.navigate(Destinos.MAPA)
                    }
                )
            }
            composable(Destinos.BILLETERA) {
                PantallaBilletera(
                    saldo = saldo,
                    accesible = accesible,
                    onRecargar = vm::recargar,
                    movimientos = movimientos,
                    onPagarNFC = vm::pagarPasajeNFC,
                    mensajePagoExitoso = mensajePagoExitoso,
                    mensajePagoError = mensajePagoError,
                    onLimpiarMensajePago = vm::limpiarMensajesPago
                )
            }
            composable(Destinos.AJUSTES) {
                PantallaConfiguracionCuenta(
                    viewModel = actualLoginVm,
                    onVolver = { nav.popBackStack() },
                    onCerrarSesion = {
                        nav.navigate(Destinos.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}