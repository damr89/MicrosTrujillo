package com.example.microstrujillo.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

object Destinos {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val RADAR = "radar"
    const val BILLETERA = "billetera"
}

private data class Item(val ruta: String, val etiqueta: String, val emoji: String)

private val items = listOf(
    Item(Destinos.INICIO, "Inicio", "🏠"),
    Item(Destinos.RADAR, "Radar", "📡"),
    Item(Destinos.BILLETERA, "Billetera", "💳")
)

@Composable
fun TransportApp(vm: TransportViewModel = viewModel()) {
    val nav = rememberNavController()
    val rutas by vm.rutas.collectAsState()
    val accesible by vm.modoAccesible.collectAsState()
    val saldo by vm.saldo.collectAsState()
    val actual = nav.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (actual != Destinos.LOGIN) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = actual == item.ruta,
                            onClick = {
                                nav.navigate(item.ruta) {
                                    popUpTo(Destinos.INICIO)
                                    launchSingleTop = true
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
                    onIrRadar = { nav.navigate(Destinos.RADAR) },
                    onIrBilletera = { nav.navigate(Destinos.BILLETERA) }
                )
            }
            composable(Destinos.RADAR) {
                // Obtenemos los estados para el mapa
                val origen = vm.origen.collectAsState().value
                val destino = vm.destino.collectAsState().value
                val rutaCalculada = vm.rutaCalculada.collectAsState().value
                val modoSeleccion = vm.modoSeleccion.collectAsState().value

                PantallaMapa(
                    origen = origen,
                    destino = destino,
                    rutaCalculada = rutaCalculada,
                    modoSeleccion = modoSeleccion,
                    onEstablecerModo = vm::establecerModoSeleccion,
                    onMapClick = vm::enClicMapa,
                    onGpsRequest = vm::obtenerUbicacionGPS
                )
            }
            composable(Destinos.BILLETERA) { PantallaBilletera(saldo, accesible, vm::recargar) }
        }
    }
}
