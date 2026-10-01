package com.krithi.ui.screen.setting.logic.common

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.helper.LanguageHelper
import com.krithi.ui.activity.ComposeActivity
import com.krithi.ui.dialog.ItemsCallbackSingleChoice
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.screen.setting.NormalPreference
import com.krithi.viewmodel.settingViewModel

private val itemRes = listOf(
  R.string.auto,
  R.string.zh_simple,
  R.string.zh_traditional,
  R.string.english,
  R.string.japanese,
)
@Composable
fun LanguageLogic() {
  val context = LocalContext.current
  val settingState by settingViewModel.settingsState.collectAsStateWithLifecycle()
  val select = settingState.common.language

  val state = rememberDialogState(false)
  NormalPreference(
    stringResource(R.string.select_language),
    stringResource(R.string.select_language_tips)
  ) {
    state.show()
  }

  NormalDialog(
    dialogState = state,
    titleRes = R.string.select_language,
    positiveRes = null,
    negativeRes = null,
    itemRes = itemRes,
    itemsCallbackSingleChoice = ItemsCallbackSingleChoice(select) {
      if (select == it) {
        return@ItemsCallbackSingleChoice
      }
      LanguageHelper.saveSelectLanguage(context, it)

      val intent = Intent(context, ComposeActivity::class.java)
      intent.action = Intent.ACTION_MAIN
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(intent)
    }
  )


}