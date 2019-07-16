package com.wzy.editview.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.wzy.editview.R
import com.wzy.editview.suger.onClickNew
import com.wzy.editview.suger.show
import com.wzy.editview.utils.DensityUtil
import com.wzy.editview.utils.ScreenUtil
import kotlinx.android.synthetic.main.view_editview.view.*
import org.jetbrains.anko.sdk23.listeners.textChangedListener
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor

class EditView(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var mTextViewList = arrayListOf<TextView>()
    private var mTextInfoList = arrayListOf<String>()
    private var mHintInfoList = arrayListOf<String>()

    private var mTextLength: Int = 0
    private var mTextBackDrawable: Drawable? = null
    private var mTextSize: Float = 0f
    private var mTextColor: Int = 0
    private var mTextHintColor: Int = 0


    init {
        context?.let {
            LayoutInflater.from(it).inflate(R.layout.view_editview, this)
        }
        initAttrs(attrs)
        initViews()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EditView)
        ta?.let {
            mTextLength = it.getInt(R.styleable.EditView_gevTextLength, 0)
            mTextHintColor = it.getInt(R.styleable.EditView_gevTextHintColor, 0)
            mTextColor = it.getInt(R.styleable.EditView_gevTextColor, 0)
            mTextSize = it.getFloat(R.styleable.EditView_gevTextSize, 0f)
            mTextBackDrawable = it.getDrawable(R.styleable.EditView_gevTextBackDrawable)
        }
        ta?.recycle()
    }

    private fun initViews() {
        isClickable = true
        etEditView.isCursorVisible = false
        etEditView.background = null
        etEditView.gravity = Gravity.CENTER
        etEditView.singleLine = true
        etEditView.maxEms = mTextLength
        etEditView.textChangedListener {
            afterTextChanged {
                if (!TextUtils.isEmpty(it.toString())) {
                    if (mTextInfoList.size < mTextLength) {
                        try {
                            if (it.toString().toCharArray().size > 1) {
                                //剩余未输入的字符个数
                                var count = 0
                                if (mTextInfoList.size <= mTextLength) {
                                    count = mTextLength - mTextInfoList.size
                                }
                                if (it.toString().toCharArray().size > count) {
                                    if (count > 0) {
                                        for (index in 0 until count) {
                                            mTextInfoList.add(it.toString().toCharArray()[index].toString())
                                        }
                                    }
                                } else {
                                    it.toString().toCharArray().forEach {
                                        mTextInfoList.add(it.toString())
                                    }
                                }
                            } else {
                                mTextInfoList.add(it.toString())
                            }
                            etEditView.setText("")
                            mTextInfoList.forEachIndexed { index, s ->
                                if (index < mTextViewList.size) {
                                    mTextViewList[index].textColor = mTextColor
                                    mTextViewList[index].text = "$s"
                                }
                            }
                            if (mTextInfoList.size >= mTextLength) {
                                mInputCompleteListener?.inputComplete()
                            }
                        } catch (e: Exception) {
                            Log.e("error", "标签输入框解析字符异常：${it.toString()}，error：${e.printStackTrace()}")
                        }
                    } else {
                        etEditView.setText("")
                    }
                }
            }
        }

        etEditView.setDelKeyEventListener {
            onKeyDelete()
        }

        llItemRootView?.let {
            if (it.childCount > 0) {
                it.removeAllViews()
            }
        }

        var index = 0
        while (index < mTextLength) {
            val textview = TextView(context)
            textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            textview.gravity = Gravity.CENTER
            if (mTextBackDrawable == null) {
                textview.setBackgroundResource(R.drawable.shape_bg_rectangle_golden)
            } else {
                textview.setBackgroundDrawable(mTextBackDrawable)
            }
            if (!mTextInfoList.isEmpty() && !TextUtils.isEmpty(mTextInfoList[index])) {
                textview.textColor = mTextColor
                textview.text = "${mTextInfoList[index]}"
            } else if (!mHintInfoList.isEmpty() && !TextUtils.isEmpty(mHintInfoList[index])) {
                textview.textColor = mTextHintColor
                textview.text = "${mHintInfoList[index]}"
            } else {
                textview.text = ""
            }
            textview.requestLayout()

            val textParams =
                LinearLayout.LayoutParams(DensityUtil.dp2px(context, 40f), DensityUtil.dp2px(context, 40f), 1F)
            textParams.setMargins(DensityUtil.dp2px(context, 5f), 0, 0, 0)
            llItemRootView.addView(textview, textParams)

            mTextViewList.add(index, textview)
            index++
        }
        this.onClickNew {
            forceInputViewGetFocus()
        }
    }

    fun showView(hintMsg: String) {
        show()
        mTextInfoList.clear()
        if (!mTextViewList.isEmpty()) {
            mTextViewList.forEach {
                it.text = ""
            }
        }

        if (!TextUtils.isEmpty(hintMsg)) {
            mHintInfoList.clear()
            hintMsg.toCharArray().forEachIndexed { index, c ->
                if (index >= mTextLength) {
                    return@forEachIndexed
                }
                mHintInfoList.add(index, "$c")
                if (!mTextViewList.isEmpty()) {
                    mTextViewList[index].textColor = mTextHintColor
                    mTextViewList[index].text = "$c"
                }
            }
        }
    }

    private fun forceInputViewGetFocus() {
        etEditView.setFocusable(true)
        etEditView.setFocusableInTouchMode(true)
        etEditView.requestFocus()
        ScreenUtil.showSoftInput(context,etEditView)
    }

    fun onKeyDelete() {
        if (mTextInfoList.isEmpty()) {
            return
        }
        try {
            mTextInfoList.removeAt(mTextInfoList.size - 1)
            if (!mHintInfoList.isEmpty()) {
                if (!mTextViewList.isEmpty()) {
                    var s = ""
                    if (mHintInfoList.size > mTextInfoList.size) {
                        s = "${mHintInfoList[mTextInfoList.size]}"
                    }
                    if (mTextViewList.size > mTextInfoList.size) {
                        mTextViewList[mTextInfoList.size].textColor = mTextHintColor
                        mTextViewList[mTextInfoList.size].text = "$s"
                    }
                }
            } else {
                if (!mTextViewList.isEmpty()) {
                    if (mTextViewList.size > mTextInfoList.size) {
                        mTextViewList[mTextInfoList.size].textColor = mTextHintColor
                        mTextViewList[mTextInfoList.size].text = ""
                    }
                }
            }
            if (mTextInfoList.size < mTextLength) {
                mInputCompleteListener?.deleteContent()
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("error", "输入框数组越界，error：${e.printStackTrace()}")
        }
    }

    fun getTestInfo(): String {
        if (!mTextInfoList.isEmpty()) {
            val buffer = StringBuffer()
            mTextInfoList.forEach {
                buffer.append(it)
            }
            return buffer.toString()
        }
        return ""
    }

    fun clearAll() {
        mHintInfoList.clear()
        mTextInfoList.clear()
        closeKeyBoard()
    }

    fun closeKeyBoard() {
        ScreenUtil.hideSoftInput(context,etEditView)
    }

    private var mInputCompleteListener: InputCompleteListener? = null

    fun setInputCompleteListener(inputCompleteListener: InputCompleteListener?) {
        this.mInputCompleteListener = inputCompleteListener
    }

    interface InputCompleteListener {
        fun inputComplete()

        fun deleteContent()
    }

}