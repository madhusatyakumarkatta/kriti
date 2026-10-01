package com.krithi.ui.screen.setting.logic.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.data.prefs.SettingPrefs
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

private val itemRes = listOf(
  R.string.aplayer_lockscreen,
  R.string.system_lockscreen,
  R.string.close
)

@Composable
fun LockScreenLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val select = settingState.common.lockScreen

  val lockScreenState = rememberDialogState(false)
  NormalPreference(
    stringResource(R.string.lockscreen_show), content = stringResource(
      when (select) {
        SettingPrefs.LOCKSCREEN_APLAYER -> R.string.aplayer_lockscreen_tip
        SettingPrefs.LOCKSCREEN_SYSTEM -> R.string.system_lockscreen_tip
        else -> R.string.lockscreen_off_tip
      }
    )
  ) {
    lockScreenState.show()
  }

  NormalDialog(
    dialogState = lockScreenState,
    titleRes = R.string.lockscreen_show,
    positiveRes = null,
    negativeRes = null,
    itemRes = itemRes,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(select) {
      if (select == it) {
        return@ItemsCallbackSingleChoice
      }
      settingVM.setLockScreen(it)
    }
  )
}