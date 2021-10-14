package ru.netologia.dto

import ru.netologia.enumeration.AttachmentType

data class Attachment (
    val url: String,
    val type: AttachmentType
    )


