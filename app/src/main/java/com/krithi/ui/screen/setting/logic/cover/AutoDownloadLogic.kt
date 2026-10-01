package com.krithi.ui.screen.setting.logic.cover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.settingViewModel

private val itemRes = listOf(R.string.always, R.string.wifi_only, R.string.never)

@Composable
fun AutoDownloadLogic() {
  val libraryVM = libraryViewModel
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val selected = settingState.cover.autoDownloadCover

  val state = rememberDialogState(false)
  NormalPreference(
    stringResource(R.string.auto_download_album_artist_cover),
    stringResource(itemRes[selected])
  ) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.auto_download_album_artist_cover,
    positiveRes = null,
    negativeRes = null,
    itemRes = itemRes,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(selected) {
      if (selected == it) {
        return@ItemsCallbackSingleChoice
      }
      settingVM.setAutoDownloadCover(it)
      libraryVM.fetchMedia(true)
    }
  )
}