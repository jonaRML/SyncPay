package com.encontac.syncpay

import java.time.Instant



class Persona (val nombre : String, var montoTotal : Int, var montoParcial : Int, val fecha : Instant = Instant.now()) {

}

