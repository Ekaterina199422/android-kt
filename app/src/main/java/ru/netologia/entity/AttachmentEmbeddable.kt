package ru.netologia.entity

import ru.netologia.dto.Attachment
import ru.netologia.enumeration.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}