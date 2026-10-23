package com.example.microstrujillo.data

import com.example.microstrujillo.model.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun autenticar(usuario: String, contrasena: String): UserEntity? {
        return userDao.autenticarUsuario(usuario, contrasena)
    }

    suspend fun existeUsuario(usuario: String): Boolean {
        return userDao.buscarPorUsuario(usuario) != null
    }

    suspend fun registrarUsuario(usuario: UserEntity) {
        userDao.insertarUsuario(usuario)
    }

    suspend fun obtenerUsuarioRecordado(): UserEntity? {
        return userDao.obtenerUsuarioRecordado()
    }

    suspend fun guardarPreferenciaRecordar(userId: Int, recordar: Boolean) {
        userDao.limpiarSesionesRecordadas()
        if (recordar) {
            userDao.actualizarRecordarSesion(userId, true)
        }
    }

    suspend fun restablecerContrasena(usuario: String, nuevaContrasena: String): Boolean {
        val filasAfectadas = userDao.actualizarContrasena(usuario, nuevaContrasena)
        return filasAfectadas > 0
    }

    suspend fun inicializarUsuarioPorDefecto() {
        if (userDao.contarUsuarios() == 0) {
            userDao.insertarUsuario(
                UserEntity(
                    usuario = "admin",
                    contrasena = "1234",
                    recordarSesion = false
                )
            )
        }
    }
}