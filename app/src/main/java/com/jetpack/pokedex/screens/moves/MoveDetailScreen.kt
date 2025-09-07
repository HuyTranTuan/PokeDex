package com.jetpack.pokedex.screens.moves

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.screens.home.PokemonListItemView
import com.jetpack.pokedex.screens.home.getTypeColor
import com.jetpack.pokedex.sidecomponents.BackwardButton
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MoveDetailScreen(
    moveName: String,
    navController: NavController,
    moveViewModel: MoveViewModel,
    pokemonViewModel: PokemonViewModel
) {
    var moveDetail by remember { mutableStateOf<MoveDetail?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(moveName) {
        isLoading = true
        errorMessage = null
        try {
            moveDetail = moveViewModel.getMoveDetailByName(moveName)
        } catch (e: Exception) {
            errorMessage = "Failed to load details. Error${e.message}"
        } finally {
            isLoading = false
        }
    }

    val lazyListState = rememberLazyListState() // State for LazyColumn
    val coroutineScope = rememberCoroutineScope()

    val showButton by remember { // Determine if the Scroll to Top button should be shown
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (moveDetail != null) {
                val detail = moveDetail!!

                // Move Name
                item {
                    var moveBaseName = detail.name.split("-")
                    var moveName = moveBaseName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    for (i in 1 until moveBaseName.size){
                        moveName += " ${moveBaseName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                    }
                    Text(
                        moveName,
                        style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(10.dp)
                    )
                }

                // Move Base info
                item {
                    Row(
                        modifier = Modifier.width(maxOf(250.dp, 370.dp)),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp,
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(text = "Power: ${detail.power}", modifier = Modifier.padding(5.dp))
                        Text("Accuracy: ${detail.accuracy}", modifier = Modifier.padding(5.dp))
                        Text("PP: ${detail.pp}", modifier = Modifier.padding(5.dp))
                    }
                }

                item {
                    Row(
                        modifier = Modifier.width(maxOf(250.dp, 370.dp)),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp,
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        var moveTargetBaseName = detail.target.split("-")
                        var moveTarget = ""
                        for (i in moveTargetBaseName){
                            moveTarget += " ${i.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                        }
                        Text(text = "Target: $moveTarget")
                        Text("Damage Class: ${detail.damageClass.replaceFirstChar { if(it.isLowerCase()) it.titlecase() else it.toString() }}")
                    }
                }

                // Move Generation
                item {
                    Row(
                        modifier = Modifier.width(maxOf(250.dp, 370.dp)),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp,
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(
                            text = "Generation: " + getGenerationName(detail.generation),
                            modifier = Modifier.padding(5.dp)
                        )
                        Box (
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = getTypeColor(detail.type.name))
                        ){
                            val typeName = detail.type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            Text(
                                text="Type: $typeName",
                                modifier = Modifier.padding(5.dp),
                                color = if(detail.type.name != "shadow") Color.Black else Color.White
                            )
                        }
                    }
                }

                if (detail.effectEntries.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .width(maxOf(250.dp, 370.dp))
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                20.dp,
                                Alignment.CenterHorizontally
                            ),
                        ) {
                            Text(text ="Effect Entries:", modifier = Modifier.wrapContentWidth())
                            Text(
                                text = "Effect: "+detail.effectEntries[0].effect,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Flavor Text Entries
                if (detail.flavorTextEntries.isNotEmpty()) {
                    item {
                        Text(
                            "Flavor Text Entries:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    var flavorTextEntries = detail.flavorTextEntries.filter { it.language == "en" }
                    items(flavorTextEntries.size) { index ->
                        val flavorText = flavorTextEntries[index]
                        Text(
                            text = "Detail: "+ flavorText.flavorText.replace("\n", " "),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        val versionName = flavorText.versionGroup.name.split("-")
                        var flavorTextVersion = versionName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        for (i in 1 until versionName.size){
                            flavorTextVersion += " ${versionName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                        }
                        Text(
                            text = "Version: $flavorTextVersion",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Learned By Pokemon - The List/Grid
                item {
                    Text(
                        text = "Learned By Pokemon:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    val pokeList = mutableListOf<Pokemon>()
                    for (i in detail.learnedByPokemon){
                        val pokemon = pokemonViewModel.getPokemonDetailByName(i.name.trim())
                        pokeList.add(Pokemon(
                            id = pokemon?.id ?: "N/A",
                            name = pokemon?.name?: i.name,
                            url = pokemon?.url ?: i.url,
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
                    val isLoading by pokemonViewModel.isLoading.observeAsState(initial = false)
                    val canLoadMore by pokemonViewModel.isLoading.observeAsState(initial = false)
                    var listHeight = 300
                    if (pokeList.isEmpty()) {
                        listHeight = 150
                    }
                    if(pokeList.size > 2){
                        listHeight = 600
                    }
                    if (pokeList.size in 1..2){
                        listHeight = 300
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(listHeight.dp)
                    ){
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
                                    text = "No Pokemon Learned",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            MovePokemonLazyList(
                                pokemonList = pokeList,
                                isLoadingMore = isLoading,
                                canLoadMore = canLoadMore,
                                onLoadMore = { pokeList },
                                navController = navController
                            )

                        }
                    }
                }
            }
        }
    }
    AnimatedVisibility(visible = showButton, enter = fadeIn(), exit = fadeOut()) {
        ScrollToTopButton(
            goToTop = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            },
            isVisibleStart = false,
        )
    }

    BackwardButton(navController = navController)

}

fun getGenerationName(generation: String): String{
    return when(generation){
        "generation-i" -> "I"
        "generation-ii" -> "II"
        "generation-iii" -> "III"
        "generation-iv" -> "IV"
        "generation-v" -> "V"
        "generation-vi" -> "VI"
        "generation-vii" -> "VII"
        "generation-viii" -> "VIII"
        "generation-ix" -> "IX"
        else -> "Unknown"
    }
}

@Composable
fun MovePokemonLazyList(
    pokemonList: List<Pokemon>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    navController: NavController
) {
    val state = rememberLazyGridState()
    val showButton by remember {
        derivedStateOf {
            state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(top = 4.dp, bottom = 50.dp),
    ) {
        items(
            pokemonList,
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
    AnimatedVisibility(visible = showButton, enter = fadeIn(), exit = fadeOut()) {
        ScrollToTopButton(
            goToTop = {
                coroutineScope.launch {
                    state.animateScrollToItem(index = 0)
                }
            },
            isVisibleStart = true,
        )
    }

    // Pagination: Trigger load more when the user scrolls near the end
    LaunchedEffect(state, canLoadMore) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= pokemonList.size - 5
            }
            .distinctUntilChanged()
            .filter { it && canLoadMore && !isLoadingMore }
            .collect {
                onLoadMore()
            }
    }
}
