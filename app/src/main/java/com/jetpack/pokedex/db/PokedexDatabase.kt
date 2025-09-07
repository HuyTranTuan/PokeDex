package com.jetpack.pokedex.db

//import android.content.Context
//import androidx.privacysandbox.tools.core.generator.build
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [PokemonEntity::class /*, MoveEntity::class, ... */], version = 1, exportSchema = false)
//abstract class PokedexDatabase : RoomDatabase() {
//    abstract fun pokemonDao(): PokemonDao
//    // abstract fun moveDao(): MoveDao
//    // ... other DAOs
//
//    companion object {
//        @Volatile // Ensures visibility of this instance across threads
//        private var INSTANCE: PokedexDatabase? = null
//
//        fun getDatabase(context: Context): PokedexDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PokedexDatabase::class.java,
//                    "pokedex_database" // Name of your database file
//                )
//                    // .fallbackToDestructiveMigration() // Use migrations for production apps
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}