package io.rebble.sideload


import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    private val OPEN_REQUEST_CODE = 41;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Figure out what to do based on the intent type
        if (intent?.type?.equals("application/octet-stream") == true) {
            handlePBW(intent) // Handle pbw being sent
        }

        val fileButton: Button = findViewById(R.id.file_select)
        fileButton.setOnClickListener { chooseFile() }
    }

    private fun chooseFile() {
        val type = "*/*"
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = type
        startActivityForResult(Intent.createChooser(i, "select file"), OPEN_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            handlePBW(data)
        }
    }

    private fun handlePBW(intent: Intent) {
        val uri: Uri? = intent.data
        if (uri == null) {
            tellUserCouldntOpenFile()
            return
        }
        attemptForward(uri)
    }

    private fun tellUserCouldntOpenFile() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show()
    }

    private fun attemptForward(fileURI: Uri?) {
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
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.data = fileURI
        startActivity(sendIntent)
    }
}

