package com.example.stockmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stockmanager.ui.screens.Base.DbHelper
import com.example.stockmanager.ui.screens.Movimiento.MovimientoDao
import com.example.stockmanager.ui.screens.Movimiento.PantallaInventario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = MovimientoDao(DbHelper(this))
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaInventario(dao)
                }
            }
        }
    }
}