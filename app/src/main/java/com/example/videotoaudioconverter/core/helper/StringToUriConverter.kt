package com.example.videotoaudioconverter.core.helper

import android.net.Uri
import androidx.core.net.toUri


fun stringToUriConverter(videoString: String): Uri{
    return videoString.toUri()
}