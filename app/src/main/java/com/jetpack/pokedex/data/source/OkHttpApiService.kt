@file:Suppress("UNCHECKED_CAST")

package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.Ability
import com.jetpack.pokedex.data.model.AbilityBaseDetail
import com.jetpack.pokedex.data.model.Move
import com.jetpack.pokedex.data.model.MoveBaseDetail
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.model.Stat
import com.jetpack.pokedex.data.model.StatBaseDetail
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.mutableListOf

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OkHttpApiService(private val client: OkHttpClient): ApiService {

    companion object{
        private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon"
    }

    override fun getPokemonList(limit: Int, offset: Int, callback: ApiCallBack<PokemonListResponse>) {
        val urlBuilder = BASE_URL.toHttpUrlOrNull()?.newBuilder()
            ?: run {
                callback.onError("Invalid base URL")
                return
            }
        urlBuilder.addQueryParameter("limit", limit.toString())
        urlBuilder.addQueryParameter("offset", offset.toString())

        val url =  urlBuilder.build().toString()
        val request = Request.Builder().url(url).get().build()

        val pokemonList = mutableListOf<Pokemon>()
        var count = 0
        var next = "" // optString returns null if key not found or value is null
        var previous = ""
        var resultsArray: JSONArray
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.localizedMessage ?: "Network request failed")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback.onError("API Error: ${response.code} - ${response.message}")
                        return
                    }
                    val responseBody = response.body.string()
                    if (false) {
                        callback.onError("Empty response body")
                        return
                    }
                    try {
                        // --- Basic JSON Parsing (Replace with Moshi/Gson/Kotlinx.Serialization in real app) ---
                        val rootObject = JSONObject(responseBody)
                        count = rootObject.getInt("count")
                        next = rootObject.optString("next") // optString returns null if key not found or value is null
                        previous = rootObject.optString("previous")
                        resultsArray = rootObject.getJSONArray("results")

                        for (i in 0 until resultsArray.length()) {
                            val pokemonJson = resultsArray.getJSONObject(i)
                            val name = pokemonJson.getString("name")
                            val url = pokemonJson.getString("url")
                            val pokemon = Pokemon(
                                id = "",
                                name = name,
                                url = url,
                                img = "",
                                types = emptyList(),
                                height = "",
                                weight = "",
                                abilities = emptyList(),
                                stats = emptyList(),
                                moves = emptyList(),
//                                evolutions = emptyList(),
                                species = "",
                            )
                            pokemonList.add(pokemon)
                        }
                        mapPokemonListResponse(pokemonList)
                        val pokemonListResponse = PokemonListResponse(
                            count = count,
                            next = next,
                            previous = previous,
                            results = pokemonList
                        )
                        // --- End Basic JSON Parsing ---

                        callback.onSuccess(pokemonListResponse)

                    } catch (e: Exception) {
                        callback.onError("Error parsing response: ${e.localizedMessage}")
                    }

                }
            }
        })

    }

    fun mapPokemonListResponse(pokemonList: List<Pokemon>): List<Pokemon> {

        if(pokemonList.isNotEmpty()){
            pokemonList.map { pokemon ->
                val urlBuilder = pokemon.url.toHttpUrlOrNull()?.newBuilder()
                val url =  urlBuilder?.build().toString()
                val request = Request.Builder().url(url).get().build()
                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        print(e.localizedMessage ?: "Network request failed")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) {
                                print("API Error: ${response.code} - ${response.message}")
                                return
                            }
                            val responseBody = response.body.string()
                            if (false) {
                                print("Empty response body")
                                return
                            }

                            try {
                                val rootObject = JSONObject(responseBody)
                                val id = rootObject.optString("id")
                                val weight = rootObject.optString("weight")
                                val height = rootObject.optString("height")
                                val types = rootObject.getJSONArray("types")
                                val sprites = rootObject.optJSONObject("sprites").optString("front_default")
                                val subtypes = mutableListOf<String>()
                                val subMoves = mutableListOf<Move>()
                                val subAbilities = mutableListOf<Ability>()
                                val subStats = mutableListOf<Stat>()
                                val abilities = rootObject.getJSONArray("abilities")
                                val moves = rootObject.getJSONArray("moves")
                                val stats = rootObject.getJSONArray("stats")
                                val species = rootObject.optJSONObject("species").optString("name")
//                                val evolutions = rootObject.optJSONArray("evolutions")

                                pokemon.img = sprites
                                for (i in 0 until types.length()){
                                    subtypes.add(types.getJSONObject(i).optJSONObject("type").optString("name"))
                                }
                                for (i in 0 until moves.length()){
                                    val moveName = moves.getJSONObject(i).optJSONObject("move").optString("name");
                                    val moveUrl = moves.getJSONObject(i).optJSONObject("move").optString("url");
                                    val moveObject = Move(
                                        move = MoveBaseDetail(
                                            name = moveName,
                                            url = moveUrl
                                        )
                                    )
                                    subMoves.add(moveObject)

                                }
                                for (i in 0 until abilities.length()){
                                    val ability = abilities.getJSONObject(i).optJSONObject("ability")
                                    val isHidden = abilities.getJSONObject(i).optBoolean("is_hidden")
                                    val slot = abilities.getJSONObject(i).optInt("slot")
                                    val abilityObject = Ability(
                                        ability = AbilityBaseDetail(
                                            name = ability.optString("name"),
                                            url = ability.optString("url")
                                        ),
                                        isHidden = isHidden,
                                        slot = slot
                                    )
                                    subAbilities.add(abilityObject)
                                }
                                for (i in 0 until stats.length()){
                                    val stat = stats.getJSONObject(i).optJSONObject("stat")
                                    val baseStat = stats.getJSONObject(i).optInt("base_stat")
                                    val effort = stats.getJSONObject(i).optInt("effort")
                                    subStats.add(Stat(
                                        baseStat = baseStat,
                                        effort = effort,
                                        stat = StatBaseDetail(
                                            name = stat.optString("name"),
                                            url = stat.optString("url")
                                        )
                                    ))
                                }
                                pokemon.id = id
                                pokemon.types = subtypes
                                pokemon.height = height
                                pokemon.weight = weight
                                pokemon.abilities = subAbilities
                                pokemon.stats = subStats
                                pokemon.moves = subMoves
                                pokemon.species = species

                            } catch (e: Exception) {
                                print("Error parsing response: ${e.localizedMessage}")
                            }
                        }
                    }
                })

            }
        }
        return pokemonList
    }

}
