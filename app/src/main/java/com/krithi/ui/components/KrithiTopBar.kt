package com.krithi.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KrithiTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title, color = PrimaryTextDark) },
        modifier = modifier,
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDark,
            titleContentColor = PrimaryTextDark,
            actionIconContentColor = PrimaryTextDark
        )
    )
}
