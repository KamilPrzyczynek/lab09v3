import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lab09v3.R

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    private val background: ColorDrawable = ColorDrawable(Color.RED)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2

        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(canvas)

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteIcon.draw(canvas)
    }
}
