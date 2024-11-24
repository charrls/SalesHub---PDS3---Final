package com.example.saleshub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.saleshub.TypeConverter.Converters
import com.example.saleshub.model.Client
import com.example.saleshub.model.Product
import com.example.saleshub.model.Sale

@Database(entities = [Product::class, Client::class, Sale::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun clientDao(): ClientDao
    abstract fun salesDao(): SalesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "saleshub_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }
}
