package ru.netologia.dto

import android.net.Uri
import java.io.File

data class Photo(
    val uri: Uri? = null, // uri позваляет нам использовать функцию setImageUri у ImageView
    val file: File? = null // файл даст нам загрузить выбранный файл на сервер
    )
