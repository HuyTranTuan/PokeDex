package com.jetpack.pokedex.data.model

data class TypeListResponse(
    override val count: Int,
    override val next: String,
    override val previous: String,
    val results: List<TypeDetail>
) : BaseObjectListResponse(count, next, previous)

data class TypeDetail(
    var id: String,
    override val url: String,
    override val name: String,
    var generation: String,
    var doubleDamageFrom: List<TypeBaseDetail>,
    var doubleDamageTo: List<TypeBaseDetail>,
    var halfDamageFrom: List<TypeBaseDetail>,
    var halfDamageTo: List<TypeBaseDetail>,
    var noDamageFrom: List<TypeBaseDetail>,
    var noDamageTo: List<TypeBaseDetail>,
    var moves: List<MoveBaseDetail>,
    var pokemon: List<PokemonBaseDetail>
) : BaseObjectDetail(name, url)