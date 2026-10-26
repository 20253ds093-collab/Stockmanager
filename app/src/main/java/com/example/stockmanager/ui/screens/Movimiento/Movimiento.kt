package com.example.stockmanager.ui.screens.Movimiento

data class Movimiento(
    val id: Long = 0,
    val codigoProducto: String,
    val cantidad: Int,
    val tipo: String,
    val fecha: String,
    val sincronizado: Boolean = false
)