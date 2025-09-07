package com.jetpack.pokedex.data.model

data class MoveDetail(
    var id: String,
    override val url: String,
    override val name: String,
    var type: Type,
    var accuracy: Int,
    var power: Int,
    var target: String,
    var pp: Int,
    var contestType: ContestType, // Cool
    var damageClass: String, // special
    var effectEntries: List<EffectEntry>,
    var flavorTextEntries: List<FlavorTextEntry>,
    var generation: String,
    var learnedByPokemon: MutableList<Pokemon>,
    var priority: Int,
    var superContestEffect: String,
): BaseObjectDetail(name, url)

data class MoveListResponse(
    override val count: Int,
    override val next: String,
    override val previous: String,
    val results: List<MoveDetail>
) : BaseObjectListResponse(count, next, previous)


