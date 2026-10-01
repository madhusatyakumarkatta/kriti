package com.krithi.ui.screen.setting.logic.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.nav.MessageNotifier
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

private val itemRes = listOf(R.string.use_system_color, R.string.use_black_color)

@Composable
fun NotifyBackgroundLogic() {
  val settingVM = settingViewModel
  val settingState by settingVM.settingsState.collectAsStateWithLifecycle()

  val select = if (settingState.notification.notifyUseSystemBackground) 0 else 1
  val state = rememberDialogState()
  NormalPreference(
    stringResource(R.string.notify_bg_color),
    stringResource(R.string.notify_bg_color_info)
  ) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.notify_bg_color,
    itemRes = itemRes,
    positiveRes = null,
    negativeRes = null, itemsCallbackSingleChoice = ItemsCallbackSingleChoice(select) {
      if (select == it) {
        return@ItemsCallbackSingleChoice
      }
      if (!settingState.notification.classicNotify) {
        MessageNotifier.show(R.string.notify_bg_color_warnning)
        return@ItemsCallbackSingleChoice
      }

      settingVM.setNotifyUseSystemBackground(it == 0)
    }
  )
}