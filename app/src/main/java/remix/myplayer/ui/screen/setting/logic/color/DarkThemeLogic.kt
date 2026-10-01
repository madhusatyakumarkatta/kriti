package com.krithi.ui.screen.setting.logic.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.data.prefs.ThemePrefs
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

private val itemRes = listOf(
  R.string.always_off,
  R.string.always_on,
  R.string.follow_system
)

@Composable
fun DarkThemeLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val state = rememberDialogState(false)

  val selected = when (settingState.color.darkTheme) {
    ThemePrefs.ALWAYS_OFF -> 0
    ThemePrefs.ALWAYS_ON -> 1
    else -> 2
  }

  NormalPreference(stringResource(R.string.dark_theme), stringResource(itemRes[selected])) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.dark_theme,
    itemRes = itemRes,
    positiveRes = null,
    negativeRes = null,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(selected) {
      if (selected == it) {
        return@ItemsCallbackSingleChoice
      }

      settingVM.setDarkTheme(
        when (it) {
          0 -> ThemePrefs.ALWAYS_OFF
          1 -> ThemePrefs.ALWAYS_ON
          else -> ThemePrefs.FOLLOW_SYSTEM
        }
      )
    }
  )
}