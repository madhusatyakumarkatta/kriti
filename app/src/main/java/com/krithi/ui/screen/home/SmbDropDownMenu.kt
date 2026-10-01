package com.krithi.ui.screen.home

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krithi.R
import com.krithi.misc.manager.DynamicModuleStatus
import com.krithi.ui.dialog.DialogState
import com.krithi.ui.dialog.NormalDialog
import com.krithi.ui.dialog.dismissLoading
import com.krithi.ui.dialog.rememberDialogState
import com.krithi.ui.dialog.showLoading
import com.krithi.ui.dialog.updateLoadingText
import com.krithi.ui.nav.MessageNotifier
import com.krithi.ui.theme.LocalTheme
import com.krithi.viewmodel.SmbViewModel
import timber.log.Timber

@Composable
internal fun SmbDropDownMenu(smbVM: SmbViewModel, onShowSmbAddDialog: () -> Unit) {
  val context = LocalContext.current

  val smbDialogState = rememberDialogState()
  val installStatus by smbVM.moduleInstallStatus.collectAsStateWithLifecycle()

  LaunchedEffect(installStatus) {
    val status = installStatus ?: return@LaunchedEffect
    Timber.v("installSmbModule, status: $status")
    when (status) {
      is DynamicModuleStatus.Downloading -> {
        showLoading(false, context.getString(R.string.downloading))
        updateLoadingText(
          context.getString(
            R.string.smb_downloading_with_progress,
            (status.progress * 100).toInt()
          )
        )
      }

      DynamicModuleStatus.Installing -> {
        showLoading(false)
        updateLoadingText(context.getString(R.string.smb_installing))
      }

      DynamicModuleStatus.Installed -> {
        dismissLoading()
        onShowSmbAddDialog()
        smbVM.clearInstallStatus()
      }

      DynamicModuleStatus.Canceled -> {
        dismissLoading()
        MessageNotifier.show(R.string.smb_download_canceled)
        smbVM.clearInstallStatus()
      }

      is DynamicModuleStatus.Error -> {
        dismissLoading()
        MessageNotifier.show(
          R.string.smb_download_failed,
          status.exception.localizedMessage
        )
        smbVM.clearInstallStatus()
      }

      is DynamicModuleStatus.Failed -> {
        dismissLoading()
        MessageNotifier.show(R.string.smb_download_failed, status.errorCode.toString())
        smbVM.clearInstallStatus()
      }

      DynamicModuleStatus.Pending -> {
        showLoading(false, context.getString(R.string.downloading))
      }

      DynamicModuleStatus.UnAvailable -> {
        dismissLoading()
        MessageNotifier.show(R.string.smb_module_not_supported)
        smbVM.clearInstallStatus()
      }

      DynamicModuleStatus.Unknown -> {
        dismissLoading()
        MessageNotifier.show(R.string.smb_unknown_error)
        smbVM.clearInstallStatus()
      }
    }
  }

  DropdownMenuItem(
    text = {
      Text(
        stringResource(R.string.smb),
        color = LocalTheme.current.textPrimary
      )
    },
    onClick = {
      if (smbVM.isSmbModuleInstalled) {
        onShowSmbAddDialog()
      } else {
        smbDialogState.show()
      }
    }
  )

  SmbDialog(smbDialogState) {
    smbVM.startSmbModuleInstallation()
  }
}

@Composable
private fun SmbDialog(dialogState: DialogState, onConfirm: () -> Unit) {
  NormalDialog(
    dialogState = dialogState,
    title = stringResource(R.string.smb_download_required_title),
    content = stringResource(R.string.smb_download_required_message),
    onPositive = onConfirm
  )
}