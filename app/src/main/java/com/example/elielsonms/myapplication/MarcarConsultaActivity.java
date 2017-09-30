package com.example.elielsonms.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarcarConsultaActivity extends AppCompatActivity {

    private static int OBTER_HORARIOS = 0;
    private static int MARCAR_CONSULTA = 1;
    private Map<Integer,Long> mapIdHorarioas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marcar_consulta);

        System.out.println("Calling service "+OBTER_HORARIOS);
        carregarHorarios();

        ((Button)findViewById(R.id.btnMarcarConsulta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((EditText)findViewById(R.id.editTextCPF)).getText().length() < 2){
                    exibirMsg("Favor informar o cpf com no minimo 2 digitos");
                    return;
                }
                if(((EditText)findViewById(R.id.editTextAnimal)).getText().length() < 2){
                    exibirMsg("Favor informar o nome do animal da consulta");
                    return;
                }
                System.out.println("Calling service "+MARCAR_CONSULTA);
                exibirMsg("Marcando a Consulta");
                PendingIntent pendingResult = createPendingResult(MARCAR_CONSULTA, new Intent(), 0);
                Intent intent = new Intent(getApplicationContext(), FetchSoapService.class);
                intent.setAction(FetchSoapService.ACTION_MARCAR_CONSULTA);
                intent.putExtra(FetchSoapService.PEDIDO, pendingResult);

                intent.putExtra("cpf", ((EditText)findViewById(R.id.editTextCPF)).getText().toString());
                intent.putExtra("animal", ((EditText)findViewById(R.id.editTextAnimal)).getText().toString());
                intent.putExtra("idHorario",mapIdHorarioas.get(((Spinner)findViewById(R.id.spinnerHorarios)).getSelectedItemPosition()-1));
                startService(intent);
            }
        });
    }

    private void carregarHorarios() {
        PendingIntent pendingResult = createPendingResult(OBTER_HORARIOS, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), FetchSoapService.class);
        intent.setAction(FetchSoapService.ACTION_HORARIOS);
        intent.putExtra(FetchSoapService.PEDIDO, pendingResult);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Receiving PendingIntent");
        super.onActivityResult(requestCode, resultCode, data);
        if(OBTER_HORARIOS == requestCode) {
            mapIdHorarioas = new HashMap<Integer,Long>();
            List<String> resultado = new ArrayList<String>();
            resultado.add("Selecione um horario disponível");
            if(data.getSerializableExtra(FetchSoapService.RESULTADO) == null) {
                exibirMsg("Não foi possivel ober os horarios");
            }else {
                List<HorarioDTO> resultados = (List<HorarioDTO>)data.getSerializableExtra(FetchSoapService.RESULTADO);
                for(int i =0; i<resultados.size();i++ ){
                    mapIdHorarioas.put(i,resultados.get(i).getIdHorario());
                    resultado.add(resultados.get(i).getHorario() + " " + resultados.get(i).getMedico());
                }
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,resultado);
            Spinner horarios = (Spinner)findViewById(R.id.spinnerHorarios);
            horarios.setAdapter(adapter);
            exibirMsg("");
        }
        if(MARCAR_CONSULTA == requestCode) {
            String msg = (String)data.getSerializableExtra(FetchSoapService.RESULTADO);
            if(msg.contains("sucesso")){
                carregarHorarios();
            }
            exibirMsg(msg);
        }
    }

    public void exibirMsg(String msg){
        ((TextView)findViewById(R.id.textViewMsg)).setText(msg);
    }
}
