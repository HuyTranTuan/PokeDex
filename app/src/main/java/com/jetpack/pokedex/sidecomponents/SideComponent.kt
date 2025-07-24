package com.jetpack.pokedex.sidecomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ScrollToTopButton(goToTop:() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(end = 16.dp, bottom = 105.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = goToTop,
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            Icon(Icons.Filled.KeyboardArrowUp, "Scroll to Top")
        }
    }
}

@Composable
fun BackwardButton(navController: NavController) {
    FloatingActionButton(
        shape = RoundedCornerShape(25),
        onClick = { navController.navigateUp() },
        modifier = Modifier
            .padding(top = 120.dp, start = 16.dp)
            .size(50.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Go Backward")
    }
}