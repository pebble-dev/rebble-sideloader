package io.rebble.sideload


import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data: Uri? = intent?.data

        // Figure out what to do based on the intent type
        if (intent?.type?.equals("application/octet-stream") == true) {
            handlePBW(intent) // Handle pbw being sent
        } else {
            tellUserCouldntOpenFile()
        }
    }
    fun handlePBW(intent: Intent) {
        val uri: Uri? = intent.data
        if (uri == null) {
            tellUserCouldntOpenFile()
            return
        }
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
        attemptForward(uri)
    }

    private fun tellUserCouldntOpenFile() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show()
    }

    fun attemptForward(fileURI: Uri?) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val sendIntent = Intent()
        sendIntent.component = ComponentName("com.getpebble.android.basalt", "com.getpebble.android.main.activity.MainActivity")
        sendIntent.setPackage("com.getpebble.android.basalt")
        sendIntent.action = "android.intent.action.VIEW"
        sendIntent.data = fileURI
        startActivity(sendIntent)
    }
}

