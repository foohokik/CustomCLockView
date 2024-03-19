package com.example.clockcustomview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.icu.util.Calendar
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Float.min
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.hours

class ClockView
@JvmOverloads
constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {


    init {

        setAttrs()

    }

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = frameColor
    }

    private val circleBackGroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = circleBackGroundColor
    }

    private val scalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        color = scaleColor
    }

    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = numbersColor
    }

    private val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        color = handColor
    }

    var frameColor = 0
    var circleBackGroundColor = 0
    var scaleColor = 0
    var handColor = 0
    var numbersColor = 0
    private var centerPoint = PointF()
    private var scalePoint = PointF(0f, 0f)
    private var frameRadius = 0f
    private var contentRadius = 0f
    private var mWidth = 0
    private var mHeght = 0
    private var textSize = 0f
    private val rect = Rect()
    private var hour = 0
    private var minute = 0
    private var second = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingBottom + paddingTop
        setMeasuredDimension(
            calculateDefaultSize(desiredWidth, widthMeasureSpec),
            calculateDefaultSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w - paddingStart - paddingEnd
        mHeght = h - paddingTop - paddingBottom
        centerPoint = PointF(
            (mWidth / 2 + paddingStart).toFloat(),
            (mHeght / 2 + paddingTop).toFloat()
        )

    }


    override fun onDraw(canvas: Canvas) {
        canvas.translate(centerPoint.x, centerPoint.y)
        frameRadius = min(mWidth / 2f, mHeght / 2f)
        framePaint.strokeWidth = frameRadius / 7f
        contentRadius = frameRadius - framePaint.strokeWidth.toInt()
        textSize = contentRadius/4f

        drawCircles(canvas)
        drawBackCircle(canvas)
        drawScaleClock(canvas)
        drawNumbers(canvas)
        drawHands(canvas)
        drawCenterPoint(canvas)
        postInvalidateDelayed(500)
        postInvalidate()
    }

    private fun calculateDefaultSize(desiredSize: Int, measureSpec: Int): Int {
        val size = MeasureSpec.getSize(measureSpec)
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> desiredSize.coerceAtMost(size)
            else -> desiredSize
        }
    }

    private fun drawCircles(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, contentRadius, framePaint)
    }

    private fun drawBackCircle(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, contentRadius, circleBackGroundPaint)
    }

    private fun drawScaleClock(canvas: Canvas) {
        scalePoint.y = 0.9f * contentRadius

        for (i in 0..59) {
            if (i % 5 == 0) {
                scalePaint.strokeWidth = frameRadius / 30f
                scalePaint.color = Color.BLACK
                canvas.drawPoint(scalePoint.x, scalePoint.y, scalePaint)

            } else {
                scalePaint.strokeWidth = frameRadius / 60f
                scalePaint.color = Color.BLACK
                canvas.drawPoint(scalePoint.x, scalePoint.y, scalePaint)
            }
            canvas.rotate(6f)
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        numberPaint.textSize = textSize

        for (number in 1..12) {
            val num = number.toString()
            numberPaint.getTextBounds(num, 0, num.length, rect)
            val angle = (Math.PI / 6 * (number - 3)).toFloat()
            val x = (0f + cos(angle) * 0.8f * contentRadius - rect.width() / 2)
            val y = (0f + sin(angle) * 0.8f * contentRadius + rect.height() / 2)
            canvas.drawText(num, x, y, numberPaint)
        }
    }


    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        hour = calendar[Calendar.HOUR]
        hour = if (hour > 12) hour - 12 else hour
        minute = calendar[Calendar.MINUTE]
        second = calendar[Calendar.SECOND]

        drawHourHand(canvas, ((hour + minute / 60.0) * 5f).toFloat())
        drawMinuteHand(canvas, minute.toFloat())
        drawSecondHand(canvas, second.toFloat())
    }

    private fun drawHourHand(canvas: Canvas, pos: Float) {
        handPaint.strokeWidth = 0.06f * frameRadius
        val angle = (Math.PI * pos / 30 - Math.PI / 2).toFloat()
        canvas.drawLine(
            (cos(angle) * -frameRadius * 0.1f),
            (sin(angle) * -frameRadius * 0.1f),
            (cos(angle) * frameRadius * 0.35f),
            (sin(angle) * frameRadius * 0.35f),
            handPaint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, pos: Float) {
        handPaint.strokeWidth = 0.03f * frameRadius
        val angle = (Math.PI * pos / 30 - Math.PI / 2).toFloat()
        canvas.drawLine(
            (cos(angle) * -frameRadius * 0.15f),
            (sin(angle) * -frameRadius * 0.15f),
            (cos(angle) * frameRadius * 0.45f),
            (sin(angle) * frameRadius * 0.45f),
            handPaint
        )
    }

    private fun drawSecondHand(canvas: Canvas, pos: Float) {
        handPaint.strokeWidth = 0.015f * frameRadius
        val angle = (Math.PI * pos / 30 - Math.PI / 2).toFloat()
        canvas.drawLine(
            (cos(angle) * -frameRadius * 0.25f),
            (sin(angle) * -frameRadius * 0.25f),
            (cos(angle) * frameRadius * 0.6f),
            (sin(angle) * frameRadius * 0.6f),
            handPaint
        )
    }

    private fun drawCenterPoint(canvas: Canvas) {
        scalePaint.strokeWidth = frameRadius / 10f
        scalePaint.color = Color.BLACK
        canvas.drawPoint(0f, 0f, scalePaint)
    }


    @SuppressLint("ResourceAsColor", "Recycle", "CustomViewStyleable")
    private fun setAttrs() {
        val setXmlAttributes = context.obtainStyledAttributes(attrs, R.styleable.ClockCustomView)

        frameColor = setXmlAttributes.getColor(
            R.styleable.ClockCustomView_frameColor,
            ContextCompat.getColor(context, DEFAULT_COLOR)
        )

        circleBackGroundColor = setXmlAttributes.getColor(
            R.styleable.ClockCustomView_circleBackGroundColor,
            ContextCompat.getColor(context, DEFAULT_BACKGROUND_COLOR)
        )

        scaleColor = setXmlAttributes.getColor(
            R.styleable.ClockCustomView_scaleColor,
            ContextCompat.getColor(context, DEFAULT_COLOR)
        )

        numbersColor = setXmlAttributes.getColor(
            R.styleable.ClockCustomView_numbersColor,
            ContextCompat.getColor(context, DEFAULT_COLOR)
        )

        handColor = setXmlAttributes.getColor(
            R.styleable.ClockCustomView_handColor,
            ContextCompat.getColor(context, DEFAULT_COLOR)
        )

    }

    companion object {
        private val DEFAULT_COLOR = R.color.black
        private val DEFAULT_BACKGROUND_COLOR = R.color.lightGray

    }


}



