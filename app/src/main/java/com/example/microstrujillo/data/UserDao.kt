package com.example.microstrujillo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microstrujillo.model.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena LIMIT 1")
    suspend fun autenticarUsuario(usuario: String, contrasena: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    suspend fun buscarPorUsuario(usuario: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE recordarSesion = 1 LIMIT 1")
    suspend fun obtenerUsuarioRecordado(): UserEntity?

    @Query("UPDATE usuarios SET recordarSesion = 0")
    suspend fun limpiarSesionesRecordadas()

    @Query("UPDATE usuarios SET recordarSesion = :recordar WHERE id = :userId")
    suspend fun actualizarRecordarSesion(userId: Int, recordar: Boolean)

    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE usuario = :usuario")
    suspend fun actualizarContrasena(usuario: String, nuevaContrasena: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UserEntity): Long

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}