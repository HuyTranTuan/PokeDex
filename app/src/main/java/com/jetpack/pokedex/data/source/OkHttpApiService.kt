@file:Suppress("UNCHECKED_CAST")

package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.Ability
import com.jetpack.pokedex.data.model.AbilityBaseDetail
import com.jetpack.pokedex.data.model.ContestType
import com.jetpack.pokedex.data.model.EffectEntry
import com.jetpack.pokedex.data.model.FlavorTextEntry
import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.model.Move
import com.jetpack.pokedex.data.model.MoveBaseDetail
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonBaseDetail
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.model.RegionBaseDetail
import com.jetpack.pokedex.data.model.Stat
import com.jetpack.pokedex.data.model.StatBaseDetail
import com.jetpack.pokedex.data.model.Type
import com.jetpack.pokedex.data.model.TypeBaseDetail
import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.data.model.TypeListResponse
import com.jetpack.pokedex.data.model.VersionGroupDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.mutableListOf
import kotlin.coroutines.resumeWithException

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OkHttpApiService(private val client: OkHttpClient): PokemonApiService, GenerationApiService, MoveApiService, TypeApiService {
    companion object{
        private const val BASE_URL = "https://pokeapi.co/"
        private const val VERSION = "api/v2/"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
        return suspendCancellableCoroutine { continuation ->
            val urlBuilder = (BASE_URL+VERSION+"pokemon").toHttpUrlOrNull()?.newBuilder()
                ?: run {
                    if (continuation.isActive) continuation.resumeWithException(IllegalArgumentException("Invalid base URL for list"))
                    return@suspendCancellableCoroutine
                }
            urlBuilder.addQueryParameter("limit", limit.toString())
            urlBuilder.addQueryParameter("offset", offset.toString())

            val url =  urlBuilder.build().toString()
            val request = Request.Builder().url(url).get().build()
            val call = client.newCall(request)

            continuation.invokeOnCancellation { call.cancel() }

            val pokemonList = mutableListOf<Pokemon>()
            var count = 0
            var next = ""
            var previous = ""
            var resultsArray: JSONArray
            call.enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!continuation.isActive) {
                        response.close(); return
                    }
                    response.use {
                        if (!response.isSuccessful) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("API Error (list): ${it.code} - ${it.message}"))
                            }
                            return
                        }
                        val responseBody = response.body.string()
                        if (false) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("Empty response body for list"))
                            }
                            return
                        }
                        try {
                            val rootObject = JSONObject(responseBody)
                            count = rootObject.getInt("count")
                            next = rootObject.optString("next")
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
                                    species = "",
                                )
                                pokemonList.add(pokemon)
                            }
                            mapPokemonList(pokemonList)
                            val pokemonListResponse = PokemonListResponse(
                                count = count,
                                next = next,
                                previous = previous,
                                results = pokemonList
                            )
                            // --- End Basic JSON Parsing ---
                            continuation.resume(pokemonListResponse, null)

                        } catch (e: Exception) {
                            continuation.resumeWithException(RuntimeException("JSON Parsing Error (list): ${e.localizedMessage}", e))
                        }

                    }
                }
            })
        }
    }

    fun mapPokemonList(pokemonList: List<Pokemon>): List<Pokemon> {
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

                                pokemon.img = sprites
                                for (i in 0 until types.length()){
                                    subtypes.add(types.getJSONObject(i).optJSONObject("type").optString("name"))
                                }
                                for (i in 0 until moves.length()){
                                    val moveName = moves.getJSONObject(i).optJSONObject("move").optString("name")
                                    val moveUrl = moves.getJSONObject(i).optJSONObject("move").optString("url")
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMoveList(limit: Int, offset: Int) : MoveListResponse {
        return suspendCancellableCoroutine { continuation ->
            val urlBuilder = (BASE_URL+VERSION+"move").toHttpUrlOrNull()?.newBuilder()
                ?: run {
                    if (continuation.isActive) continuation.resumeWithException(IllegalArgumentException("Invalid base URL for list"))
                    return@suspendCancellableCoroutine
                }
            urlBuilder.addQueryParameter("limit", limit.toString())
            urlBuilder.addQueryParameter("offset", offset.toString())

            val url =  urlBuilder.build().toString()
            val request = Request.Builder().url(url).get().build()
            val call = client.newCall(request)

            continuation.invokeOnCancellation { call.cancel() }

            call.enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!continuation.isActive) {
                        response.close(); return
                    }
                    response.use {
                        if (!response.isSuccessful) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("API Error (list): ${it.code} - ${it.message}"))
                            }
                            return
                        }
                        val responseBody = response.body.string()
                        if (responseBody.isEmpty()) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("Empty response body for list"))
                            }
                            return
                        }
                        try {
                            val moveList = mutableListOf<MoveDetail>()
                            val rootObject = JSONObject(responseBody)
                            var count = rootObject.getInt("count")
                            var next = rootObject.optString("next")
                            var previous = rootObject.optString("previous")
                            var resultsArray = rootObject.getJSONArray("results")

                            if (resultsArray.length() == 0) {
                                val moveListResponse = MoveListResponse(
                                    count = count,
                                    next = next,
                                    previous = previous,
                                    results = moveList // empty list
                                )
                                continuation.resume(moveListResponse, null)
                                return
                            }

                            for (i in 0 until resultsArray.length()) {
                                val moveJson = resultsArray.getJSONObject(i)
                                val name = moveJson.getString("name")
                                val url = moveJson.getString("url")
                                var moveDetail = MoveDetail(
                                    accuracy = 0,
                                    contestType = ContestType("", ""),
                                    damageClass = "",
                                    effectEntries = emptyList(),
                                    flavorTextEntries = emptyList(),
                                    generation = "",
                                    id = "",
                                    learnedByPokemon = mutableListOf(),
                                    name = name,
                                    pp = 0,
                                    power = 0,
                                    priority = 0,
                                    superContestEffect = "",
                                    target = "",
                                    type = Type("", ""),
                                    url = url
                                )
                                moveList.add(moveDetail)
                            }
                            mapMoveListResponse(moveList)
                            val moveListResponse = MoveListResponse(
                                count = count,
                                next = next,
                                previous = previous,
                                results = moveList
                            )
                            // --- End Basic JSON Parsing ---
                            continuation.resume(moveListResponse, null)

                        } catch (e: Exception) {
                            continuation.resumeWithException(RuntimeException("JSON Parsing Error (list): ${e.localizedMessage}", e))
                        }
                    }
                }
            })
        }
    }

    private fun mapMoveListResponse(moveList: List<MoveDetail>) : List<MoveDetail> {
        if(moveList.isNotEmpty()){
            moveList.map { move ->
                val detailUrlBuilder = move.url.toHttpUrlOrNull()?.newBuilder()
                val url = detailUrlBuilder?.build().toString()
                val request = Request.Builder().url(url).get().build()
                val call = client.newCall(request)

                call.enqueue(object : Callback {
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
                                move.id = rootObject.optString("id")
                                move.pp = rootObject.optInt("pp")
                                move.power = rootObject.optInt("power")
                                move.priority = rootObject.optInt("priority")
                                move.target = rootObject.optJSONObject("target")?.optString("name") ?: ""
                                move.type = Type(
                                    name = rootObject.optJSONObject("type")?.optString("name") ?: "",
                                    url = rootObject.optJSONObject("type")?.optString("url") ?: ""
                                )
                                move.accuracy = rootObject.optInt("accuracy")
                                move.contestType = ContestType(
                                    rootObject.optJSONObject("contest_type")?.optString("name") ?: "",
                                    rootObject.optJSONObject("contest_type")?.optString("url") ?: ""
                                )
                                move.damageClass = rootObject.optJSONObject("damage_class")?.optString("name") ?: ""
                                var effectEntries = rootObject.getJSONArray("effect_entries")
                                var effectEntryList = mutableListOf<EffectEntry>()
                                for (i in 0 until effectEntries.length()) {
                                    var effectEntry = EffectEntry(
                                        effect = effectEntries.getJSONObject(i).optString("effect"),
                                        shortEffect = effectEntries.getJSONObject(i).optString("short_effect")
                                    )
                                    effectEntryList.add(effectEntry)
                                }
                                move.effectEntries = effectEntryList

                                val flavorTextEntries = rootObject.getJSONArray("flavor_text_entries")
                                val flavorTextEntryList = mutableListOf<FlavorTextEntry>()
                                for (i in 0 until flavorTextEntries.length()) {
                                    var flavorTextEntry = FlavorTextEntry(
                                        flavorText = flavorTextEntries.getJSONObject(i).optString("flavor_text"),
                                        language = flavorTextEntries.getJSONObject(i).optJSONObject("language").optString("name"),
                                        versionGroup = VersionGroupDetail(
                                            name = flavorTextEntries.getJSONObject(i).optJSONObject("version_group").optString("name"),
                                            url = flavorTextEntries.getJSONObject(i).optJSONObject("version_group").optString("url")
                                        )
                                    )
                                    flavorTextEntryList.add(flavorTextEntry)
                                }
                                move.flavorTextEntries = flavorTextEntryList
                                move.generation = rootObject.optJSONObject("generation")?.optString("name") ?: ""

                                val learnedByArray = rootObject.optJSONArray("learned_by_pokemon")
                                val tempLearnedBy = mutableListOf<Pokemon>()
                                for (i in 0 until learnedByArray.length()) {
                                    val pokemonJson = learnedByArray.getJSONObject(i)
                                    if (pokemonJson != null) {
                                        tempLearnedBy.add(
                                            Pokemon(
                                                id = "", // You might not get ID here
                                                name = pokemonJson.optString("name"),
                                                url = pokemonJson.optString("url"),
                                                img = "",
                                                types = emptyList(),
                                                height = "",
                                                weight = "",
                                                abilities = emptyList(),
                                                stats = emptyList(),
                                                moves = emptyList(),
                                                species = ""
                                            )
                                        )
                                    }
                                }
                                move.learnedByPokemon = tempLearnedBy
                            } catch (e: Exception) {
                                print("Error parsing response: ${e.localizedMessage}")
                            }
                        }
                    }
                })
            }
        }
        return moveList
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getGenerationList(limit: Int, offset: Int):GenerationListResponse {
        return suspendCancellableCoroutine { continuation ->
            val urlBuilder = (BASE_URL+VERSION+"generation").toHttpUrlOrNull()?.newBuilder()
                ?: run {
                    if(continuation.isActive){
                        continuation.resumeWithException(IllegalArgumentException("Invalid base URL for list"))
                    }
                    return@suspendCancellableCoroutine
                }
            urlBuilder.addQueryParameter("limit", limit.toString())
            urlBuilder.addQueryParameter("offset", offset.toString())

            val url =  urlBuilder.build().toString()
            val request = Request.Builder().url(url).get().build()
            val call = client.newCall(request)

            continuation.invokeOnCancellation { call.cancel() }

            val generationList = mutableListOf< GenerationDetail>()
            var count = 0
            var next = "" // optString returns null if key not found or value is null
            var previous = ""
            var resultsArray: JSONArray
            call.enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!continuation.isActive) {
                        response.close(); return
                    }
                    response.use {
                        if (!response.isSuccessful) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("API Error (list): ${it.code} - ${it.message}"))
                            }
                            return
                        }
                        val responseBody = response.body.string()
                        if (responseBody.isEmpty()) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("Empty response body for list"))
                            }
                            return
                        }
                        try {
                            val rootObject = JSONObject(responseBody)
                            count = rootObject.getInt("count")
                            next = rootObject.optString("next")
                            previous = rootObject.optString("previous")
                            resultsArray = rootObject.getJSONArray("results")

                            for (i in 0 until resultsArray.length()) {
                                val generationJson = resultsArray.getJSONObject(i)
                                val name = generationJson.getString("name")
                                val url = generationJson.getString("url")
                                val generationDetail = GenerationDetail(
                                    id = "",
                                    name = name,
                                    url = url,
                                    mainRegion = RegionBaseDetail("", ""),
                                    moves = emptyList(),
                                    pokemonSpecies = emptyList(),
                                    versionGroups = emptyList(),
                                    types = emptyList()
                                )
                                generationList.add(generationDetail)
                            }
                            mapGenerationList(generationList)
                            val generationListResponse = GenerationListResponse(
                                count = count,
                                next = next,
                                previous = previous,
                                results = generationList
                            )
                            // --- End Basic JSON Parsing ---
                            continuation.resume(generationListResponse, null)

                        } catch (e: Exception) {
                            continuation.resumeWithException(RuntimeException("JSON Parsing Error (list): ${e.localizedMessage}", e))
                        }

                    }
                }
            })
        }
    }

    fun mapGenerationList(generationList: List<GenerationDetail>): List<GenerationDetail> {
        if(generationList.isNotEmpty()){
            generationList.map { generation ->
                val urlBuilder = generation.url.toHttpUrlOrNull()?.newBuilder()
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
                                var id = rootObject.optString("id")
                                var mainRegion = RegionBaseDetail(
                                    name = rootObject.optJSONObject("main_region")?.optString("name") ?: "",
                                    url = rootObject.optJSONObject("main_region")?.optString("url") ?: ""
                                )
                                var subMoves = rootObject.getJSONArray("moves")
                                var subPokemonSpecies = rootObject.getJSONArray("pokemon_species")
                                var subVersionGroups = rootObject.getJSONArray("version_groups")
                                var subTypes = rootObject.getJSONArray("types")

                                generation.id = id
                                generation.mainRegion = mainRegion
                                val listMove = mutableListOf<MoveBaseDetail>()
                                for (i in 0 until subMoves.length()){
                                    var move = subMoves.getJSONObject(i)
                                    var name = move.optString("name")
                                    var url = move.optString("url")
                                    listMove.add(MoveBaseDetail(name, url))
                                }
                                generation.moves = listMove

                                val listPokemonSpecies = mutableListOf<PokemonBaseDetail>()
                                for (i in 0 until subPokemonSpecies.length()){
                                    var pokemon = subPokemonSpecies.getJSONObject(i)
                                    var name = pokemon.optString("name")
                                    var url = pokemon.optString("url")
                                    listPokemonSpecies.add(PokemonBaseDetail(name, url))
                                }
                                generation.pokemonSpecies = listPokemonSpecies

                                val listVersionGroups = mutableListOf<VersionGroupDetail>()
                                for (i in 0 until subVersionGroups.length()){
                                    var versionGroup = subVersionGroups.getJSONObject(i)
                                    var name = versionGroup.optString("name")
                                    var url = versionGroup.optString("url")
                                    listVersionGroups.add(VersionGroupDetail(name, url))
                                }
                                generation.versionGroups = listVersionGroups

                                val listTypes = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until subTypes.length()){
                                    var type = subTypes.getJSONObject(i)
                                    var name = type.optString("name")
                                    var url = type.optString("url")
                                    listTypes.add(TypeBaseDetail(name, url))
                                }
                                generation.types = listTypes

                            } catch (e: Exception) {
                                print("Error parsing response: ${e.localizedMessage}")
                            }
                        }
                    }
                })

            }
        }
        return generationList
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getTypeList(limit: Int, offset: Int): TypeListResponse {
        return suspendCancellableCoroutine { continuation ->
            val urlBuilder = (BASE_URL+VERSION+"type").toHttpUrlOrNull()?.newBuilder()
                ?: run {
                    if(continuation.isActive){
                        continuation.resumeWithException(IllegalArgumentException("Invalid base URL for list"))
                    }
                    return@suspendCancellableCoroutine
                }
            urlBuilder.addQueryParameter("limit", limit.toString())
            urlBuilder.addQueryParameter("offset", offset.toString())

            val url =  urlBuilder.build().toString()
            val request = Request.Builder().url(url).get().build()
            val call = client.newCall(request)

            call.enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!continuation.isActive) {
                        response.close(); return
                    }
                    response.use {
                        if (!response.isSuccessful) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("API Error (list): ${it.code} - ${it.message}"))
                            }
                            return
                        }
                        val responseBody = response.body.string()
                        if (false) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException("Empty response body for list"))
                            }
                            return
                        }
                        try {
                            val typeList = mutableListOf<TypeDetail>()

                            // --- Basic JSON Parsing (Replace with Moshi/Gson/Kotlinx.Serialization in real app) ---
                            val rootObject = JSONObject(responseBody)
                            val count = rootObject.getInt("count")
                            val next = rootObject.optString("next") // optString returns null if key not found or value is null
                            val previous = rootObject.optString("previous")
                            val resultsArray = rootObject.getJSONArray("results")

                            for (i in 0 until resultsArray.length()) {
                                val typeJson = resultsArray.getJSONObject(i)
                                val name = typeJson.getString("name")
                                val url = typeJson.getString("url")
                                val typeDetail = TypeDetail(
                                    id = "",
                                    name = name,
                                    url = url,
                                    pokemon = emptyList(),
                                    moves = emptyList(),
                                    doubleDamageFrom = emptyList(),
                                    doubleDamageTo = emptyList(),
                                    halfDamageFrom = emptyList(),
                                    halfDamageTo = emptyList(),
                                    noDamageFrom = emptyList(),
                                    noDamageTo = emptyList(),
                                    generation = ""
                                )
                                typeList.add(typeDetail)
                            }
                            mapTypeListResponse(typeList)
                            val typeListResponse = TypeListResponse(
                                count = count,
                                next = next,
                                previous = previous,
                                results = typeList
                            )
                            // --- End Basic JSON Parsing ---

                            continuation.resume(typeListResponse, null)

                        } catch (e: Exception) {
                            continuation.resumeWithException(RuntimeException("JSON Parsing Error (list): ${e.localizedMessage}", e))
                        }

                    }
                }
            })
        }
    }

    fun mapTypeListResponse(typeList: List<TypeDetail>): List<TypeDetail> {
        if(typeList.isNotEmpty()){
            typeList.map { typeItem ->
                val urlBuilder = typeItem.url.toHttpUrlOrNull()?.newBuilder()
                val url =  urlBuilder?.build().toString()
                val requestType = Request.Builder().url(url).get().build()
                client.newCall(requestType).enqueue(object: Callback {
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
                                var id = rootObject.optString("id")
                                var generation = rootObject.optJSONObject("generation").optString("name")
                                var moves = rootObject.getJSONArray("moves")
                                var pokemon = rootObject.getJSONArray("pokemon")
                                var doubleDamageFrom = rootObject.optJSONObject("damage_relations").getJSONArray("double_damage_from")
                                var doubleDamageTo = rootObject.optJSONObject("damage_relations").getJSONArray("double_damage_to")
                                var halfDamageFrom = rootObject.optJSONObject("damage_relations").getJSONArray("half_damage_from")
                                var halfDamageTo = rootObject.optJSONObject("damage_relations").getJSONArray("half_damage_to")
                                var noDamageFrom = rootObject.optJSONObject("damage_relations").getJSONArray("no_damage_from")
                                var noDamageTo = rootObject.optJSONObject("damage_relations").getJSONArray("no_damage_to")

                                typeItem.id = id
                                typeItem.generation = generation

                                val listMoves = mutableListOf<MoveBaseDetail>()
                                for (i in 0 until moves.length()){
                                    var move = moves.getJSONObject(i)
                                    var name = move.optString("name")
                                    var url = move.optString("url")
                                    listMoves.add(MoveBaseDetail(name, url))
                                }
                                typeItem.moves = listMoves

                                val listPokemon = mutableListOf<PokemonBaseDetail>()
                                for (i in 0 until pokemon.length()){
                                    var pokemon = pokemon.getJSONObject(i).optJSONObject("pokemon")
                                    var name = pokemon.optString("name")
                                    var url = pokemon.optString("url")
                                    listPokemon.add(PokemonBaseDetail(name, url))
                                }
                                typeItem.pokemon = listPokemon

                                val listDoubleDamageFrom = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until doubleDamageFrom.length()){
                                    var typeEle = doubleDamageFrom.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listDoubleDamageFrom.add(TypeBaseDetail(name, url))
                                }
                                typeItem.doubleDamageFrom = listDoubleDamageFrom

                                val listDoubleDamageTo = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until doubleDamageTo.length()){
                                    var typeEle = doubleDamageTo.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listDoubleDamageTo.add(TypeBaseDetail(name, url))
                                }
                                typeItem.doubleDamageTo = listDoubleDamageTo

                                val listHalfDamageFrom = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until halfDamageFrom.length()){
                                    var typeEle = halfDamageFrom.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listHalfDamageFrom.add(TypeBaseDetail(name, url))
                                }
                                typeItem.halfDamageFrom = listHalfDamageFrom

                                val listHalfDamageTo = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until halfDamageTo.length()){
                                    var typeEle = halfDamageTo.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listHalfDamageTo.add(TypeBaseDetail(name, url))
                                }
                                typeItem.halfDamageTo = listHalfDamageTo

                                val listNoDamageFrom = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until noDamageFrom.length()){
                                    var typeEle = noDamageFrom.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listNoDamageFrom.add(TypeBaseDetail(name, url))
                                }
                                typeItem.noDamageFrom = listNoDamageFrom

                                val listNoDamageTo = mutableListOf<TypeBaseDetail>()
                                for (i in 0 until noDamageTo.length()){
                                    var typeEle = noDamageTo.getJSONObject(i)
                                    var name = typeEle.optString("name")
                                    var url = typeEle.optString("url")
                                    listNoDamageTo.add(TypeBaseDetail(name, url))
                                }
                                typeItem.noDamageTo = listNoDamageTo

                            } catch (e: Exception) {
                                print("Error parsing response: ${e.localizedMessage}")
                            }
                        }
                    }
                })

            }
        }
        return typeList
    }
}
