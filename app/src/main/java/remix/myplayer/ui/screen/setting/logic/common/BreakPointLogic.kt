package com.krithi.ui.screen.setting.logic.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun BreakPointLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.play_breakpoint),
    stringResource(R.string.play_breakpoint_tip),
    settingState.play.playAtBreakPoint
  ) {
    settingVM.setPlayAtBreakPoint(it)
  }
}