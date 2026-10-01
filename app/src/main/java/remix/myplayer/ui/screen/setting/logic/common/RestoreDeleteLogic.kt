package com.krithi.ui.screen.setting.logic.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.krithi.R
import com.krithi.ui.nav.MessageNotifier
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.libraryViewModel
import com.krithi.viewmodel.settingViewModel

@Composable
fun RestoreDeleteLogic() {
  val libraryVM = libraryViewModel
  val settingVM = settingViewModel

  NormalPreference(
    stringResource(R.string.restore_songs),
    stringResource(R.string.restore_songs_tip)
  ) {
    settingVM.setDeleteIds(emptySet())
    libraryVM.fetchMedia()
    MessageNotifier.show(R.string.alread_restore_songs)
  }

}