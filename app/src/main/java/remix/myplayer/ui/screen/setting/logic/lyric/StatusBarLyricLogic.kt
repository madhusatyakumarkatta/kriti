package com.krithi.ui.screen.setting.logic.lyric

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.service.Command
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.util.MusicUtil
import com.krithi.util.Util.isSupportStatusBarLyric
import com.krithi.util.Util.sendLocalBroadcast
import com.krithi.viewmodel.settingViewModel

@Composable
fun StatusBarLyricLogic() {
  val context = LocalContext.current
  if (!isSupportStatusBarLyric(context)) {
    return
  }

  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.statusbar_lrc),
    checked = settingState.lyric.statusBarLyricEnabled
  ) {
    settingVM.setStatusBarLyricEnabled(it)

    val intent =
      MusicUtil.makeCmdIntent(Command.TOGGLE_STATUS_BAR_LRC)
    sendLocalBroadcast(intent)
  }
}