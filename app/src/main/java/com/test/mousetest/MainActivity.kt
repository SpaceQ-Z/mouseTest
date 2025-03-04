package com.test.mousetest

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.test.mousetest.ui.theme.MouseTestTheme

class MainActivity : ComponentActivity() {
    private lateinit var textView: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MouseTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column(modifier = modifier.fillMaxSize()) {
            AndroidView(factory = {
                View.inflate(context, R.layout.activity_main, null).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    findViewById<Button>(R.id.button_show_mouse).setOnClickListener {
                        Log.i("PointerCapture", "Requesting pointer capture")
                        rootView.releasePointerCapture()
                    }
                    findViewById<Button>(R.id.button_hide_mouse).setOnClickListener {
                        Log.i("PointerCapture", "Releasing pointer capture")
                        rootView.isFocusable = true
                        rootView.defaultFocusHighlightEnabled = false
                        rootView.requestPointerCapture()
                    }
                    textView = findViewById(R.id.text_view)
                    rootView.isFocusable = true
                    rootView.isFocusableInTouchMode = true
                    rootView.requestFocus()

                    //当显示鼠标时会通过hover收到回调
                    rootView.setOnHoverListener(object : View.OnHoverListener {
                        override fun onHover(v: View?, event: MotionEvent?): Boolean {
                            val x = event?.x
                            val y = event?.y
                            val absoluteX = event?.rawX
                            val absoluteY = event?.rawY
                            val relativeX = event?.getAxisValue(MotionEvent.AXIS_RELATIVE_X)
                            val relativeY = event?.getAxisValue(MotionEvent.AXIS_RELATIVE_Y)
                            textView.text = "鼠标悬停时坐标  - xy: ($x, $y), rawXY: ($absoluteX, $absoluteY), relativeXY: (x:$relativeX, y:$relativeY)"
                            return true
                        }
                    })

                    //当隐藏鼠标时会通过CapturedPointer收到回调
                    rootView.setOnCapturedPointerListener { _, event ->
                        val x = event.x
                        val y = event.y
                        val absoluteX = event.rawX
                        val absoluteY = event.rawY
                        val relativeX = event.getAxisValue(MotionEvent.AXIS_RELATIVE_X)
                        val relativeY = event.getAxisValue(MotionEvent.AXIS_RELATIVE_Y)
                        textView.text = "隐藏鼠标时偏移量  - xy: ($x, $y), rawXY: ($absoluteX, $absoluteY), relativeXY: (x:$relativeX, y:$relativeY)"
                        true
                    }
                }
            })
        }
    }
}