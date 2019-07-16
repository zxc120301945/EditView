package com.wzy.editview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.wzy.editview.suger.onClickNew
import com.wzy.editview.widgets.EditView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gevEditView.showView("可以输入提醒文案")
        gevEditView.setInputCompleteListener(object : EditView.InputCompleteListener {
            override fun deleteContent() {
                Toast.makeText(this@MainActivity,"删除一个内容",Toast.LENGTH_SHORT).show()
            }

            override fun inputComplete() {
                Toast.makeText(this@MainActivity,"输入完成",Toast.LENGTH_SHORT).show()
            }
        })

        btn.onClickNew {
            Toast.makeText(this@MainActivity,"${gevEditView.getTestInfo()}",Toast.LENGTH_SHORT).show()
        }
    }
}
