package com.example.microstrujillo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.microstrujillo.model.MovimientoEntity
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.SaldoEntity
import com.example.microstrujillo.model.UserEntity

@Database(
    entities = [
        RouteEntity::class,
        UserEntity::class,
        MovimientoEntity::class,
        SaldoEntity::class
    ],
    version = 42,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao
    abstract fun userDao(): UserDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trujillo_transport_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}