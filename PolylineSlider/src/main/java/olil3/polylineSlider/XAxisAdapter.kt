package olil3.polylineSlider

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class XAxisAdapter(
    private val mContext: Context,
    private val mNumberOfTextBoxes: Int,
    private val mTextBoxSpacing: Int,
    private val mUnit: String,
    private val mTextBoxViewIDs: IntArray
) : RecyclerView.Adapter<XAxisAdapter.TextBoxViewHolder>() {
    class TextBoxViewHolder(mTextBox: TextView) : RecyclerView.ViewHolder(mTextBox) {
        val mTextView = mTextBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextBoxViewHolder {
        val mTextBox = TextView(mContext)
        mTextBox.id = View.generateViewId()
        mTextBox.textSize = 18f
        mTextBox.gravity = Gravity.CENTER
        mTextBox.layoutParams =
            ViewGroup.LayoutParams(mTextBoxSpacing, ViewGroup.LayoutParams.MATCH_PARENT)

        return TextBoxViewHolder(mTextBox)
    }

    override fun getItemCount(): Int {
        return mNumberOfTextBoxes
    }

    override fun onBindViewHolder(holder: TextBoxViewHolder, position: Int) {
        holder.mTextView.text = (position.toString() + mUnit)
        mTextBoxViewIDs[position] = holder.mTextView.id
    }

    override fun getItemId(position: Int): Long {
        return mTextBoxViewIDs[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
