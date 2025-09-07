@file:Suppress("KotlinConstantConditions")

package com.jetpack.pokedex.screens.moves

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.ui.theme.LightGrey
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import kotlinx.coroutines.launch

@Composable
fun MovesScreen(moveViewModel: MoveViewModel, navController: NavController) {
    val moveList by moveViewModel.moveList.observeAsState(initial = emptyList())
    val isLoading by moveViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by moveViewModel.errorMessage.observeAsState(initial = null)
    val canLoadMore by moveViewModel.isLoading.observeAsState(initial = false)

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (moveList.isEmpty() && isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (moveList.isEmpty()) {
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
                    Button(onClick = { moveViewModel.fetchMove() }) {
                        Text("Retry")
                    }
                }
            } else {
                MoveLazyList(
                    moveList = moveList,
                    isLoadingMore = isLoading && moveList.isEmpty(),
                    canLoadMore = canLoadMore,
                    onLoadMore = {
                        moveViewModel.fetchMove(loadMore = true)
                    },
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun MoveLazyList(
    moveList: List<MoveDetail>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    navController: NavController
) {
    val state = rememberScrollState()
    //Here we create a condition if the firstVisibleItemIndex is greater than 0
    val showButton by remember {
        derivedStateOf {
            state.value > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (moveList.isNotEmpty()) {
            Text(
                text = "Moves",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            FlowRow(
                modifier = Modifier
                    .width(maxOf(250.dp, 370.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between items on the same line
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between lines
            ) {
                moveList.forEach { move ->
                    var moveBaseName = move.name.split("-")
                    var moveName = moveBaseName[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    for (i in 1 until moveBaseName.size){
                        moveName += " ${moveBaseName[i].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = LightGrey)
                            .clickable {
                                navController.navigate("${AppDestinations.MOVE_DETAIL_ROUTE}/${move.name}")
                            },
                        contentAlignment = Alignment.Center,
                    ){
                        Text(
                            text = moveName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(top = 3.dp, bottom = 3.dp, start = 10.dp, end = 10.dp),
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
}
