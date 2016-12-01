package com.example.denis.sgmapapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ConfigActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    private int grau; // 0 = grau decimal; 1 = grau-minuto decimal; 2 = grau=minuto-segundo
    private int undMedida; // 0 = metro; 1 = pes
    private RadioButton radio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //sharedpreferences do app
        sp = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        //seleciona valores dos radios em ordem com o sharedpreferences
        setDistancia();
        setCoordenada();

        //configurando para que esta classe seja o escutador dos radiosgrupos
        RadioGroup rdC = (RadioGroup) findViewById(R.id.rdGrupoCoord);
        rdC.setOnCheckedChangeListener(this);
        RadioGroup rdD = (RadioGroup) findViewById(R.id.rdGrupoDistancia);
        rdD.setOnCheckedChangeListener(this);
    }


    private void setDistancia(){
        //  grau = sp.getInt("coordenada",0);
        undMedida = sp.getInt("distancia",0);
        if(undMedida == 0){
            RadioButton rdUnidade = (RadioButton) findViewById(R.id.rdMetros);
            rdUnidade.setChecked(true);
        }else{
            RadioButton rdUnidade = (RadioButton) findViewById(R.id.rdPes);
            rdUnidade.setChecked(true);
        }

    }

    private void setCoordenada(){
        grau = sp.getInt("coordenada",0);

        if(grau == 0){
            RadioButton rdCoord = (RadioButton) findViewById(R.id.rdGrau);
            rdCoord.setChecked(true);
        }else if(grau == 1){
            RadioButton rdCoord = (RadioButton) findViewById(R.id.rdGrauMinuto);
            rdCoord.setChecked(true);
        }else{
            RadioButton rdCoord = (RadioButton) findViewById(R.id.rdGrauMinutoSegundo);
            rdCoord.setChecked(true);

            //   RadioGroup rd= (RadioGroup) findViewById(R.id.rdGrupoCoord);


        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        //pega o nome do radioGrupo atraves do id
        String nomeRdGrupo = getResources().getResourceEntryName(radioGroup.getId());
        //pega o nome do radio selecionado atraves do id
        String nomeRadio = getResources().getResourceEntryName(i);
        //Editor sharedpreferences
        spEdit = sp.edit();

        //testa para ver qual radiogrupo disparou o evento
        if(nomeRdGrupo.equals("rdGrupoDistancia") ){

            //salvar no sharedpraferences valor referente ao radio
            switch(nomeRadio){
                case "rdMetros":
                    spEdit.putInt("distancia",0);
                    break;
                case "rdPes":
                    spEdit.putInt("distancia",1);
                    break;
                default:
            }

        }else if(nomeRdGrupo.equals("rdGrupoCoord")){

            //salvar no sharedpraferences valor referente ao radio
            switch(nomeRadio){
                case "rdGrau":
                    spEdit.putInt("coordenada",0);
                    break;
                case "rdGrauMinuto":
                    spEdit.putInt("coordenada",1);
                    break;
                case "rdGrauMinutoSegundo":
                    spEdit.putInt("coordenada",2);
                    break;
                default:
            }
        }
        //comita as alterações
        spEdit.commit();
    }
}
