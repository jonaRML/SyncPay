package com.encontac.syncpay

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// funcion para sacar la fecha actual del dispositivo
fun fechaActual(): String{

    val localDateTime = LocalDateTime.now()

    val formatter = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))// formato legible

    val fecha = formatter.format(localDateTime)

    return fecha.substring(0, 1).uppercase() + fecha.substring(1)
}