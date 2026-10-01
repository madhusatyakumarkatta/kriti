package com.krithi.ui.screen.setting

import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krithi.BuildConfig
import com.krithi.R
import com.krithi.helper.EQHelper
import com.krithi.ui.nav.LocalNavController
import com.krithi.ui.nav.RouteAbout
import com.krithi.ui.nav.RouteEq
import com.krithi.ui.nav.RouteSettingDetail
import com.krithi.ui.screen.setting.logic.color.BlackThemeLogic
import com.krithi.ui.screen.setting.logic.color.ColoredNaviBarLogic
import com.krithi.ui.screen.setting.logic.color.DarkThemeLogic
import com.krithi.ui.screen.setting.logic.color.PrimaryColorLogic
import com.krithi.ui.screen.setting.logic.color.SecondaryColorLogic
import com.krithi.ui.screen.setting.logic.common.BlackListLogic
import com.krithi.ui.screen.setting.logic.common.BreakPointLogic
import com.krithi.ui.screen.setting.logic.common.ExportPlayListLogic
import com.krithi.ui.screen.setting.logic.common.ImportPlayListLogic
import com.krithi.ui.screen.setting.logic.common.LanguageLogic
import com.krithi.ui.screen.setting.logic.common.LockScreenLogic
import com.krithi.ui.screen.setting.logic.common.ManualScanLogic
import com.krithi.ui.screen.setting.logic.common.RestoreDeleteLogic
import com.krithi.ui.screen.setting.logic.common.ScanSizeLogic
import com.krithi.ui.screen.setting.logic.common.ShakeLogic
import com.krithi.ui.screen.setting.logic.common.ShowDisplayNameLogic
import com.krithi.ui.screen.setting.logic.common.UiFontScaleLogic
import com.krithi.ui.screen.setting.logic.cover.AutoDownloadLogic
import com.krithi.ui.screen.setting.logic.cover.DownloadSourceLogic
import com.krithi.ui.screen.setting.logic.cover.IgnoreMediaStoreLogic
import com.krithi.ui.screen.setting.logic.library.LibraryLogic
import com.krithi.ui.screen.setting.logic.lyric.DesktopLyricLogic
import com.krithi.ui.screen.setting.logic.lyric.LyricPriorityLogic
import com.krithi.ui.screen.setting.logic.lyric.StatusBarLyricLogic
import com.krithi.ui.screen.setting.logic.lyric.TranslationLogic
import com.krithi.ui.screen.setting.logic.notification.ClassicNotifyLogic
import com.krithi.ui.screen.setting.logic.notification.NotifyBackgroundLogic
import com.krithi.ui.screen.setting.logic.other.ClearCacheLogic
import com.krithi.ui.screen.setting.logic.play.AutoPlayLogic
import com.krithi.ui.screen.setting.logic.play.DecoderModeLogic
import com.krithi.ui.screen.setting.logic.play.IgnoreAudioFocusLogic
import com.krithi.ui.screen.setting.logic.play.ListLoopLogic
import com.krithi.ui.screen.setting.logic.play.PlayFadeLogic
import com.krithi.ui.screen.setting.logic.play.ReplayGainLogic
import com.krithi.ui.screen.setting.logic.playingscreen.KeepScreenOnLogic
import com.krithi.ui.screen.setting.logic.playingscreen.PlayingCoverAnimationLogic
import com.krithi.ui.screen.setting.logic.playingscreen.PlayingScreenBackgroundLogic
import com.krithi.ui.screen.setting.logic.playingscreen.PlayingScreenBottomLogic
import com.krithi.ui.theme.LocalTheme
import com.krithi.ui.widget.common.CommonAppBar
import com.krithi.viewmodel.mainViewModel

@Composable
fun SettingScreen() {
  Scaffold(
    topBar = { CommonAppBar(title = stringResource(R.string.setting), actions = emptyList()) },
    containerColor = LocalTheme.current.mainBackground,
  ) { contentPadding ->
    LazyColumn(
      modifier = Modifier.padding(contentPadding)
    ) {
      item {
        val nav = LocalNavController.current

        SettingCategory.entries.forEach { category ->
          SettingCategoryPreference(
            iconRes = category.iconRes,
            titleRes = category.titleRes,
            descriptionRes = category.descriptionRes,
          ) {
            nav.navigate(settingDetailRoute(category.route))
          }
        }
      }
    }
  }
}

@Composable
fun SettingDetailScreen(categoryKey: String) {
  val category = SettingCategory.fromRoute(categoryKey) ?: return

  Scaffold(
    topBar = { CommonAppBar(title = stringResource(category.titleRes), actions = emptyList()) },
    containerColor = LocalTheme.current.mainBackground,
  ) { contentPadding ->
    LazyColumn(
      modifier = Modifier.padding(contentPadding)
    ) {
      item {
        when (category) {
          SettingCategory.Common -> CommonPreferenceItems()
          SettingCategory.Play -> PlayPreferenceItems()
          SettingCategory.Color -> ColorPreferenceItems()
          SettingCategory.Library -> LibraryPreferenceItems()
          SettingCategory.PlayingScreen -> PlayingScreenPreferenceItems()
          SettingCategory.Cover -> CoverPreferenceItems()
          SettingCategory.Lyric -> LyricPreferenceItems()
          SettingCategory.Notification -> NotificationPreferenceItems()
          SettingCategory.Other -> OtherPreferenceItems()
        }
      }
    }
  }
}

