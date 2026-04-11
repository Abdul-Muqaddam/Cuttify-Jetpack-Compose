package com.example.videotoaudioconverter.presentation.output_screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File

object FileOpener {

    fun openFolder(context: Context, folderPath: String) {
        val folder = File(folderPath)

        // Create folder if it doesn't exist yet
        if (!folder.exists()) folder.mkdirs()

        try {
            val encodedPath = folderPath
                .removePrefix("/storage/emulated/0/")
                .replace("/", "%2F")

            val uri = Uri.parse(
                "content://com.android.externalstorage.documents/document/primary%3A$encodedPath"
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "vnd.android.document/directory")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No file manager found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open folder", Toast.LENGTH_SHORT).show()
        }
    }
}
