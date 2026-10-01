package com.krithi.ui.screen.setting.logic.play

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.data.prefs.SettingPrefs
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun DecoderModeLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()
  val decoderMode = settingState.play.decoderMode
  val state = rememberDialogState(false)

  NormalPreference(
    stringResource(R.string.audio_decoder_mode),
    stringResource(R.string.audio_decoder_mode_tip)
  ) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.audio_decoder_mode,
    itemRes = listOf(
      R.string.audio_decoder_mode_default,
      R.string.audio_decoder_mode_ffmpeg
    ),
    positiveRes = null,
    negativeRes = null,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(
      selected = when (decoderMode) {
        SettingPrefs.DECODER_MODE_FFMPEG -> 1
        else -> 0
      }
    ) {
      settingVM.setDecoderMode(
        when (it) {
          1 -> SettingPrefs.DECODER_MODE_FFMPEG
          else -> SettingPrefs.DECODER_MODE_DEFAULT
        }
      )
    }
  )
}
