package com.krithi.ui.screen.setting.logic.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.settingViewModel

@Composable
fun ShowDisplayNameLogic() {
  val libraryVM = libraryViewModel
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.show_displayname),
    stringResource(R.string.show_displayname_tip),
    settingState.common.showDisplayName
  ) {
    settingVM.setShowDisplayName(it)
    libraryVM.fetchMedia()
  }
}