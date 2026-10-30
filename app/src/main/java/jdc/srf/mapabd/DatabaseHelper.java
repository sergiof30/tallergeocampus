package jdc.srf.mapabd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GeoCampus.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PUNTOS = "puntos_interes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_CATEGORIA = "categoria";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";
    public static final String COLUMN_FECHA = "fechaRegistro";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PUNTOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_CATEGORIA + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_LATITUD + " REAL, " +
                COLUMN_LONGITUD + " REAL, " +
                COLUMN_FECHA + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUNTOS);
        onCreate(db);
    }

    // Insertar punto de interés
    public boolean insertarPunto(PuntoInteres punto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, punto.getNombre());
        values.put(COLUMN_CATEGORIA, punto.getCategoria());
        values.put(COLUMN_DESCRIPCION, punto.getDescripcion());
        values.put(COLUMN_LATITUD, punto.getLatitud());
        values.put(COLUMN_LONGITUD, punto.getLongitud());
        values.put(COLUMN_FECHA, punto.getFechaRegistro());

        long result = db.insert(TABLE_PUNTOS, null, values);
        db.close();
        return result != -1;
    }

    // Obtener todos los puntos
    public List<PuntoInteres> obtenerTodosPuntos() {
        List<PuntoInteres> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PUNTOS + " ORDER BY " + COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                PuntoInteres punto = new PuntoInteres(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUD)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
                );
                lista.add(punto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // Obtener un punto por ID
    public PuntoInteres obtenerPuntoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PUNTOS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            PuntoInteres punto = new PuntoInteres(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            );
            cursor.close();
            return punto;
        }
        return null;
    }

    // Actualizar registro
    public boolean actualizarPunto(PuntoInteres punto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, punto.getNombre());
        values.put(COLUMN_CATEGORIA, punto.getCategoria());
        values.put(COLUMN_DESCRIPCION, punto.getDescripcion());
        values.put(COLUMN_LATITUD, punto.getLatitud());
        values.put(COLUMN_LONGITUD, punto.getLongitud());

        int rows = db.update(TABLE_PUNTOS, values, COLUMN_ID + "=?", new String[]{String.valueOf(punto.getId())});
        db.close();
        return rows > 0;
    }

    // Eliminar registro
    public boolean eliminarPunto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PUNTOS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
