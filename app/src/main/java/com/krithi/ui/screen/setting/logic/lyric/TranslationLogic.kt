package com.krithi.ui.screen.setting.logic.lyric

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.screen.setting.SwitchPreference
import com.krithi.viewmodel.settingViewModel

@Composable
fun TranslationLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  SwitchPreference(
    stringResource(R.string.lyric_translation_title),
    stringResource(R.string.lyric_translation_tip),
    settingState.lyric.translationEnabled
  ) {
    settingVM.setLyricTranslationEnabled(it)
  }
}
