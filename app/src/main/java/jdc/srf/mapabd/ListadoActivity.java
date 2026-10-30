package jdc.srf.mapabd;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListadoActivity extends AppCompatActivity {

    private ListView listViewPuntos;
    private DatabaseHelper dbHelper;
    private List<PuntoInteres> listaPuntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);

        listViewPuntos = findViewById(R.id.listViewPuntos);
        dbHelper = new DatabaseHelper(this);

        cargarLista();

        listViewPuntos.setOnItemClickListener((parent, view, position, id) -> {
            PuntoInteres seleccionado = listaPuntos.get(position);
            Intent intent = new Intent(ListadoActivity.this, DetalleActivity.class);
            intent.putExtra("PUNTO_ID", seleccionado.getId());
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista();
    }

    private void cargarLista() {
        listaPuntos = dbHelper.obtenerTodosPuntos();
        if (listaPuntos.isEmpty()) {
            Toast.makeText(this, "Base de datos vacía.", Toast.LENGTH_SHORT).show();
        }

        List<String> titulos = new ArrayList<>();
        for (PuntoInteres p : listaPuntos) {
            titulos.add(p.getNombre() + " (" + p.getCategoria() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulos);
        listViewPuntos.setAdapter(adapter);
    }
}
