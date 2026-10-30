package jdc.srf.mapabd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleActivity extends AppCompatActivity {

    private TextView tvNombre, tvCategoria, tvDescripcion, tvCoordenadas, tvFecha;
    private Button btnEditar, btnEliminar;
    private DatabaseHelper dbHelper;
    private int puntoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);

        dbHelper = new DatabaseHelper(this);

        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvCategoria = findViewById(R.id.tvDetalleCategoria);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvCoordenadas = findViewById(R.id.tvDetalleCoordenadas);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        puntoId = getIntent().getIntExtra("PUNTO_ID", -1);

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActivity.this, FormularioActivity.class);
            intent.putExtra("PUNTO_ID", puntoId);
            startActivity(intent);
            finish();
        });

        btnEliminar.setOnClickListener(v -> {
            if (dbHelper.eliminarPunto(puntoId)) {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDetalle();
    }

    private void cargarDetalle() {
        PuntoInteres punto = dbHelper.obtenerPuntoPorId(puntoId);
        if (punto != null) {
            tvNombre.setText(punto.getNombre());
            tvCategoria.setText("Categoría: " + punto.getCategoria());
            tvDescripcion.setText("Descripción: " + punto.getDescripcion());
            tvCoordenadas.setText("Latitud: " + punto.getLatitud() + "\nLongitud: " + punto.getLongitud());
            tvFecha.setText("Fecha de Registro: " + punto.getFechaRegistro());
        }
    }
}
