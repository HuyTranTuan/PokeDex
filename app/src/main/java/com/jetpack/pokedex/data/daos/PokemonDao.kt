package com.jetpack.pokedex.data.daos

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow // Use Flow for reactive updates
//
//@Dao
//interface PokemonDao {
//    @Query("SELECT * FROM pokemon_list ORDER BY id ASC")
//    fun getAllPokemon(): Flow<List<PokemonEntity>> // Observe changes with Flow
//
//    @Query("SELECT * FROM pokemon_list WHERE id = :pokemonId")
//    fun getPokemonById(pokemonId: Int): Flow<PokemonEntity?> // Or PokemonEntity if it's always expected
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace if data already exists (good for updates)
//    suspend fun insertAll(pokemon: List<PokemonEntity>)
//
//    @Query("DELETE FROM pokemon_list") // For clearing cache
//    suspend fun clearAll()
//
//    @Query("SELECT COUNT(*) FROM pokemon_list")
//    suspend fun count(): Int // To check if the cache is empty
//}