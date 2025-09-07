package com.jetpack.pokedex.navhost

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.screens.generations.GenerationDetailScreen
import com.jetpack.pokedex.screens.generations.GenerationsScreen
import com.jetpack.pokedex.screens.home.HomeScreen
import com.jetpack.pokedex.screens.home.PokemonDetailScreen
import com.jetpack.pokedex.screens.moves.MoveDetailScreen
import com.jetpack.pokedex.screens.moves.MovesScreen
import com.jetpack.pokedex.screens.types.TypeDetailScreen
import com.jetpack.pokedex.screens.types.TypesScreen
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModel
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import com.jetpack.pokedex.viewmodel.type.TypeViewModel

@Composable
fun TypesSectionNavHost(
    pokemonViewModel: PokemonViewModel,
    moveViewModel: MoveViewModel,
    typeViewModel: TypeViewModel,
    generationViewModel: GenerationViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.TYPE_LIST_ROUTE
    ) {
        composable(route = AppDestinations.POKEMON_LIST_ROUTE) {
            HomeScreen(pokemonViewModel, navController)
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
                    pokemonViewModel = pokemonViewModel
                )
            } else {
                Text("Error: Pokémon not found.")
            }
        }

        composable(route = AppDestinations.MOVE_LIST_ROUTE) {
            MovesScreen(moveViewModel, navController)
        }

        composable(
            route = "${AppDestinations.MOVE_DETAIL_ROUTE}/{${AppDestinations.MOVE_NAME_ARG}}",
            arguments = listOf(navArgument(AppDestinations.MOVE_NAME_ARG) { type =
                NavType.StringType })
        ) { backStackEntry ->
            val moveName = backStackEntry.arguments?.getString(AppDestinations.MOVE_NAME_ARG)
            if (moveName != null) {
                MoveDetailScreen(
                    moveName = moveName,
                    navController = navController,
                    moveViewModel = moveViewModel,
                    pokemonViewModel = pokemonViewModel
                )
            } else {
                Text("Error: Move not found.")
            }
        }

        composable(route = AppDestinations.GENERATION_LIST_ROUTE) {
            GenerationsScreen(generationViewModel, navController)
        }

        composable(
            route = "${AppDestinations.GENERATION_DETAIL_ROUTE}/{${AppDestinations.GENERATION_NAME_ARG}}",
            arguments = listOf(navArgument(AppDestinations.GENERATION_NAME_ARG) { type =
                NavType.StringType })
        ) { backStackEntry ->
            val generationName = backStackEntry.arguments?.getString(AppDestinations.GENERATION_NAME_ARG)
            if (generationName != null) {
                GenerationDetailScreen(
                    generationName = generationName,
                    navController = navController,
                    generationViewModel = generationViewModel,
                    pokemonViewModel = pokemonViewModel,
                    moveViewModel = moveViewModel
                )
            } else {
                Text("Error: Generation not found.")
            }
        }

        composable(route = AppDestinations.TYPE_LIST_ROUTE) {
            TypesScreen(typeViewModel, navController)
        }

        composable(
            route = "${AppDestinations.TYPE_DETAIL_ROUTE}/{${AppDestinations.TYPE_NAME_ARG}}",
            arguments = listOf(navArgument(AppDestinations.TYPE_NAME_ARG) { type =
                NavType.StringType })
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString(AppDestinations.TYPE_NAME_ARG)
            if (typeName != null) {
                TypeDetailScreen(
                    typeName = typeName,
                    navController = navController,
                    typeViewModel = typeViewModel,
                    pokemonViewModel = pokemonViewModel
                )
            } else {
                Text("Error: Type not found.")
            }
        }
    }
}