package com.example.stockmanager.ui.screens.Movimiento

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.stockmanager.ui.screens.Base.DbHelper
//Crudddd
class MovimientoDao(private val helper: DbHelper) {
    //Create
    fun insertar(m: Movimiento): Long {
        val db = helper.writableDatabase
        val producto = ContentValues().apply {
            put("codigo", m.codigoProducto)
            put("nombre", m.codigoProducto)
        }
        db.insertWithOnConflict("productos", null, producto, SQLiteDatabase.CONFLICT_IGNORE)
        return db.insert("movimientos", null, valores(m))
    }

    // READ
    fun obtenerTodos(): List<Movimiento> {
        val lista = mutableListOf<Movimiento>()
        helper.readableDatabase.rawQuery(
            "SELECT id, codigo_producto, cantidad, tipo, fecha, sincronizado " +
            "FROM movimientos ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Movimiento(
                        c.getLong(0), c.getString(1), c.getInt(2),
                        c.getString(3), c.getString(4), c.getInt(5) == 1
                    )
                )
            }
        }
        return lista
    }
    // UPDATE
    fun actualizar(m: Movimiento): Int {
        val db = helper.writableDatabase

        val producto = ContentValues().apply {
            put("codigo", m.codigoProducto)
            put("nombre", m.codigoProducto)
        }
        db.insertWithOnConflict("productos", null, producto, SQLiteDatabase.CONFLICT_IGNORE)

        return db.update("movimientos", valores(m), "id = ?", arrayOf(m.id.toString()))
    }

    // DELETE
    fun eliminar(id: Long): Int =
        helper.writableDatabase.delete("movimientos", "id = ?", arrayOf(id.toString()))

    fun obtenerPendientes(): List<Movimiento> = obtenerTodos().filter { !it.sincronizado }

    fun marcarSincronizados(ids: List<Long>) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            for (id in ids) {
                db.execSQL("UPDATE movimientos SET sincronizado = 1 WHERE id = ?", arrayOf<Any>(id))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun stockDe(codigo: String): Int {
        return helper.readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(CASE WHEN tipo = 'ENTRADA' THEN cantidad ELSE -cantidad END), 0) " +
            "FROM movimientos WHERE codigo_producto = ?", arrayOf(codigo)
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
    }

    private fun valores(m: Movimiento) = ContentValues().apply {
        put("codigo_producto", m.codigoProducto)
        put("cantidad", m.cantidad)
        put("tipo", m.tipo)
        put("fecha", m.fecha)
        put("sincronizado", if (m.sincronizado) 1 else 0)
    }
}