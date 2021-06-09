package mx.tecnm.tepic.ladm_u4_practica1_autocontestadora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE MENSAJES(MENSAJE VARCHAR(200), deseado VARCHAR(200))")
        db.execSQL("CREATE TABLE LISTANUMEROS(NOMBRE VARCHAR(200), CELULAR VARCHAR(200), deseado VARCHAR(200))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}