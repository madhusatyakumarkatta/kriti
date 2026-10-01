package com.krithi.ui.widget.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.SharedFlow
import com.krithi.data.model.misc.Library
import com.krithi.ui.screen.RemoteScreen
import com.krithi.ui.screen.library.AlbumScreen
import com.krithi.ui.screen.library.ArtistScreen
import com.krithi.ui.screen.library.FolderScreen
import com.krithi.ui.screen.library.GenreScreen
import com.krithi.ui.screen.library.PlayListScreen
import com.krithi.ui.screen.library.SongScreen
import com.krithi.viewmodel.settingViewModel
import com.krithi.viewmodel.settings.SettingViewModel

@Composable
fun ViewPager(
  modifier: Modifier = Modifier,
  libraries: List<Library>,
  pagerState: PagerState,
  scrollToCurrentEvent: SharedFlow<Unit>? = null,
  vm: SettingViewModel = settingViewModel
) {
  HorizontalPager(
    modifier = modifier,
    state = pagerState,
    beyondViewportPageCount = 1
  ) { page ->
    val library = libraries.getOrNull(page) ?: return@HorizontalPager
    when (library.tag) {
      Library.TAG_SONG -> SongScreen(scrollToCurrentEvent)
      Library.TAG_ALBUM -> AlbumScreen()
      Library.TAG_ARTIST -> ArtistScreen()
      Library.TAG_GENRE -> GenreScreen()
      Library.TAG_PLAYLIST -> PlayListScreen()
      Library.TAG_FOLDER -> FolderScreen()
      Library.TAG_REMOTE -> RemoteScreen()
      else -> PageContent("Page: ${libraries[page]}")
    }
  }

  LaunchedEffect(pagerState.currentPage) {
    libraries.getOrNull(pagerState.currentPage)?.let {
      vm.changeLibrary(it)
    }
  }
}

@Composable
fun PageContent(data: String) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.LightGray)
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = data,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .padding(bottom = 16.dp)
        .fillMaxSize()
    )
  }
}
