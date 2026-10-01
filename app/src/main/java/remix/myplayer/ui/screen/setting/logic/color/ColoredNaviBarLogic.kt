package com.krithi.ui.screen.setting.logic.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun ColoredNaviBarLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.navigation_color),
    stringResource(R.string.navigation_is_show),
    settingState.color.coloredNaviBar
  ) {
    settingVM.setColoredNaviBar(it)
  }
}