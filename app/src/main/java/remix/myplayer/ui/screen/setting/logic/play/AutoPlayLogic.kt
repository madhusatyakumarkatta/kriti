package com.krithi.ui.screen.setting.logic.play

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun AutoPlayLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()
  val autoPlay = settingState.play.autoPlay
  val state = rememberDialogState(false)

  NormalPreference(stringResource(R.string.auto_play), stringResource(R.string.auto_play_tip)) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.auto_play,
    itemRes = listOf(
      R.string.auto_play_headset_plug,
      R.string.auto_play_open_software,
      R.string.auto_play_none
    ),
    positiveRes = null,
    negativeRes = null,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(autoPlay) {
      if (autoPlay == it) {
        return@ItemsCallbackSingleChoice
      }
      settingVM.setAutoPlay(it)
    }
  )

}