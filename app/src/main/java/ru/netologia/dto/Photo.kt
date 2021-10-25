package ru.netologia.dto

import android.net.Uri

data class Photo(
    val uri: Uri? = null, // uri позваляет нам использовать функцию setImageUri у ImageView

    )