@Composable
private fun SettingCategoryPreference(
  @DrawableRes iconRes: Int,
  @StringRes titleRes: Int,
  @StringRes descriptionRes: Int,
  onClick: () -> Unit,
) {
  val title = stringResource(titleRes)
  Preference(
    onClick = onClick,
    title = stringResource(titleRes),
    content = stringResource(descriptionRes),
    leading = {
      Icon(
        modifier = Modifier.padding(end = 24.dp),
        painter = painterResource(iconRes),
        contentDescription = title,
        tint = LocalTheme.current.primary
      )
    }
  )
}

private fun settingDetailRoute(categoryRoute: String): String {
  return "$RouteSettingDetail/$categoryRoute"
}

private enum class SettingCategory(
  @get:DrawableRes val iconRes: Int,
  @get:StringRes val titleRes: Int,
  @get:StringRes val descriptionRes: Int,
  val route: String,
) {

  Common(R.drawable.ic_tune_24dp, R.string.common, R.string.setting_common_desc, "common"),
  Play(R.drawable.ic_play_arrow_black_24dp, R.string.play, R.string.setting_play_desc, "play"),
  Color(R.drawable.ic_palette_24dp, R.string.color, R.string.setting_color_desc, "color"),
  Library(
    R.drawable.ic_library_books_24dp,
    R.string.library,
    R.string.setting_library_desc,
    "library"
  ),
  PlayingScreen(
    R.drawable.ic_smart_display_24dp,
    R.string.playing_screen,
    R.string.setting_playing_screen_desc,
    "playing_screen"
  ),
  Cover(R.drawable.ic_album_24dp, R.string.cover, R.string.setting_cover_desc, "cover"),
  Lyric(R.drawable.ic_lyrics_24dp, R.string.lrc, R.string.setting_lyric_desc, "lyric"),
  Notification(
    R.drawable.ic_notification_sound_24dp,
    R.string.notify,
    R.string.setting_notification_desc,
    "notification"
  ),
  Other(R.drawable.ic_info_outlined_24dp, R.string.other, R.string.setting_other_desc, "other");

  companion object {

    fun fromRoute(route: String): SettingCategory? {
      return entries.firstOrNull { it.route == route }
    }
  }
}

@Composable
private fun CommonPreferenceItems() {
  ScanSizeLogic()

  BlackListLogic()

  LockScreenLogic()

  ManualScanLogic()

  ImportPlayListLogic()

  ExportPlayListLogic()

  RestoreDeleteLogic()

  LanguageLogic()

  UiFontScaleLogic()

  ShakeLogic()

  ShowDisplayNameLogic()
}

@Composable
private fun PlayPreferenceItems() {
  IgnoreAudioFocusLogic()

  BreakPointLogic()

  PlayFadeLogic()

  ReplayGainLogic()

  ListLoopLogic()

  AutoPlayLogic()

  DecoderModeLogic()
}

@Composable
private fun ColorPreferenceItems() {
  DarkThemeLogic()

  BlackThemeLogic()

  PrimaryColorLogic()

  SecondaryColorLogic()

  ColoredNaviBarLogic()

}

@Composable
private fun LibraryPreferenceItems() {
  LibraryLogic()
}

@Composable
private fun PlayingScreenPreferenceItems() {
  PlayingScreenBackgroundLogic()

  PlayingScreenBottomLogic()

  KeepScreenOnLogic()
}

@Composable
private fun CoverPreferenceItems() {
  PlayingCoverAnimationLogic()

  IgnoreMediaStoreLogic()

  AutoDownloadLogic()

  DownloadSourceLogic()
}

@Composable
private fun NotificationPreferenceItems() {
  ClassicNotifyLogic()

  NotifyBackgroundLogic()
}

@Composable
private fun LyricPreferenceItems() {
  DesktopLyricLogic()

  TranslationLogic()

  StatusBarLyricLogic()

  LyricPriorityLogic()
}

@Composable
private fun OtherPreferenceItems() {
  val mainViewModel = mainViewModel
  val activity = LocalActivity.current
  val nav = LocalNavController.current

  ArrowPreference(R.string.eq_setting) {
    EQHelper.startEqualizer(activity ?: return@ArrowPreference) {
      nav.navigate(RouteEq)
    }
  }

  ArrowPreference(R.string.about_info) {
    nav.navigate(RouteAbout)
  }

  if (BuildConfig.FLAVOR == "normal") {
    Preference(onClick = {
      mainViewModel.checkInAppUpdate(true)
    }, title = stringResource(R.string.check_update))
  }

  ClearCacheLogic()
}
