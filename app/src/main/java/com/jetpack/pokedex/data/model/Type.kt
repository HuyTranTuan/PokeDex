package com.jetpack.pokedex.data.model

data class TypeListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<TypeDetail>
)

data class TypeDetail(
    var id: String,
    val url: String,
    val name: String,
    var generation: String,
    var doubleDamageFrom: List<TypeBaseDetail>,
    var doubleDamageTo: List<TypeBaseDetail>,
    var halfDamageFrom: List<TypeBaseDetail>,
    var halfDamageTo: List<TypeBaseDetail>,
    var noDamageFrom: List<TypeBaseDetail>,
    var noDamageTo: List<TypeBaseDetail>,
    var moves: List<MoveBaseDetail>,
    var pokemon: List<PokemonBaseDetail>
)