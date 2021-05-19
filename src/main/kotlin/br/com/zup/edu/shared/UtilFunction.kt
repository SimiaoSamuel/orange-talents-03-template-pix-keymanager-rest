package br.com.zup.edu.shared

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Timestamp.toLocalDateTime(): LocalDateTime {
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
}