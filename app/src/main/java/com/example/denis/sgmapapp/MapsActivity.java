package com.example.denis.sgmapapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.icu.text.RelativeDateTimeFormatter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GpsStatus.Listener {

    //Gerenciador de localização
    private LocationManager locManager;
    //Provedor de localização
    private LocationProvider locProvider;
    //Localização atual do dispositivo
    private LatLng usuLocal=null;
    //Opção do marcador do dispositivo
    private MarkerOptions optMarcador = new MarkerOptions();
    //marcador do dispositivo
    private Marker usuMacador;
    //Lista de Marker para gerenciamento depois de adicionados ao mapa
    private List<Marker> satsMak = new ArrayList<Marker>();
    //icones usados na aplicação
    private BitmapDescriptor icon;
    //Mapa do google para pegar o método projection (converte ponto em lnglat)
    private GoogleMap mMap;
    //Fragmento do Mapa para pegar o tamanho da tela depois de inicializado
    SupportMapFragment sMapa;
    //Shared preferences
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    //lat e long padrão unifacs pa7
    double defLat, defLong;
    //variaveis que devem ser definidas na tela config e consultadas pelo shared preferences
    private int grau; // 0 = grau decimal; 1 = grau-minuto decimal; 2 = grau=minuto-segundo
    private int undMedida; // 0 = metro; 1 = pes
    //Teste de botão satelite ativado ou não
    private boolean btnSat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //armazena o fragmento do mapa inicializado em sMapa
        sMapa = mapFragment;

        //pega o serviço e o provedor de localização
        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locProvider = locManager.getProvider(LocationManager.GPS_PROVIDER);



        //pega o sharedpreferences do app
        sp = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        //registra um escutador para o botão do satelite
        Button btSat =(Button) findViewById(R.id.btnSat);
        btSat.setOnClickListener(new View.OnClickListener() {

            //define o escutador
            public void onClick(View v) {
                    // confirma que o evento é do botão satelite
                    Button bt = (Button)findViewById(R.id.btnSat);
                    //se for de ativado-desativado...
                    if(btnSat){
                        // muda variavel btnSat para false, remove os markes do mapa, limpa a lista de marcadores e muda o texto do botão
                        btnSat = false;
                        for(Marker s:satsMak){

                            s.remove();
                        }
                        satsMak.clear();
                        bt.setText("Sat-Off");
                        mMap.setMaxZoomPreference(19.0f);
                        mMap.setMinZoomPreference(1.0f);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usuLocal,18.0f));
                        //se for de desativado-ativado...
                    }else{
                        // muda variavel btnSat para true, chama método que exibe os satelites e muda o texto do botão
                        mMap.setMaxZoomPreference(15.0f);
                        mMap.setMinZoomPreference(1.0f);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usuLocal,1.0f));
                        btnSat = true;
                        plotSats();
                        bt.setText("Sat-On");

                    }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Define tipo do mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // desabilita mapas indoor e 3D
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);

        // Definir LngLat inicial, pegando a ultima salva no shared preferences
        if(usuLocal == null){
            defLat  = Double.valueOf(sp.getString("lat","-13.011692"));
            defLong = Double.valueOf(sp.getString("log","-38.490162"));
        }
        usuLocal = new LatLng(defLat, defLong);

        //setando posição do marcador no mapa
        optMarcador.position(usuLocal);
        //titulo do marcador
        optMarcador.title("Local atual");
        //configura um icone..
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_nav);
        optMarcador.icon(icon);
        // adiciona marcador ao mapa
        usuMacador = mMap.addMarker(optMarcador);

        // posiciona o ponto de vista (centraliza em um ponto)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( usuLocal, 18.0f ));
        // Configura elementos da interface gráfica
        UiSettings mapUI = mMap.getUiSettings();
        // habilita: pan, zoom, tilt, rotate
        mapUI.setAllGesturesEnabled(true);
        mapUI.setMapToolbarEnabled(true);
        // habilita norte
        mapUI.setCompassEnabled(true);
        // habilta controle do zoom
        mapUI.setZoomControlsEnabled(true);

    }

    @Override
    public void onLocationChanged(Location location) {

        //chama metodo que define valores da posição no label em cima do mapa
        setTxt(location);

        //salva ultima localização valida
        spEdit = sp.edit();
        defLat  = location.getLatitude();
        defLong = location.getLongitude();
        spEdit.putString("lat",String.valueOf(defLat));
        spEdit.putString("log",String.valueOf(defLat));
        spEdit.commit();

        //Salva a ultima atualização de localização na variavel da classe
        usuLocal = new LatLng(location.getLatitude(),location.getLongitude());
        //atualiza posição do dispositivo no mapa
        usuMacador.setPosition(usuLocal);
        // se o modo satelite estiver desativado...
        if(!btnSat) {
            //Centraliza o mapa na posição atual caso o modo satelite esteja desativado (facilitar visualização dos mesmos)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(usuLocal));
        }

        // posiciona o ponto de vista (centraliza em um ponto)
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( usuLocal, 19.0f ));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        //se o gps for ativado trocar o icone do marcador dispositivo
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_nav);
        usuMacador.setIcon(icon);
    }

    @Override
    public void onProviderDisabled(String s) {

        //se o gps for desativado trocar o icone do marcador dispositivo
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_semsinal);
        usuMacador.setIcon(icon);


    }

    private void setTxt(Location location){

        //texto padrão sem valores..
        String tFormat = "Obtendo valores...";
        //Acha o textview na activity
        TextView txt = (TextView)findViewById(R.id.txtInfo);
        //atribui valores as variaveis da classe atraves do shred preferences (salvos na tela config)
        undMedida = sp.getInt("distancia",0);
        grau = sp.getInt("coordenada",0);

        // escolhe o texto a ser exibido conforme definição na tela config.
        switch (grau) {
            case 0:
                if(undMedida == 0){
                    // em metros e graus decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_DEGREES) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude()+"m";
                }else{
                    // em pés e graus decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_DEGREES) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude()* 3.28+"ft";
                }
                break;
            case 1:
                if(undMedida == 0){
                    // em metros e graus minutos decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_MINUTES) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude()+"m";
                }else{
                    // em pés e graus minutos decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_MINUTES) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude()* 3.28+"ft";
                }
                break;
            case 2:
                if(undMedida == 0){
                    // em metros e graus minutos segundos decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_SECONDS) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude() +"m";
                }else{
                    // em pés e graus minutos segundos decimais
                    tFormat = "Latitude: " + location.convert(location.getLatitude(),Location.FORMAT_SECONDS) + " Longitude: " + location.convert(location.getLongitude(),Location.FORMAT_DEGREES) + " Altitude: " + location.getAltitude()* 3.28+"ft";
                }
                break;
        }

        //define o texto escolhido no textview
        txt.setText(tFormat);

    }

    @Override
    public void onGpsStatusChanged(int i) {

        //Plotar os satelites a cada atualização
       // plotSats();

    }



    @Override
    protected void onPause() {
        super.onPause();
        //libera o serviço de localização quando o aplicativo não estiver em uso (deixa de ser um escutador)
        if (locManager.isProviderEnabled(locProvider.getName())) {
            try {
                locManager.removeUpdates(this);
                locManager.removeGpsStatusListener(this);
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //recupera o serviço de localização e passa a escutar as notificações
        if (locManager.isProviderEnabled(locProvider.getName())) {
            try {
                locManager.requestLocationUpdates(locProvider.getName(), 30000, 1, this);
                locManager.addGpsStatusListener(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // Algoritmo do professor para converter azimuth e elevação em pontos na tela
    private Point getGPSCoord(float az, float elev){
        int x,y, Xy,Yy;
        double W=0,H=0;

        try {
            //pega o tamanho da tela
            H = sMapa.getView().getHeight();
            W = sMapa.getView().getWidth();
        }catch (Exception e){
            // tratar erro se não conseguir pegar os valores
        }

        //define coordenadas  do satelite em semi-esfera
        Xy = (int)(W/2 * Math.cos(elev) * Math.sin(az));
        Yy = (int)(W/2 * Math.cos(elev) * Math.cos(az));

        //converte para coordenadas em pixel da tela
        x = (int)(Xy + W/2);
        y = (int)(- Yy + H/2);

        //retorna o ponto
        return new Point(x,y);
    }


    private void plotSats(){


        try {

            //cria iterable para os satelites
            Iterable<GpsSatellite> sats;
            //pega os satelites atualizados e coloca no iterable
            GpsStatus gpsStatus;
            gpsStatus=locManager.getGpsStatus(null);
            sats=gpsStatus.getSatellites();

            //confirma se o modo satelite está ativo
            if(btnSat) {

                // confirma remoção dos markes no mapa e limpa  o arraylist se ele já não estiver limpo
                if (satsMak.size() > 0) {

                    for (Marker s : satsMak) {

                        s.remove();

                    }
                   satsMak.clear();
                }


                // faz um for each para tratar e adicionar os satelites um por um no mapa como um marker
                for (GpsSatellite sat : sats) {

                    //o marker e o options marker que serão adicionados
                    Marker m;
                    MarkerOptions optSatMak = new MarkerOptions();
                    // pega o az e elev do satelite e converte para coordenadas em pixel baseados no tamanho da tela
                    Point pp = getGPSCoord(sat.getAzimuth(), sat.getElevation());

                    // tenta converter as coordenadas em LngLat no map e logo depois  adiciona no mapa
                    try {
                        LatLng LLp = mMap.getProjection().fromScreenLocation(pp);
                        optSatMak.position(LLp);
                    } catch (Exception e) {
                        //tratamento de erro se não conseguir pegar e converter as coordenadas
                    }

                    //texto do marcador com prn e snr
                    optSatMak.title("PRN: " + String.valueOf(sat.getPrn()) + " SNR: " + String.valueOf(sat.getSnr()));
                   // optSatMak.snippet(" FIX:" + sat.usedInFix());

                    //escolhe qual icone usar caso o satelite ser usado como fix
                    if (!sat.usedInFix()) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_sat);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_satfix);
                    }
                    optSatMak.icon(icon);

                    //adiciona marcador satelite no mapa
                    m = mMap.addMarker(optSatMak);
                    //adiciona marcador satelite em uma lista para posteriores tratamentos (como remoção no mapa)
                    satsMak.add(m);

                }

            }


        } catch (SecurityException e) {

            // implementar erro caso não retorne o status do gps
        }
    }


}