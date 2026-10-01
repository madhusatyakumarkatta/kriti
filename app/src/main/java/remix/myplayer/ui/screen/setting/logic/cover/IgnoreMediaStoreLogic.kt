package com.krithi.ui.screen.setting.logic.cover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.settingViewModel

@Composable
fun IgnoreMediaStoreLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val libraryVM = libraryViewModel

  SwitchPreference(
    stringResource(R.string.ignore_mediastore_artwork),
    stringResource(R.string.ignore_mediastore_artwork_tips),
    settingState.cover.ignoreMediaStore
  ) {
    settingVM.setIgnoreMediaStore(it)
    libraryVM.fetchMedia(true)
  }
}