package com.jetpack.pokedex.screens.generations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.screens.home.PokemonListItemView
import com.jetpack.pokedex.screens.home.getTypeColor
import com.jetpack.pokedex.screens.moves.getGenerationName
import com.jetpack.pokedex.sidecomponents.BackwardButton
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.ui.theme.DarkGrey
import com.jetpack.pokedex.ui.theme.LightGrey
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModel
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import kotlinx.coroutines.launch

@Composable
fun GenerationDetailScreen(
    generationName: String,
    navController: NavController,
    generationViewModel: GenerationViewModel,
    pokemonViewModel: PokemonViewModel,
    moveViewModel: MoveViewModel
) {
    val generationList = generationViewModel.generationList.observeAsState(initial = emptyList()).value

    val state = rememberScrollState()
    val showButton by remember {
        derivedStateOf {
            state.value > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(state),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Title
        val generation: GenerationDetail = generationList.find { it.name == generationName }!!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Generation ${getGenerationName(generationName)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Generation
        Row (
            modifier = Modifier.width(maxOf(200.dp, 370.dp)),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.fillMaxWidth()) {
                Row (modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Main region is ",
                        color = DarkGrey,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    Text(
                        text = generation.mainRegion.name.replaceFirstChar { if(it.isLowerCase()) it.titlecase() else it.toString() },
                        fontWeight = FontWeight.Bold,
                        color = DarkGrey,
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp)
//                            .clickable { navController.navigate(route = "${AppDestinations.REGION_DETAIL_ROUTE}/${i.mainRegion.name}")}
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
        }

        // Version Groups
        if (generation.versionGroups.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .width(maxOf(200.dp, 370.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column (modifier = Modifier.width(maxOf(70.dp, 80.dp))) {
                    Text(text = "Versions")
                }
                Column (modifier = Modifier.fillMaxWidth()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        generation.versionGroups.forEach { version ->
                            var versionBaseName = version.name.split("-")
                            var versionName = versionBaseName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            for (i in 1 until versionBaseName.size){
                                versionName += " ${versionBaseName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .clickable {
//                                            navController.navigate("${AppDestinations.MOVE_DETAIL_ROUTE}/${version.name}")
                                    },
                                contentAlignment = Alignment.Center,
                            ){
                                Text(
                                    text = versionName,
                                    fontSize = 12.sp,
                                    color = DarkGrey,
                                    modifier = Modifier
                                        .padding(vertical = 3.dp, horizontal = 5.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Type: N/A",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Types
        if (generation.types.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .width(maxOf(200.dp, 370.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column (modifier = Modifier.width(maxOf(70.dp, 80.dp))) {
                    Text(text = "Types")
                }
                Column (modifier = Modifier.fillMaxWidth()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        generation.types.forEach { type ->
                            var typeName = type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(getTypeColor(type.name))
                                    .clickable {
                                        navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${type.name}")
                                    },
                                contentAlignment = Alignment.Center,
                            ){
                                Text(
                                    text = typeName,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(vertical = 3.dp, horizontal = 5.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .width(maxOf(200.dp, 370.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column (modifier = Modifier.width(maxOf(70.dp, 80.dp))) {
                    Text(text = "Types")
                }
                Column (modifier = Modifier.fillMaxWidth()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp)),
                            contentAlignment = Alignment.Center,
                        ){
                            Text(
                                text = "No new type",
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(vertical = 3.dp, horizontal = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Move List
        if (generation.moves.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .width(maxOf(200.dp, 370.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column (modifier = Modifier.width(maxOf(70.dp, 80.dp))) {
                    Text(text = "Moves")
                }
                Column (modifier = Modifier.fillMaxWidth()) {
                    val moveList = mutableListOf<MoveDetail>()
                    for (m in generation.moves){
                        val move = moveViewModel.getMoveDetailByName(m.name)
                        if (move != null) {
                            moveList.add(move)
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        moveList.forEach { move ->
                            var moveBaseName = move.name.split("-")
                            var moveName = moveBaseName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            for (i in 1 until moveBaseName.size){
                                moveName += " ${moveBaseName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(color = LightGrey)
                                    .clickable {
                                        navController.navigate("${AppDestinations.MOVE_DETAIL_ROUTE}/${move.name}")
                                    },
                                contentAlignment = Alignment.Center,
                            ){
                                Text(
                                    text = moveName,
                                    fontSize = 12.sp,
                                    color = DarkGrey,
                                    modifier = Modifier
                                        .padding(vertical = 3.dp, horizontal = 5.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Type: N/A",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Starter List
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp)) {
            Text(text = "Starter of this generation: ")
        }
        Row( modifier = Modifier
            .fillMaxWidth()
            .height(240.dp) ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(13.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.background(color = Color.Transparent),
            ) {
                val pokeList = mutableListOf<Pokemon>()
                for (p in 0 until 4){
                    if(generation.pokemonSpecies.isNotEmpty()){
                        val pokemonCard = pokemonViewModel.getPokemonDetailByName(generation.pokemonSpecies[p].name)
                        if (pokemonCard?.types?.isNotEmpty() == true) {
                            if (pokemonCard.types[0] =="grass" || pokemonCard.types[0] =="fire" || pokemonCard.types[0] =="water"){
                                pokeList.add(pokemonCard)
                            }
                        }
                    }
                }
                items(
                    pokeList,
                    key = { pokemon -> pokemon.name }
                ){ pokemon ->
                    PokemonListItemView(
                        pokemon = pokemon,
                        onPokemonClick = { pokemonName ->
                            navController.navigate("${AppDestinations.POKEMON_DETAIL_ROUTE}/$pokemonName")
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

    }

    AnimatedVisibility(visible = showButton, enter = fadeIn(), exit = fadeOut()) {
        ScrollToTopButton(
            goToTop = {
                coroutineScope.launch {
                    state.animateScrollTo(0)
                }
            },
            isVisibleStart = false,
        )
    }

    BackwardButton(navController = navController)
}