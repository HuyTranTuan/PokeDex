package com.jetpack.pokedex.data.model

data class GenerationListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<GenerationDetail>
)

data class GenerationDetail(
    var id: String,
    val name: String,
    val url: String,
    var mainRegion: RegionBaseDetail,
    var moves: List<MoveBaseDetail>,
    var pokemonSpecies: List<PokemonBaseDetail>,
    var versionGroups: List<VersionGroupDetail>,
    var types: List<TypeBaseDetail>
)

data class RegionBaseDetail(
    val name: String,
    val url: String
)

data class TypeBaseDetail(
    val name: String,
    val url: String
)

data class PokemonBaseDetail(
    val name: String,
    val url: String
)