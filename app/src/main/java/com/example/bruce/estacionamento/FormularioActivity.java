package com.example.bruce.estacionamento;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bruce.estacionamento.DB.CarroDAO;
import com.example.bruce.estacionamento.DB.Carro;
import com.example.bruce.estacionamento.DB.FormularioHelper;

public class FormularioActivity extends AppCompatActivity {
    private Carro carro;
    private FormularioHelper fHelper;
    private Button btnDeletar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        fHelper = new FormularioHelper(FormularioActivity.this);
        Button button = (Button) findViewById(R.id.btnCadastrar);
        carro = (Carro) getIntent().getSerializableExtra("carroSelecionado");
        btnDeletar = findViewById(R.id.btnDeletar);

        if(carro == null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carro = fHelper.pegaCarroDoFormulario();
                    CarroDAO dao = new CarroDAO(FormularioActivity.this);
                    dao.insere(carro);
                    dao.close();
                    finish();
                }
            });
        }else{
            button.setText("Modificicar");
            fHelper.colocaNoFormulario(carro);
            btnDeletar.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CarroDAO dao = new CarroDAO(FormularioActivity.this);
                    carro = fHelper.pegaCarroDoFormulario();
                    dao.alterar(carro);
                    dao.close();
                    finish();
                }
            });

            btnDeletar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CarroDAO dao = new CarroDAO(FormularioActivity.this);
                    carro = fHelper.pegaCarroDoFormulario();
                    dao.delete(carro);
                    dao.close();
                    finish();
                }
            });
        }
    }
}
