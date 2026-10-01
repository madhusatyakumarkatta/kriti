package com.krithi.ui.screen.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.data.prefs.SettingPrefs
import com.krithi.ui.nav.DetailScreenRoute
import com.krithi.ui.nav.LocalNavController
import com.krithi.ui.theme.LocalTheme
import com.krithi.ui.widget.library.ModeHeader
import com.krithi.ui.widget.library.list.GridItem
import com.krithi.ui.widget.library.list.ListItem
import com.krithi.util.ext.spanCount
import com.krithi.util.ext.verticalScrollbar
import com.krithi.viewmodel.MultiSelectState
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.mainViewModel
import com.krithi.viewmodel.settingViewModel


@Composable
fun GenreScreen() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val genres by libraryViewModel.genres.collectAsStateWithLifecycle()
  val nav = LocalNavController.current
  val mode = settingState.library.genreMode

  val mainVM = mainViewModel
  val multiSelectState by mainVM.multiSelectState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val popupEnabled = !multiSelectState.isShowInLibrary()

  Column(
    modifier = Modifier.background(LocalTheme.current.libraryBackground)
  ) {
    ModeHeader(mode == SettingPrefs.GRID_MODE) {
      if (mode == it) {
        return@ModeHeader
      }
      settingVM.setGenreMode(if (mode == SettingPrefs.GRID_MODE) SettingPrefs.LIST_MODE else SettingPrefs.GRID_MODE)
    }

    val selectedIds by remember {
      derivedStateOf {
        multiSelectState.selectedModels(MultiSelectState.Where.Genre)
      }
    }

    if (mode == SettingPrefs.LIST_MODE) {
      val listState = rememberLazyListState()
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .verticalScrollbar(listState)
      ) {
        itemsIndexed(genres, key = { _, genre ->
          genre.id
        }) { _, genre ->
          ListItem(
            modifier = Modifier.height(64.dp),
            model = genre,
            text1 = genre.genre,
            selected = selectedIds.contains(genre.getKey()),
            text2 = pluralStringResource(R.plurals.song_num, genre.count, genre.count),
            popupEnabled = popupEnabled,
            onClick = {
              if (multiSelectState.where == MultiSelectState.Where.Genre) {
                mainVM.updateMultiSelectModel(genre)
                return@ListItem
              }

              nav.navigate(DetailScreenRoute(genre = genre))
            },
            onLongClick = {
              mainVM.showMultiSelect(context, MultiSelectState.Where.Genre, genre)
            })
        }
      }
    } else {
      val gridState = rememberLazyGridState()
      LazyVerticalGrid(
        modifier = Modifier
          .weight(1f)
          .verticalScrollbar(gridState),
        state = gridState,
        columns = GridCells.Fixed(spanCount()),
        content = {
          items(genres, key = {
            it.id
          }) { genre ->
            GridItem(
              genre,
              text1 = genre.genre,
              selected = selectedIds.contains(genre.getKey()),
              popupEnabled = popupEnabled,
              onClick = {
                if (multiSelectState.where == MultiSelectState.Where.Genre) {
                  mainVM.updateMultiSelectModel(genre)
                  return@GridItem
                }

                nav.navigate(DetailScreenRoute(genre = genre))
              },
              onLongClick = {
                mainVM.showMultiSelect(context, MultiSelectState.Where.Genre, genre)
              })
          }
        })
    }
  }
}
