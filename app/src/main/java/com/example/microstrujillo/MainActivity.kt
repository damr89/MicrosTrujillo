package com.example.microstrujillo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microstrujillo.data.AppDatabase
import com.example.microstrujillo.data.RouteRepository
import com.example.microstrujillo.data.UserRepository
import com.example.microstrujillo.presentation.LoginViewModel
import com.example.microstrujillo.presentation.LoginViewModelFactory
import com.example.microstrujillo.presentation.TransportApp
import com.example.microstrujillo.presentation.TransportViewModel
import com.example.microstrujillo.presentation.TransportViewModelFactory
import com.example.microstrujillo.ui.theme.MicrosTrujilloTheme
import org.osmdroid.config.Configuration
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. INICIALIZAMOS EL MAPA GLOBALMENTE AQUI
        val ctx = applicationContext
        
        // Configuramos la ruta de preferencias
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
        
        // User-Agent obligatorio para OpenStreetMap
        Configuration.getInstance().userAgentValue = "MicrosTrujilloAppEstudiantil/1.0 (universidad_trujillo_mobile_app@gmail.com)"
        
        // Configurar directorio de caché estable
        val basePath = File(ctx.cacheDir, "osmdroid_hot")
        basePath.mkdirs()
        Configuration.getInstance().osmdroidBasePath = basePath
        val tileCache = File(basePath, "tiles")
        tileCache.mkdirs()
        Configuration.getInstance().osmdroidTileCache = tileCache

        enableEdgeToEdge()

        // 2. INICIALIZAMOS EL BACKEND (ROOM DATABASE + REPOSITORIOS)
        val database = AppDatabase.obtenerBaseDatos(this)
        val routeRepository = RouteRepository(database.routeDao())
        val userRepository = UserRepository(database.userDao())

        setContent {
            MicrosTrujilloTheme {

                // 3. CREAMOS LOS VIEWMODELS MEDIANTE SUS RESPECTIVAS FACTORIES
                val transportViewModel: TransportViewModel = viewModel(
                    factory = TransportViewModelFactory(routeRepository)
                )
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(userRepository)
                )

                // Inicializar rutas GeoJSON desde assets/routes/
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    transportViewModel.inicializarRutas(applicationContext)
                }

                // 4. PASAMOS AMBOS VIEWMODELS A LA APLICACIÓN PRINCIPAL
                TransportApp(
                    vm = transportViewModel,
                    loginVm = loginViewModel
                )
            }
        }
    }
}
