package com.jetpack.pokedex.data.model

data class Pokemon(
    var id: String,
    val name: String,
    val url: String,
    var img: String,
    var types: List<String>,
    var height: String,
    var weight: String,
    var abilities: List<Ability>,
    var stats: List<Stat>,
    var moves: List<Move>,
//    val evolutions: List<PokemonEvolution>,
    var species: String,
)

data class MoveBaseDetail(
    var name: String,
    var url: String
)

data class Move(
    val move: MoveBaseDetail,
)

data class Ability(
    var ability: AbilityBaseDetail,
    var isHidden: Boolean,
    var slot: Int
)

data class AbilityBaseDetail(
    val name: String,
    val url: String
)

data class Stat(
    val baseStat: Int,
    val effort: Int,
    val stat: StatBaseDetail
)

data class StatBaseDetail(
    val name: String,
    val url: String
)

data class PokemonListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<Pokemon>
)