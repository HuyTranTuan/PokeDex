package com.jetpack.pokedex.data.model

data class MoveDetail(
    var id: String,
    val url: String,
    var type: Type,
    val name: String,
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
)

data class MoveListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<MoveDetail>
)

data class EffectEntry(
    val effect: String,
    val shortEffect: String
)

data class FlavorTextEntry(
    val flavorText: String,
    val language: String,
    val versionGroup: VersionGroupDetail
)

data class VersionGroupDetail(
    val name: String,
    val url: String
)

data class Type(
    val name: String,
    val url: String
)

data class ContestType(
    val name: String,
    val url: String
)
