package com.jetpack.pokedex.data.model

open class BaseObjectListResponse(
    open val count: Int,
    open val next: String,
    open val previous: String,
)

open class BaseObjectDetail(
    open val name: String,
    open val url: String,
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
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class Type(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class ContestType(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class MoveBaseDetail(
    override var name: String,
    override var url: String
) : BaseObjectDetail(name, url)

data class Move(
    val move: MoveBaseDetail,
)

data class Ability(
    var ability: AbilityBaseDetail,
    var isHidden: Boolean,
    var slot: Int
)

data class AbilityBaseDetail(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class Stat(
    val baseStat: Int,
    val effort: Int,
    val stat: StatBaseDetail
)

data class StatBaseDetail(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)
data class RegionBaseDetail(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class TypeBaseDetail(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)

data class PokemonBaseDetail(
    override val name: String,
    override val url: String
) : BaseObjectDetail(name, url)
