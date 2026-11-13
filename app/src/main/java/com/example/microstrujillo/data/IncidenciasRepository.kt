package com.example.microstrujillo.data

import com.example.microstrujillo.data.remote.AlertaIncidencia
import com.example.microstrujillo.data.remote.IncidenciaDto
import com.example.microstrujillo.data.remote.IncidenciasApiService
import com.example.microstrujillo.data.remote.RetrofitClient
import com.example.microstrujillo.data.remote.TipoIncidencia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class IncidenciasRepository(
    private val api: IncidenciasApiService = RetrofitClient.incidenciasApi
) {
    // Almacén en memoria de incidencias de Trujillo (soporte offline/fallback)
    private val cacheLocal = mutableListOf(
        AlertaIncidencia(
            id = 1,
            linea = "Nuevo California (Línea V)",
            tipo = TipoIncidencia.TRAFICO,
            descripcion = "Tráfico pesado en Av. España y Óvalo Papal por trabajos en la vía.",
            fechaHora = "Hace 10 min",
            resuelta = false
        ),
        AlertaIncidencia(
            id = 2,
            linea = "Nuevo California (Línea A)",
            tipo = TipoIncidencia.DEMORA,
            descripcion = "Demora de 10 minutos cerca a Av. Mansiche y UNT hacia Buenos Aires.",
            fechaHora = "Hace 25 min",
            resuelta = false
        ),
        AlertaIncidencia(
            id = 3,
            linea = "Nuevo California (Línea B1)",
            tipo = TipoIncidencia.DESVIO,
            descripcion = "Desvío provisional por obras de alcantarillado en Av. América Norte.",
            fechaHora = "Hace 1 hora",
            resuelta = true
        )
    )

    private var contadorId = 100

    /**
     * READ: Consulta las incidencias remotas con Retrofit y combina con las de Trujillo
     */
    suspend fun obtenerIncidencias(conectado: Boolean): Result<List<AlertaIncidencia>> = withContext(Dispatchers.IO) {
        if (!conectado) {
            return@withContext Result.success(cacheLocal.toList())
        }

        try {
            // Llamada HTTP real con Retrofit 2
            val respuestaRemota = api.obtenerIncidencias()
            
            // Tomamos una muestra de la API remota para comprobar que Retrofit parseó el JSON
            val mapeados = respuestaRemota.take(2).mapIndexed { index, dto ->
                AlertaIncidencia(
                    id = dto.id ?: (index + 50),
                    linea = "Línea Remota API #${dto.id ?: index}",
                    tipo = if (index % 2 == 0) TipoIncidencia.DEMORA else TipoIncidencia.TRAFICO,
                    descripcion = dto.cuerpo.take(80) + "...",
                    fechaHora = "Sincronizado de API",
                    resuelta = false
                )
            }

            val unificados = (cacheLocal + mapeados).distinctBy { it.id }
            Result.success(unificados)
        } catch (e: Exception) {
            // Resiliencia: si la red falla, retornamos el caché local sin romper la app
            Result.success(cacheLocal.toList())
        }
    }

    /**
     * CREATE: Envía una nueva incidencia a la API REST vía POST con Retrofit
     */
    suspend fun crearIncidencia(
        linea: String,
        tipo: TipoIncidencia,
        descripcion: String,
        conectado: Boolean
    ): Result<AlertaIncidencia> = withContext(Dispatchers.IO) {
        val dto = IncidenciaDto(
            titulo = "$linea - ${tipo.etiqueta}",
            cuerpo = descripcion
        )

        try {
            if (conectado) {
                // Envío POST real a la API REST
                val creadoRemoto = api.crearIncidencia(dto)
                val nuevaAlerta = AlertaIncidencia(
                    id = creadoRemoto.id ?: contadorId++,
                    linea = linea,
                    tipo = tipo,
                    descripcion = descripcion,
                    fechaHora = "Ahora",
                    resuelta = false
                )
                cacheLocal.add(0, nuevaAlerta)
                Result.success(nuevaAlerta)
            } else {
                val nuevaAlerta = AlertaIncidencia(
                    id = contadorId++,
                    linea = linea,
                    tipo = tipo,
                    descripcion = descripcion,
                    fechaHora = "Ahora (Local)",
                    resuelta = false
                )
                cacheLocal.add(0, nuevaAlerta)
                Result.success(nuevaAlerta)
            }
        } catch (e: Exception) {
            // Guardado en modo local
            val nuevaAlerta = AlertaIncidencia(
                id = contadorId++,
                linea = linea,
                tipo = tipo,
                descripcion = descripcion,
                fechaHora = "Ahora (Offline)",
                resuelta = false
            )
            cacheLocal.add(0, nuevaAlerta)
            Result.success(nuevaAlerta)
        }
    }

    /**
     * UPDATE: Modifica el estado de una incidencia vía PUT con Retrofit
     */
    suspend fun alternarEstadoResuelta(
        id: Int,
        conectado: Boolean
    ): Result<AlertaIncidencia> = withContext(Dispatchers.IO) {
        val index = cacheLocal.indexOfFirst { it.id == id }
        if (index == -1) {
            return@withContext Result.failure(Exception("Incidencia no encontrada"))
        }

        val actual = cacheLocal[index]
        val actualizado = actual.copy(resuelta = !actual.resuelta)

        try {
            if (conectado) {
                val dto = IncidenciaDto(
                    id = id,
                    titulo = "${actualizado.linea} - ${actualizado.tipo.etiqueta} (Resuelta: ${actualizado.resuelta})",
                    cuerpo = actualizado.descripcion
                )
                // Llamada PUT a la API REST con Retrofit
                api.actualizarIncidencia(id, dto)
            }
        } catch (_: Exception) {
            // Continuar con actualización local si la red falla
        }

        cacheLocal[index] = actualizado
        Result.success(actualizado)
    }

    /**
     * DELETE: Elimina una incidencia vía DELETE con Retrofit
     */
    suspend fun eliminarIncidencia(
        id: Int,
        conectado: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (conectado) {
                // Llamada DELETE a la API REST con Retrofit
                api.eliminarIncidencia(id)
            }
        } catch (_: Exception) {
            // Continuar con borrado local
        }

        cacheLocal.removeAll { it.id == id }
        Result.success(Unit)
    }
}
