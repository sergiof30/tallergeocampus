package jdc.srf.mapabd;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormularioActivity extends AppCompatActivity{

    private EditText etNombre, etDescripcion;
    private Spinner spCategoria;
    private TextView tvCoordenadas;
    private Button btnObtenerUbicacion, btnGuardar;

    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private double latitudActual = 0.0;
    private double longitudActual = 0.0;
    private int puntoIdEditar = -1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final String[] categorias = {"Laboratorios", "Bibliotecas", "Salas de estudio", "Cafeterías", "Parqueaderos", "Escenarios deportivos", "Oficinas administrativas", "Zonas de encuentro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        spCategoria = findViewById(R.id.spCategoria);
        tvCoordenadas = findViewById(R.id.tvCoordenadas);
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacion);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Configurar Spinner de Categorías
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spCategoria.setAdapter(adapter);

        // Verificar si es edición
        if (getIntent().hasExtra("PUNTO_ID")) {
            puntoIdEditar = getIntent().getIntExtra("PUNTO_ID", -1);
            cargarDatosParaEditar(puntoIdEditar);
        }

    }

    private void solicitarUbicacionGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerCoordenadas();
        }
    }

    private void obtenerCoordenadas() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                    tvCoordenadas.setText("Lat: " + latitudActual + "\nLng: " + longitudActual);
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual. Active el GPS.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void guardarOPunto() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitudActual == 0.0 && longitudActual == 0.0) {
            Toast.makeText(this, "Debe obtener las coordenadas GPS primero.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (puntoIdEditar != -1) {
            PuntoInteres puntoEditado = new PuntoInteres(puntoIdEditar, nombre, categoria, descripcion, latitudActual, longitudActual, fecha);
            if (dbHelper.actualizarPunto(puntoEditado)) {
                Toast.makeText(this, "Punto actualizado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            PuntoInteres nuevoPunto = new PuntoInteres(0, nombre, categoria, descripcion, latitudActual, longitudActual, fecha);
            if (dbHelper.insertarPunto(nuevoPunto)) {
                Toast.makeText(this, "Punto registrado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void cargarDatosParaEditar(int id) {
        PuntoInteres punto = dbHelper.obtenerPuntoPorId(id);
        if (punto != null) {
            etNombre.setText(punto.getNombre());
            etDescripcion.setText(punto.getDescripcion());
            latitudActual = punto.getLatitud();
            longitudActual = punto.getLongitud();
            tvCoordenadas.setText("Lat: " + latitudActual + "\nLng: " + longitudActual);

            for (int i = 0; i < categorias.length; i++) {
                if (categorias[i].equals(punto.getCategoria())) {
                    spCategoria.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerCoordenadas();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
