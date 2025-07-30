@file:Suppress("UNCHECKED_CAST")

package com.jetpack.pokedex.pages.types

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.pages.home.getTypeColor
import com.jetpack.pokedex.pages.moves.MovePokemonLazyList
import com.jetpack.pokedex.pages.moves.getGenerationName
import com.jetpack.pokedex.sidecomponents.BackwardButton
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.ui.theme.LightGrey
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import com.jetpack.pokedex.viewmodel.type.TypeViewModel
import kotlinx.coroutines.launch

@Composable
fun TypeDetailScreen(
    typeName: String,
    navController: NavController,
    typeViewModel: TypeViewModel,
    pokemonViewModel: PokemonViewModel
) {
    var typeDetail by remember { mutableStateOf<TypeDetail?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(typeName) {
        isLoading = true
        errorMessage = null
        try {
            typeDetail = typeViewModel.getTypeDetailByName(typeName)
        } catch (e: Exception) {
            errorMessage = "Failed to load details. Error${e.message}"
        } finally {
            isLoading = false
        }
    }

    val scrollState = rememberScrollState() // State for the scrollable Column
    val coroutineScope = rememberCoroutineScope()

    val showButton by remember { // Determine if the Scroll to Top button should be shown
        derivedStateOf {
            scrollState.value > 0
        }
    }
    Column (
        modifier = Modifier.fillMaxSize().background(Color.White).padding(top = 100.dp, bottom = 105.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.padding(20.dp))

        // Title
        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(getTypeColor(typeName))){
            Text(
                text = "Type: ${typeName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        // Generation
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 8.dp)) {
            Text(
                text = "First appeared in Generation ${getGenerationName(typeDetail?.generation.toString())}",
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }

        // Double Damage From
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "Double Damage From", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.doubleDamageFrom?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(200.dp, 370.dp)),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val doubleDamageFrom = typeDetail!!.doubleDamageFrom
                doubleDamageFrom.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = typeName,modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp))
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { Text( text = "No type", modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Double Damage To
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "Double Damage To", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.doubleDamageTo?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val doubleDamageTo = typeDetail!!.doubleDamageTo
                doubleDamageTo.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text( text = typeName,modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp) )
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {Text(text = "No type",modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp))}
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Half Damage From
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text(text = "Half Damage From", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.halfDamageFrom?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val halfDamageFrom = typeDetail!!.halfDamageFrom
                halfDamageFrom.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = typeName,modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp))
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {Text(text = "No type",modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp))}
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Half Damage To
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "Half Damage To", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.halfDamageTo?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val halfDamageTo = typeDetail!!.halfDamageTo
                halfDamageTo.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) { Text(text = typeName, modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),) }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { Text(text = "No type", modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // No Damage From
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "No Damage From", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.noDamageFrom?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val noDamageFrom = typeDetail!!.noDamageFrom
                noDamageFrom.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) { Text(text = typeName, modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),) }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { Text(text = "No type", modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),) }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // No Damage To
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "No Damage To", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.noDamageTo?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between items on the same line
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between lines
            ) {
                val noDamageTo = typeDetail!!.noDamageTo
                noDamageTo.forEach { type ->
                    var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = getTypeColor(type.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) { Text(text = typeName,modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between items on the same line
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between lines
            ) { Text(text = "No type",modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Moves
        Row (modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 2.dp)) {
            Text( text = "Moves", fontSize = 14.sp, color = Color.DarkGray )
        }
        if (typeDetail?.moves?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val moves = typeDetail!!.moves
                moves.forEach { move ->
                    var moveName = move.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = LightGrey)
                            .clickable {
                                navController.navigate("${AppDestinations.MOVE_DETAIL_ROUTE}/${move.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ) { Text(text = moveName, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.width(maxOf(250.dp, 370.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { Text(text = "No move in type", modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),) }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Pokemon in type
        Row (
            modifier = Modifier.width(maxOf(200.dp, 370.dp)).padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.Start
        ){Text( text = "Pokemon in type: ", fontSize = 14.sp)}
        val pokeList = mutableListOf<Pokemon?>()
        typeDetail?.let {
            for (pokemonInfo in it.pokemon){
                val pokemon = pokemonViewModel.getPokemonDetailByName(pokemonInfo.name.trim())
                pokeList.add(Pokemon(
                    id = pokemon?.id ?: "N/A",
                    name = pokemon?.name?: pokemonInfo.name,
                    url = pokemon?.url ?: pokemonInfo.url,
                    img = pokemon?.img ?: "N/A",
                    types = pokemon?.types ?: emptyList(),
                    height = pokemon?.height ?: "0",
                    weight = pokemon?.weight ?: "0",
                    abilities = pokemon?.abilities ?: emptyList(),
                    stats = pokemon?.stats ?: emptyList(),
                    moves = pokemon?.moves ?: emptyList(),
                    species = pokemon?.species ?: "N/A"
                ))
            }
        }
        val isLoading by pokemonViewModel.isLoading.observeAsState(initial = false)
        val canLoadMore by pokemonViewModel.isLoading.observeAsState(initial = false)
        Box( modifier = Modifier.fillMaxWidth().height(600.dp)){
            if (pokeList.isEmpty() && isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (pokeList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Pokemon in type",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                MovePokemonLazyList(
                    pokemonList = pokeList as List<Pokemon>,
                    isLoadingMore = isLoading,
                    canLoadMore = canLoadMore,
                    onLoadMore = { pokeList },
                    navController = navController
                )

            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }

    AnimatedVisibility(visible = showButton, enter = fadeIn(), exit = fadeOut()) {
        ScrollToTopButton(
            goToTop = {
                coroutineScope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            isVisibleStart = false,
        )
    }

    BackwardButton(navController = navController)
}