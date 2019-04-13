package mustafaozhan.github.com.mycurrencies.base.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

interface AutoUpdatableAdapter {
    fun <T> RecyclerView.Adapter<*>.autoNotify(
        oldList: MutableList<T>,
        newList: MutableList<T>,
        compare: (T, T) -> Boolean
    ) = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            compare(oldList[oldItemPosition], newList[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] === newList[newItemPosition]

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size
    }
    ).dispatchUpdatesTo(this)
}