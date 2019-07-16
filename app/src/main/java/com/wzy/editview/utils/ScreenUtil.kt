package com.wzy.editview.utils


import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 获得屏幕相关的辅助类
 */
class ScreenUtil private constructor() {
    companion object {

        /**
         * 显示软键盘
         */
        fun showSoftInput(context: Context, view: View) {
            (context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                view,
                0
            )
        }

        /**
         * 关闭软键盘
         */
        fun hideSoftInput(context: Context, view: View) {
            val inputMethodManager =
                (context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
