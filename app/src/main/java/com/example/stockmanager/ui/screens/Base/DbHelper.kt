package com.example.stockmanager.ui.screens.Base

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "stockmanager.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE productos (codigo TEXT PRIMARY KEY, nombre TEXT NOT NULL)")
        db.execSQL(
            "CREATE TABLE movimientos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "codigo_producto TEXT NOT NULL, " +
            "cantidad INTEGER NOT NULL, " +
            "tipo TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "sincronizado INTEGER NOT NULL DEFAULT 0, " +
            "FOREIGN KEY (codigo_producto) REFERENCES productos(codigo) ON DELETE CASCADE)"
        )
        db.execSQL("CREATE INDEX idx_mov_sincronizado ON movimientos(sincronizado)")
        db.execSQL("CREATE INDEX idx_mov_codigo ON movimientos(codigo_producto)")
    }

    override fun onUpgrade(db: SQLiteDatabase, viejo: Int, nuevo: Int) {
        db.execSQL("DROP TABLE IF EXISTS movimientos")
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }
}