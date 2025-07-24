@file:Suppress("KotlinConstantConditions", "DEPRECATION")

package com.jetpack.pokedex.pages

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.R
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.ui.theme.*
import com.jetpack.pokedex.viewmodel.PokemonViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Suppress("UNCHECKED_CAST")
@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    navController: NavController
) {
    val pokemonList by viewModel.pokemonList.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val errorMessage by viewModel.errorMessage.observeAsState(initial = null)
    val canLoadMore by viewModel.isLoading.observeAsState(initial = false)

    val gridHeight = getAvailableSurfaceHeight(LocalContext.current)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (pokemonList.isEmpty() && isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (pokemonList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: $errorMessage\nPlease try again.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchPokemon() }) {
                        Text("Retry")
                    }
                }
            } else {
                PokemonLazyList(
                    pokemonList = pokemonList,
                    isLoadingMore = isLoading && pokemonList.isEmpty(), // Show loader at bottom only if loading more
                    canLoadMore = canLoadMore,
                    onLoadMore = {
                        viewModel.fetchPokemon(loadMore = true)
                    },
                    navController = navController
                )

            }
        }
    }
}


@Composable
fun PokemonLazyList(
    pokemonList: List<Pokemon>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    navController: NavController
) {
    val state = rememberLazyGridState()
    //Here we create a condition if the firstVisibleItemIndex is greater than 0
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
            .background(color = Color.White)
            .padding(top = 110.dp, bottom = 100.dp),
    ) {
        items(
            pokemonList,
            key = { pokemon -> pokemon.name }
        ){ pokemon ->
            PokemonListItemView(
                pokemon = pokemon,
                onPokemonClick = { pokemonId ->
                    navController.navigate("${AppDestinations.POKEMON_DETAIL_ROUTE}/$pokemonId")
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
            }
        )
    }

    // Pagination: Trigger load more when the user scrolls near the end
    LaunchedEffect(state, canLoadMore) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= pokemonList.size - 5 // Load 5 items before end
            }
            .distinctUntilChanged()
            .filter { it && canLoadMore && !isLoadingMore } // Only if true, can load more, and not already loading
            .collect {
                onLoadMore()
            }
    }
}

@Composable
fun PokemonListItemView(pokemon: Pokemon, onPokemonClick: (String) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPokemonClick(pokemon.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MaterialTheme {
                NetworkImage(
                    url = pokemon.img,
                    contentDescription= pokemon.name + "\n Pokémon height:" + pokemon.height + "\n Pokémon weight:" + pokemon.weight,
                    modifier = Modifier.size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, // Capitalize
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Loop through pokemon.types and render each type name
            Box{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (pokemon.types.isNotEmpty()) {
                        pokemon.types.forEach { type ->
                            val color = getTypeColor(type)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(5.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Type: N/A",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true) // For a smooth fade-in effect
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop, // How the image should be scaled
    )
}

fun getTypeColor(type: String): Color{
    var color = Color.Transparent
    when(type){
        "normal" -> color = normal
        "fire" -> color = fire
        "water" -> color = water
        "electric" -> color = electric
        "grass" -> color = grass
        "ice" -> color = ice
        "fighting" -> color = fighting
        "poison" -> color = poison
        "ground" -> color = ground
        "flying" -> color = flying
        "psychic" -> color = psychic
        "bug" -> color = bug
        "rock" -> color = rock
        "ghost" -> color = ghost
        "dragon" -> color = dragon
        "dark" -> color = dark
        "steel" -> color = steel
        "fairy" -> color = fairy
    }
    return color
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun getAvailableSurfaceHeight(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
        val navigationBarHeight: Int = windowMetrics.windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
        val statusBarHeight: Int = windowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val screenHeight: Int = windowMetrics.bounds.height()

        screenHeight - navigationBarHeight - statusBarHeight
    } else {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenHeight = size.y

        // Older versions might include navigation bar height in screen height.
        // Consider this when using deprecated methods.
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }

        // Check if the navigation bar is visible and subtract its height
        var navigationBarHeight = 0
        val navBarResourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (navBarResourceId > 0) {
            navigationBarHeight = context.resources.getDimensionPixelSize(navBarResourceId)
        }

        return  screenHeight - statusBarHeight - navigationBarHeight
    }
}