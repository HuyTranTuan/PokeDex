package com.jetpack.pokedex

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jetpack.pokedex.navhost.GenerationSectionNavHost
import com.jetpack.pokedex.navhost.MovesSectionNavHost
import com.jetpack.pokedex.navhost.PokemonSectionNavHost
import com.jetpack.pokedex.navhost.TypesSectionNavHost
import com.jetpack.pokedex.ui.theme.Crimson
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModel
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import com.jetpack.pokedex.viewmodel.type.TypeViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PokemonHostSectionHostFragment(
    val pokemonViewModel: PokemonViewModel,
    val moveViewModel: MoveViewModel,
    val typeViewModel: TypeViewModel,
    val generationViewModel: GenerationViewModel,
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = ComposeView(requireContext()).apply {
        // Dispose of the Composition when the Fragment's View (this ComposeView) is destroyed
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PokemonSectionNavHost(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
        }
    }
}

class GenerationsSectionHostFragment(
    private val pokemonViewModel: PokemonViewModel,
    val moveViewModel: MoveViewModel,
    val typeViewModel: TypeViewModel,
    val generationViewModel: GenerationViewModel,
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            GenerationSectionNavHost(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
        }
    }
}

class MovesSectionHostFragment(
    val pokemonViewModel: PokemonViewModel,
    val moveViewModel: MoveViewModel,
    val typeViewModel: TypeViewModel,
    val generationViewModel: GenerationViewModel,
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovesSectionNavHost(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            }
        }
}

class TypesSectionHostFragment(
    val pokemonViewModel: PokemonViewModel,
    val moveViewModel: MoveViewModel,
    val typeViewModel: TypeViewModel,
    val generationViewModel: GenerationViewModel,
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TypesSectionNavHost(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            }
        }
}

class MainPokedexFragment : Fragment() {
    // Obtain ViewModels (scoped to Activity or Fragment as needed)
    private val pokemonViewModel: PokemonViewModel by activityViewModel()
    private val moveViewModel: MoveViewModel by activityViewModel()
    private val typeViewModel: TypeViewModel by activityViewModel()
    private val generationViewModel: GenerationViewModel by activityViewModel()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main_pokedex, container, false)

        pokemonViewModel.fetchPokemon()
        moveViewModel.fetchMove()
        typeViewModel.fetchType()
        generationViewModel.fetchGeneration()

        // Set the content of this ComposeView to your MainScreen composable
        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) {}
        moveViewModel.moveList.observe(viewLifecycleOwner) {}
        typeViewModel.typeList.observe(viewLifecycleOwner) {}
        generationViewModel.generationList.observe(viewLifecycleOwner) {}

        view.findViewById<ComposeView>(R.id.top_bar_compose_view).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val navController = rememberNavController()
                TopBarController(pokemonViewModel, navController)
            }
        }

        view.findViewById<ComposeView>(R.id.bottom_nav_compose_view).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PokedexBottomNavigationBar { selectedIndex ->
                    navigateToSection(selectedIndex, pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
                }
            }
        }

        // Load initial content fragment
        if (savedInstanceState == null) {
            navigateToSection(0, pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
        }
        return view
    }
    private fun navigateToSection(
        index: Int,
        pokemonViewModel: PokemonViewModel,
        moveViewModel: MoveViewModel,
        typeViewModel: TypeViewModel,
        generationViewModel: GenerationViewModel
    ) {
        val fragmentToShow: Fragment = when (index) {
            0 -> PokemonHostSectionHostFragment(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            1 -> GenerationsSectionHostFragment(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            2 -> TypesSectionHostFragment(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            3 -> MovesSectionHostFragment(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            else -> PokemonHostSectionHostFragment(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel) // Default
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.content_host_fragment_container, fragmentToShow)
            .commit()
    }
}

@Composable
fun PokedexBottomNavigationBar(onItemSelected: (Int) -> Unit) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Pokemon", "Generations", "Types", "Moves")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Favorite, Icons.Filled.Star, Icons.Filled.Info)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.Star, Icons.Outlined.Info)

    NavigationBar(containerColor = Crimson) {
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
                onClick = {
                    if (selectedIndex != index) {
                        selectedIndex = index
                        onItemSelected(index)
                    }
                }
            )
        }
    }
}

@Composable
fun TopBarController(pokemonViewModel: PokemonViewModel, navController: NavController){
    val pokemonList by pokemonViewModel.pokemonList.observeAsState(initial = emptyList())
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
            var currentPokemon = query.lowercase()
            navController.navigate("${AppDestinations.POKEMON_DETAIL_ROUTE}/$currentPokemon")
            Log.d("Detail","${AppDestinations.POKEMON_DETAIL_ROUTE}/$currentPokemon")
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
//        supportingContent = { resultText -> /* Optional: Add more details to each result item */ },
//        leadingContent = { /* Optional: Add a common leading element to result items */ },
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