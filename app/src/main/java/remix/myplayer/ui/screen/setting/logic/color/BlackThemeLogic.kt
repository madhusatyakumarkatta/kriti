package com.krithi.ui.screen.setting.logic.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun BlackThemeLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.black_theme),
    stringResource(R.string.black_theme_tip),
    settingState.color.blackTheme
  ) {
    settingVM.setBlackTheme(it)
  }
}