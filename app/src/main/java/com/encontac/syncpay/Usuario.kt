package com.encontac.syncpay

import java.time.Instant

class Usuario (val nombre : String, val imagen : String, var montoTotal : Int, var montoParcial : Int, val fecha : Instant = Instant.now())