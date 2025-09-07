package com.jetpack.pokedex.screens.types

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jetpack.pokedex.AppDestinations
import com.jetpack.pokedex.screens.home.getTypeColor
import com.jetpack.pokedex.screens.moves.getGenerationName
import com.jetpack.pokedex.sidecomponents.ScrollToTopButton
import com.jetpack.pokedex.viewmodel.type.TypeViewModel
import kotlinx.coroutines.launch

@Composable
fun TypesScreen(typeViewModel: TypeViewModel, navController: NavController) {
    val typeList = typeViewModel.typeList.observeAsState(initial = emptyList()).value
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
        Text(
            text = "Types",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        // Type List
        for (i in typeList){
            Row (
                modifier = Modifier
                    .width(maxOf(200.dp, 370.dp))
                    .padding(vertical = 8.dp),
            ) {
                Column (
                    modifier = Modifier.width(maxOf(80.dp, 100.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(getTypeColor(i.name))
                            .clickable {
                                navController.navigate("${AppDestinations.TYPE_DETAIL_ROUTE}/${i.name}");
                            }
                    ){
                        Text(
                            text = i.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "First appeared in Generation ${getGenerationName(i.generation)}",
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
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