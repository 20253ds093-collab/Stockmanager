package com.example.stockmanager.ui.screens.Movimiento

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.stockmanager.utils.hayInternet
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaInventario(dao: MovimientoDao) {
    val context = LocalContext.current
    var codigo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var esEntrada by remember { mutableStateOf(true) }
    var editando by remember { mutableStateOf<Movimiento?>(null) }
    var movimientos by remember { mutableStateOf(dao.obtenerTodos()) }
    var mensaje by remember { mutableStateOf("") }

    fun refrescar() { movimientos = dao.obtenerTodos() }

    fun guardar() {
        val cant = cantidad.toIntOrNull()
        if (codigo.isBlank() || cant == null || cant <= 0) {
            mensaje = "Escribe un código y una cantidad mayor a 0"
            return
        }
        val tipo = if (esEntrada) "ENTRADA" else "SALIDA"
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val previo = editando
        if (previo == null) {
            dao.insertar(Movimiento(codigoProducto = codigo.trim(), cantidad = cant, tipo = tipo, fecha = fecha))
        } else {
            dao.actualizar(previo.copy(codigoProducto = codigo.trim(), cantidad = cant, tipo = tipo, sincronizado = false))
            editando = null
        }
        mensaje = "Stock de ${codigo.trim()}: ${dao.stockDe(codigo.trim())}"
        codigo = ""
        cantidad = ""
        refrescar()
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun sincronizar() {
        val pendientes = dao.obtenerPendientes()
        if (pendientes.isEmpty()) {
            mensaje = "No hay movimientos pendientes"
        } else if (!hayInternet(context)) {
            mensaje = "Sin conexión: se enviarán después"
        } else {
            dao.marcarSincronizados(pendientes.map { it.id })
            mensaje = "${pendientes.size} movimientos sincronizados"
            refrescar()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = codigo, onValueChange = { codigo = it },
            label = { Text("Código del producto") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cantidad, onValueChange = { cantidad = it },
            label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = esEntrada, onClick = { esEntrada = true })
            Text("Entrada")
            Spacer(Modifier.width(16.dp))
            RadioButton(selected = !esEntrada, onClick = { esEntrada = false })
            Text("Salida")
        }
        Button(onClick = { guardar() }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar movimiento")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { sincronizar() }, modifier = Modifier.fillMaxWidth()) {
            Text("Sincronizar con el servidor")
        }
        Text(mensaje, modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(movimientos) { m ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${m.codigoProducto} | ${m.tipo} ${m.cantidad} | ${m.fecha}")
                    Text(if (m.sincronizado) "sincronizado" else "pendiente")
                    Row {
                        TextButton(onClick = {
                            editando = m
                            codigo = m.codigoProducto
                            cantidad = m.cantidad.toString()
                            esEntrada = m.tipo == "ENTRADA"
                        }) { Text("Editar") }
                        TextButton(onClick = {
                            dao.eliminar(m.id)
                            refrescar()
                        }) { Text("Eliminar") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}