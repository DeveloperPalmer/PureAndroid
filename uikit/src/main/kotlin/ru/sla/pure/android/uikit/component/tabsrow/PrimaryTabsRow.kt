package ru.sla.pure.android.uikit.component.tabsrow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sla.pure.android.core.ui.text.TextRef
import ru.sla.pure.android.core.ui.text.strRef
import ru.sla.pure.android.uikit.theme.AppTheme
import ru.sla.pure.android.uikit.theme.ColorTheme

@Composable
fun <T : Tab> PrimaryTabsRow(
  tabList: List<T>,
  selectedTab: T,
  onTabClick: (T) -> Unit,
  modifier: Modifier = Modifier
) {
  TabsRowInternal(
    modifier = modifier,
    tabList = tabList,
    selectedTab = selectedTab,
    onTabClick = onTabClick,
    height = 48.dp,
    borderWidth = null,
    colors = DefaultTabsRow.primaryColors()
  )
}

@Preview
@Composable
private fun PrimaryTabsRowPreviewLight() {
  PrimaryTabsPreview(ColorTheme.Light)
}

@Preview
@Composable
private fun PrimaryTabsRowPreviewDark() {
  PrimaryTabsPreview(ColorTheme.Dark)
}

@Composable
private fun PrimaryTabsPreview(theme: ColorTheme) {
  AppTheme(colorTheme = theme) {
    Column(
      modifier = Modifier
        .background(AppTheme.colors.backgroundPrimary)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      PrimaryTabsRow(
        tabList = PrimaryTabsRowEntity.entries,
        selectedTab = PrimaryTabsRowEntity.One,
        onTabClick = {}
      )
      PrimaryTabsRow(
        tabList = PrimaryTabsRowEntity.entries,
        selectedTab = PrimaryTabsRowEntity.Two,
        onTabClick = {}
      )
    }
  }
}

private enum class PrimaryTabsRowEntity(override val title: TextRef) : Tab {
  One(title = strRef("TAB-1")),
  Two(title = strRef("TAB-2"))
}
