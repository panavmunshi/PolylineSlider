package olil3.polylineSlider.uiComponents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import olil3.polylineSlider.PolylineSlider
import olil3.polylineSlider.utils.EPointF
import olil3.polylineSlider.utils.PolyBezierPathUtil

internal class PolylineSliderGraph(
    mContext: Context,
    private val mNumberOfDataPoints: Int,
    private val mGradientColor: Int,
    private val mSliderSpacing: Int,
    private val mPolylineSlider: PolylineSlider,
    private val mSliderAlphaVal: Int,
    private val mThumbColorFilter: PorterDuffColorFilter,
    private val mSliderColorFilter: PorterDuffColorFilter,
    private val mSliderWrapperID: IntArray
) : RecyclerView(mContext) {

    private lateinit var mInitialEPointF: EPointF
    private lateinit var mEPointFXVal: FloatArray
    private lateinit var mEPointFYVal: FloatArray
    private val mPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGradientPath = Path()
    private val mGradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBezierUtil = PolyBezierPathUtil()
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var isLayout = false

    init {
        setWillNotDraw(false)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    fun setLayoutParams() {
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        this.layoutManager = mLayoutManager
    }

    fun setAdapter() {
        val mAdapter =
            PolylineSliderGraphAdapter(
                this,
                mNumberOfDataPoints,
                mSliderSpacing,
                mSliderAlphaVal,
                mThumbColorFilter,
                mSliderColorFilter,
                mSliderWrapperID,
                context
            )
        this.adapter = mAdapter
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            mViewHeight = height
            mViewWidth = width
            mGradientPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                mViewHeight.toFloat(),
                mGradientColor,
                Color.TRANSPARENT,
                Shader.TileMode.MIRROR
            )
            isLayout = changed
        }
    }

    fun initiatePostSequence() {
        mEPointFYVal = FloatArray(mNumberOfDataPoints)
        mEPointFXVal = FloatArray(mNumberOfDataPoints)
        mPathPaint.style = Paint.Style.STROKE
        mPathPaint.strokeWidth = 5f
        mPathPaint.color = Color.MAGENTA
        mInitialEPointF =
            (this.getChildAt(0) as VerticalSlider).getSliderCoordinates()

        for (addBasePoints in 0 until mNumberOfDataPoints) {
            mEPointFXVal[addBasePoints] = mInitialEPointF.x + (addBasePoints * mSliderSpacing)
            mEPointFYVal[addBasePoints] = mInitialEPointF.y
        }

        this.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
            mPolylineSlider.performViewScroll(scrollX - oldScrollX)
            for (updateBasePoints in 0 until mNumberOfDataPoints) {
                mEPointFXVal[updateBasePoints] =
                    mInitialEPointF.x +
                        (updateBasePoints * mSliderSpacing) -
                        this.computeHorizontalScrollOffset()
            }
            invalidate()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        try {
            if (isLayout) {
                val pathToDraw = mBezierUtil.computePathThroughKnots(
                    mEPointFXVal,
                    mEPointFYVal,
                    mInitialEPointF.y,
                    computeHorizontalScrollRange() - computeHorizontalScrollOffset(),
                    mGradientPath
                )
                canvas?.clipRect(
                    0,
                    0,
                    measuredWidth,
                    measuredHeight
                )
                mGradientPath.lineTo(
                    this.computeHorizontalScrollRange().toFloat(),
                    measuredHeight.toFloat()
                )
                mGradientPath.lineTo(0.0f, mViewHeight.toFloat())
                mGradientPath.lineTo(0.0f, mInitialEPointF.y)
                canvas?.drawPath(mGradientPath, mGradientPaint)
                canvas?.drawPath(pathToDraw, mPathPaint)
            }
        } catch (e: UninitializedPropertyAccessException) {
        }
    }

    internal fun updateSliderParams(mVerticalSlider: VerticalSlider, position: Int) {
        val yVal = mVerticalSlider.getSliderCoordinates().y
        mEPointFYVal[position] = yVal
        invalidate()
    }

    internal fun updateText(position: Int, code: Int) {
        mPolylineSlider.updateText(position, code)
    }

    internal fun displayYAxisProgress(position: Int, progress: Int) {
        mPolylineSlider.displayYAxisProgress(position, progress)
    }

    internal fun changeSliderAlpha(progress: Int, code: Int) {
        mPolylineSlider.changeSliderAlpha(progress, code)
    }
}