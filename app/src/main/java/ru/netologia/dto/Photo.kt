package ru.netologia.dto

import android.net.Uri
import java.io.File

data class Photo(
    val uri: Uri? = null,
    val file: File? = null
    )
