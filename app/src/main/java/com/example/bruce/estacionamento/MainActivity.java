package com.example.bruce.estacionamento;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.bruce.estacionamento.DB.CarroDAO;
import com.example.bruce.estacionamento.DB.Carro;

public class MainActivity extends AppCompatActivity {
    private ListView listaCarros;
    private ArrayAdapter<Carro> adapter;
    private Carro carroSelecionado;

    private EditText cuposs;
    private TextView disponibless;
    private Button aceptarr;
    private TextView ocupadoo;

    String total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CarroDAO dao = new CarroDAO(MainActivity.this);
        cuposs = (EditText) findViewById(R.id.cupos);
        disponibless = (TextView) findViewById(R.id.disponible);
        ocupadoo = (TextView) findViewById(R.id.ocupado);
        listaCarros = (ListView) findViewById(R.id.lista_carros);
        aceptarr = (Button) findViewById(R.id.aceptar);

        registerForContextMenu(listaCarros);

        aceptarr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cuposs.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Espacio Vacio no permitido!",Toast.LENGTH_SHORT).
                            show();
                }else {
                    disponibless.setText(cuposs.getText());
                    cuposs.setText("");
                    cuposs.setFocusable(false);
                    cuposs.setEnabled(false);
                    aceptarr.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Cupos Establecidos!",Toast.LENGTH_SHORT).
                            show();
                }
            }
        });

        listaCarros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent edicao = new Intent(MainActivity.this, FormularioActivity.class);
                edicao.putExtra("carroSelecionado", (Carro) listaCarros.getItemAtPosition(position));
                startActivity(edicao);
            }
        });

        listaCarros.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                carroSelecionado = (Carro) parent.getItemAtPosition(position);
                return false;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        MenuItem retirar = menu.add("Retirar Carro");

        retirar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Retirar carro del estacionamento")
                        .setMessage("Deseja mesmo retirar")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CarroDAO dao = new CarroDAO(MainActivity.this);
                                dao.retirarCarroDoEstacionamento(carroSelecionado);
                                dao.close();
                                carregaListaEstacionados();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_novo:
                int dato1 = Integer.parseInt(disponibless.getText().toString());
                int dato2 = Integer.parseInt(ocupadoo.getText().toString());
                if (dato2 >= dato1){
                    Toast.makeText(getApplicationContext(),"No hay cupos Disponibles!",Toast.LENGTH_SHORT).
                            show();
                }else{
                    Intent intent = new Intent(MainActivity.this,FormularioActivity.class);
                    startActivity(intent);
                    return false;
                }
            case R.id.menu_consultar:
                Intent consultar = new Intent(MainActivity.this, ConsultarActivity.class);
                startActivity(consultar);
                return false;
            case R.id.menu_rest:
                disponibless.setText("");
                cuposs.setText("");
                cuposs.setFocusable(true);
                cuposs.setEnabled(true);
                aceptarr.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Reinicio Completado!",Toast.LENGTH_SHORT).
                        show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void carregaListaEstacionados(){

        final CarroDAO dao = new CarroDAO(MainActivity.this);
        List<Carro> carros = dao.getListEstacionados();
        dao.close();

        ListaCarrosAdapter adpt = new ListaCarrosAdapter(carros, MainActivity.this);
        listaCarros.setAdapter(adpt);
        total= String.valueOf(listaCarros.getCount());
        ocupadoo.setText(total);
    }



    @Override
    public void onResume(){
        super.onResume();
        this.carregaListaEstacionados();
    }
}
