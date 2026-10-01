package com.krithi.ui.screen.setting.logic.playingscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun KeepScreenOnLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.screen_always_on_title),
    stringResource(R.string.screen_always_on_tip),
    settingState.playingScreen.keepScreenOn
  ) {
    settingVM.setKeepScreenOn(it)
  }
}