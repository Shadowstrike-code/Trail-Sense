package com.kylecorry.trail_sense.tools.backtrack.ui

import android.content.Context
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.sol.time.Time.toZonedDateTime
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.ListItemPlainIconMenuBinding
import com.kylecorry.trail_sense.shared.CustomUiUtils
import com.kylecorry.trail_sense.shared.DistanceUtils.toRelativeDistance
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.shared.Units
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.paths.Path2

class PathListItem(
    private val context: Context,
    private val formatService: FormatService,
    private val prefs: UserPreferences,
    private val action: (path: Path2, action: PathAction) -> Unit
) {

    fun display(
        itemBinding: ListItemPlainIconMenuBinding,
        item: Path2
    ) {
        itemBinding.icon.setImageResource(if (item.temporary) R.drawable.ic_update else R.drawable.ic_tool_backtrack)
        CustomUiUtils.setImageColor(
            itemBinding.icon,
            Resources.androidTextColorSecondary(context)
        )

        val start = item.metadata.duration?.start
        val end = item.metadata.duration?.end
        val distance =
            item.metadata.distance.convertTo(prefs.baseDistanceUnits).toRelativeDistance()
        itemBinding.title.text = if (item.name != null) {
            item.name
        } else if (start != null && end != null) {
            formatService.formatTimeSpan(start.toZonedDateTime(), end.toZonedDateTime(), true)
        } else {
            context.getString(android.R.string.untitled)
        }
        itemBinding.description.text = formatService.formatDistance(
            distance,
            Units.getDecimalPlaces(distance.units),
            false
        )
        itemBinding.root.setOnClickListener {
            action(item, PathAction.Show)
        }
        itemBinding.menuBtn.setOnClickListener {
            val actions = listOf(
                PathAction.Rename,
                PathAction.Keep,
                PathAction.ToggleVisibility,
                PathAction.Export,
                PathAction.Merge,
                PathAction.Delete,
            )

            Pickers.menu(
                it, listOf(
                    context.getString(R.string.rename),
                    if (item.temporary) context.getString(R.string.keep_forever) else null,
                    if (item.style.visible) context.getString(R.string.hide) else context.getString(
                        R.string.show
                    ),
                    context.getString(R.string.export),
                    context.getString(R.string.path_merge_previous),
                    context.getString(R.string.delete),
                )
            ) {
                action(item, actions[it])
                true
            }
        }
    }

}