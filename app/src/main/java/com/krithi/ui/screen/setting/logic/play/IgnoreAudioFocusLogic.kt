package com.krithi.ui.screen.setting.logic.play

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun IgnoreAudioFocusLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.audio_focus),
    stringResource(R.string.audio_focus_tip),
    settingState.play.ignoreAudioFocus
  ) {
    settingVM.setIgnoreAudioFocus(it)
  }
}