package com.jetpack.pokedex

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.pages.GenerationsScreen
import com.jetpack.pokedex.pages.HomeScreen
import com.jetpack.pokedex.pages.MovesScreen
import com.jetpack.pokedex.ui.theme.Crimson
import com.jetpack.pokedex.viewmodel.PokemonViewModel
import com.jetpack.pokedex.pages.PokemonDetailScreen
import com.jetpack.pokedex.pages.TypesScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: PokemonViewModel) {
    val pokemonList by viewModel.pokemonList.observeAsState(initial = emptyList())
    val navController = rememberNavController()

    //Navigation bar
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = listOf("Pokemon", "Generations", "Types", "Moves")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Star, Icons.Filled.Info)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.Star, Icons.Outlined.Info)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .semantics { isTraversalGroup = true }
                    .semantics { traversalIndex = 1f },
                containerColor = Crimson,
            ){
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selectedIndex == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = item,
                                tint = if (selectedIndex == index) Color.Black else Color.LightGray
                            )
                        },
                        label = { Text(item, color = Color.White) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                    )
                }
            }
        },
        topBar = {
            TopBarController(pokemonList, navController)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = AppDestinations.POKEMON_LIST_ROUTE
        ) {
            composable(route = AppDestinations.POKEMON_LIST_ROUTE) {
                HomeScreen(viewModel = viewModel, navController = navController)
            }

            composable(
                route = "${AppDestinations.POKEMON_DETAIL_ROUTE}/{${AppDestinations.POKEMON_NAME_ARG}}",
                arguments = listOf(navArgument(AppDestinations.POKEMON_NAME_ARG) { type =
                    NavType.StringType })
            ) { backStackEntry ->
                // Retrieve the argument
                val pokemonName = backStackEntry.arguments?.getString(AppDestinations.POKEMON_NAME_ARG)
                if (pokemonName != null) {
                    PokemonDetailScreen(
                        pokemonName = pokemonName,
                        navController = navController,
                        pokemonViewModel = viewModel
                    )
                } else {
                    // Handle error: Pokemon ID not found, maybe navigate back or show error
                    Text("Error: Pokémon not found.")
                }
            }
        }
        ContentScreen(viewModel, navController, selectedIndex)
    }
}

@Composable
fun NavController(){
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Pokemon", "Generations", "Types", "Moves")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Star, Icons.Filled.Info)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.Star, Icons.Outlined.Info)
    NavigationBar(
        modifier = Modifier
            .semantics { isTraversalGroup = true }
            .semantics { traversalIndex = 1f },
        containerColor = Crimson,
    ){
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                        tint = if (selectedItem == index) Color.Black else Color.LightGray
                    )
                },
                label = { Text(item, color = Color.White) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
            )
        }
    }
}

@Composable
fun TopBarController(pokemonList: List<Pokemon>, navController: NavController){
    // Current query text
    var query by rememberSaveable { mutableStateOf("") }
    // Search results (replace with your actual search logic)
    var searchResults by remember { mutableStateOf(emptyList<String>()) }
    val allPossibleItems = pokemonList.map { pokemon ->
        pokemon.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase()
            else it.toString()
        }
    }
    // Function to perform the search (triggered by onSearch)
    val performSearch = { currentQuery: String ->
        searchResults = if (currentQuery.isBlank()) {
            emptyList()
        } else {
            allPossibleItems.filter {
                it.contains(currentQuery, ignoreCase = true)
            }
        }
    }
    CustomizableSearchBar(
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            if (newQuery.isNotBlank()) { // Example of live search
                performSearch(newQuery)
            } else {
                searchResults = emptyList()
            }
        },
        onSearch = { searchQuery ->
            performSearch(searchQuery)
        },
        searchResults = searchResults,
        onResultClick = { result ->
            query = result
            searchResults = emptyList()
            var currentPokemon = result.lowercase()
            navController.navigate("${AppDestinations.POKEMON_DETAIL_ROUTE}/${currentPokemon}")
        },
        // --- Optional Customizations ---
        placeholder = { Text("Search Pokémon...") },
        leadingIcon = {
            // Example: Show back arrow if query is not empty, otherwise search icon
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    query = ""
                    searchResults = emptyList()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Clear query")
                }
            } else {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    query = ""
                    searchResults = emptyList()
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear query")
                }
            }
        },
        // supportingContent = { resultText -> /* Optional: Add more details to each result item */ },
        // leadingContent = { /* Optional: Add a common leading element to result items */ },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
    query: String = "",
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    onResultClick: (String) -> Unit,
    // Customization options
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Search, contentDescription = "Search") },
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingContent: (@Composable (String) -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .background(Crimson)
            .padding(bottom = 16.dp)
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Show search results in a lazy column for better performance
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val resultText = searchResults[index]
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = supportingContent?.let { { it(resultText) } },
                        leadingContent = leadingContent,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                onResultClick(resultText)
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContentScreen(
    viewModel: PokemonViewModel,
    navController: NavController,
    selectedIndex: Int
) {
    when(selectedIndex){
        0 -> HomeScreen(viewModel = viewModel, navController)
        1 -> GenerationsScreen()
        2 -> TypesScreen()
        3 -> MovesScreen()
    }
}
