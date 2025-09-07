package com.jetpack.pokedex.screens.home

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.sidecomponents.BackwardButton
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.ui.theme.LT100
import com.jetpack.pokedex.ui.theme.LT150
import com.jetpack.pokedex.ui.theme.LT200
import com.jetpack.pokedex.ui.theme.LT250
import com.jetpack.pokedex.ui.theme.LT300
import com.jetpack.pokedex.ui.theme.LT50
import com.jetpack.pokedex.ui.theme.LightGrey
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    navController: NavController,
    pokemonViewModel: PokemonViewModel
) {
    var pokemonDetail by remember { mutableStateOf<Pokemon?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pokemonName) {
        isLoading = true
        errorMessage = null
        try {
            pokemonDetail = pokemonViewModel.getPokemonDetailByName(pokemonName)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        if (pokemonDetail != null) {

            // Pokemon Img
            NetworkImage(
                url = pokemonDetail!!.img,
                contentDescription = pokemonDetail!!.name,
                modifier = Modifier.size(200.dp)
            )

            // Pokemon Name
            Text(
                pokemonDetail!!.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase()
                    else it.toString() },
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Pokemon Types
            Box{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (pokemonDetail!!.types.isNotEmpty()) {
                        pokemonDetail!!.types.forEach { type ->
                            val color = getTypeColor(type)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/$type");
                                    },
                                contentAlignment = Alignment.Center,
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

            // Pokemon Height & Weight
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            ){
                Text("Height: ${pokemonDetail!!.height} inch")
                Text(text = "Weight: ${pokemonDetail!!.weight} pound")
            }

            // Pokemon Species
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Species: " + pokemonDetail!!.species.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase()
                    else it.toString() }
            )

            // Pokemon Base Stats
            Spacer(modifier = Modifier.height(16.dp))
            Card (
                modifier = Modifier
                    .width(maxOf(250.dp, 370.dp))

            ){
                Text(text = "Base Stats: ", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp))
                for (stat in pokemonDetail!!.stats){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp), // vertical spacing between rows
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        // Name column
                        Text(
                            text = "${stat.stat.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}:",
                            modifier = Modifier.width(110.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Bar column
                        val baseStat = stat.baseStat.toInt().coerceIn(1, 300)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(LightGrey)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(baseStat.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statBarColor(baseStat))
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Pokemon Abilities
            Spacer(modifier = Modifier.height(16.dp))
            Box (modifier = Modifier.width(maxOf(250.dp, 370.dp))){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    Column (
                        modifier = Modifier
                            .width(70.dp)
                    ){ Text(text = "Abilities: ", modifier = Modifier.padding(vertical = 10.dp)) }
                    Column (modifier = Modifier.fillMaxWidth()) {
                        if (pokemonDetail!!.abilities.isNotEmpty()) {
                            pokemonDetail!!.abilities.forEach { ability ->
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                        .padding(top = 5.dp, bottom = 5.dp)
                                ){
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = LightGrey),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Text(
                                            text = ability.ability.name.replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase()
                                                else it.toString() }
                                                    + " - "
                                                    + isHiddenAbility(ability.isHidden),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
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

            // Pokemon Moves
            Spacer(modifier = Modifier.height(16.dp))
            Box (modifier = Modifier.width(maxOf(250.dp, 370.dp))){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    Column (
                        modifier = Modifier
                            .width(70.dp)
                    ){ Text(text = "Moves: ", modifier = Modifier.padding(vertical = 10.dp)) }
                    Column (modifier = Modifier.fillMaxWidth()) {
                        if (pokemonDetail!!.moves.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between items on the same line
                                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between lines
                            ) {
                                pokemonDetail!!.moves.forEach { move ->
                                    var moveBaseName = move.move.name.split("-")
                                    var moveName = moveBaseName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                    for (i in 1 until moveBaseName.size){
                                        moveName += " ${moveBaseName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = LightGrey)
                                            .clickable {
                                                navController.navigate("${AppDestinations.MOVE_DETAIL_ROUTE}/${move.move.name}");
                                            },
                                        contentAlignment = Alignment.Center,
                                    ){
                                        Text(
                                            text = moveName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
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

            // Stats
            Spacer(modifier = Modifier.height(8.dp))
            Box (modifier = Modifier.width(maxOf(250.dp, 350.dp))){

            }


        } else {
            CircularProgressIndicator()
        }
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

fun isHiddenAbility(isHidden: Boolean): String {
    return if(isHidden){
        "(Hidden)"
    } else {
        "(Exposed)"
    }
}

fun statBarColor(stat: Int): Color{
    return if (stat in 0..50) {
        LT50
    } else if (stat in 51..100) {
        LT100
    } else if (stat in 101..150) {
        LT150
    } else if (stat in 151..200) {
        LT200
    } else if (stat in 201..250) {
        LT250
    } else {
        LT300
    }
}
