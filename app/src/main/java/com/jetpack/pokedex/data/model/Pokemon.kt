package com.jetpack.pokedex.data.model

data class Pokemon(
    var id: String,
    override val name: String,
    override val url: String,
    var img: String,
    var types: List<String>,
    var height: String,
    var weight: String,
    var abilities: List<Ability>,
    var stats: List<Stat>,
    var moves: List<Move>,
//    val evolutions: List<PokemonEvolution>,
    var species: String,
) : BaseObjectDetail(name, url)

data class PokemonListResponse(
    override val count: Int,
    override val next: String,
    override val previous: String,
    val results: List<Pokemon>
) : BaseObjectListResponse(count, next, previous)