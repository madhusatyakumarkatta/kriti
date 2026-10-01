package com.krithi.ui.screen.setting.logic.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.util.Constants.KB
import com.krithi.util.Constants.MB
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.settingViewModel

private val items = intArrayOf(0, 500 * KB, MB, 2 * MB, 5 * MB)

@Composable
fun ScanSizeLogic() {
  val libraryVM = libraryViewModel
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val scanSizeState = rememberDialogState(false)
  NormalPreference(
    stringResource(R.string.music_filter),
    stringResource(R.string.set_filter_size)
  ) {
    scanSizeState.show()
  }

  val select = items.indexOfFirst {
    it == settingState.common.scanSize
  }
  if (select < 0) {
    throw IllegalArgumentException("illegal pos, scanSize: ${settingState.common.scanSize}")
  }

  NormalDialog(
    dialogState = scanSizeState,
    title = stringResource(R.string.set_filter_size),
    positive = null,
    negative = null,
    items = listOf("0K", "500K", "1MB", "2MB", "5MB"),
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(select) {
      if (select == it) {
        return@ItemsCallbackSingleChoice
      }

      settingVM.setScanSize(items[it])
      libraryVM.fetchMedia()
    }
  )
}