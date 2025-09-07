package com.jetpack.pokedex.data.model

data class GenerationListResponse(
    override val count: Int,
    override val next: String,
    override val previous: String,
    val results: List<GenerationDetail>
): BaseObjectListResponse(count, next, previous)

data class GenerationDetail (
    var id: String,
    override val name: String,
    override val url: String,
    var mainRegion: RegionBaseDetail,
    var moves: List<MoveBaseDetail>,
    var pokemonSpecies: List<PokemonBaseDetail>,
    var versionGroups: List<VersionGroupDetail>,
    var types: List<TypeBaseDetail>
): BaseObjectDetail(name, url)