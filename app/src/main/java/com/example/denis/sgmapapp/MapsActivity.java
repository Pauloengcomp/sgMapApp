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

    private LocationManager locManager;
    private LocationProvider locProvider;
    private LatLng usuLocal=null;
    private MarkerOptions optMarcador = new MarkerOptions();
    private Marker usuMacador;

    private List<Marker> satsMak = new ArrayList<Marker>();;
    private BitmapDescriptor icon;


    private GoogleMap mMap;
    private Projection p = null;

    SupportMapFragment sMapa;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    //lat e long padrão unifacs pa7
    double defLat=-13.011692 , defLong=-38.490162;
    private int grau; // 0 = grau decimal; 1 = grau-minuto decimal; 2 = grau=minuto-segundo
    private int undMedida; // 0 = metro; 1 = pes
    int h,w;
    private boolean btnSat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sMapa = mapFragment;
        /*
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        h = params.height;
        w=params.width;*/

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locProvider = locManager.getProvider(LocationManager.GPS_PROVIDER);



        //sharedpreferences do app
        sp = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        Button btSat =(Button) findViewById(R.id.btnSat);
        btSat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    Button bt = (Button)findViewById(R.id.btnSat);
                    if(btnSat){

                        btnSat = false;
                        for(Marker s:satsMak){

                            s.remove();
                        }
                        satsMak.clear();
                        bt.setText("Sat-Off");

                    }else{

                        btnSat = true;
                        onGpsStatusChanged(1);
                        bt.setText("Sat-On");

                    }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        p = mMap.getProjection();

// Define tipo do mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // desabilita mapas indoor e 3D
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);

       /*try {
           // cria um ponto com as ultimas coordenadas
           usuLocal = new LatLng(locManager.getLastKnownLocation(locProvider.getName()).getLatitude(), locManager.getLastKnownLocation(locProvider.getName()).getLongitude());
       } catch (SecurityException e) {
           // e.printStackTrace();
           usuLocal = new LatLng(-13.011692, -38.490162);
        }

*/
        if(usuLocal == null){
            defLat  = Double.valueOf(sp.getString("lat","-13.011692"));
            defLong = Double.valueOf(sp.getString("log","-38.490162"));
        }

        usuLocal = new LatLng(defLat, defLong);
        optMarcador.position(usuLocal);
        optMarcador.title("Olha eu!");
        optMarcador.snippet("meio perdido...");
        // adiciona marcador ao mapa
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_nav);
        optMarcador.icon(icon);

        usuMacador = mMap.addMarker(optMarcador);

        // posiciona o ponto de vista (centraliza em um ponto)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( usuLocal, 18.0f ));
        // Configura elementos da interface gráfica
        UiSettings mapUI = mMap.getUiSettings();
        // habilita: pan, zoom, tilt, rotate
        mapUI.setAllGesturesEnabled(true);
        // habilita norte
        mapUI.setCompassEnabled(true);
        // habilta contole do zoom
        mapUI.setZoomControlsEnabled(true);

    }

    @Override
    public void onLocationChanged(Location location) {
        setTxt(location);

        //salva ultima localização valida
        spEdit = sp.edit();
        defLat  = location.getLatitude();
        defLong = location.getLongitude();
        spEdit.putString("lat",String.valueOf(defLat));
        spEdit.putString("log",String.valueOf(defLat));
        spEdit.commit();

        usuLocal = new LatLng(location.getLatitude(),location.getLongitude());
        usuMacador.setPosition(usuLocal);

        if(!btnSat) {
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
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_nav);
        usuMacador.setIcon(icon);
    }

    @Override
    public void onProviderDisabled(String s) {


        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_semsinal);
        usuMacador.setIcon(icon);


    }

    private void setTxt(Location location){

        String tFormat = "Obtendo valores...";
        TextView txt = (TextView)findViewById(R.id.txtInfo);

        undMedida = sp.getInt("distancia",0);
        grau = sp.getInt("coordenada",0);

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

        txt.setText(tFormat);

    }

    @Override
    public void onGpsStatusChanged(int i) {

        plotSats();
       /* GpsStatus gpsStatus;
        Iterable<GpsSatellite> sats;
        //  String txtfull = "";

        try {
            gpsStatus=locManager.getGpsStatus(null);
            sats=gpsStatus.getSatellites();
            if(satsMak == null && p != null){
                for (GpsSatellite sat:sats) {

                    //  Satelites sate = new Satelites(sat.getPrn(),sat.usedInFix(),sat.getSnr(), sat.getAzimuth(), sat.getElevation());
                    // oSatelites.add(sate);
                    // para cada passagem no loop, sat é um objeto GpsSatellite
                    // constante no array sats
                    // txtfull +=  "PRN:" + String.valueOf(sat.getPrn()) + " FIX:" + sat.usedInFix() + " SNR:" + sat.getSnr() + " Azimuth:" + sat.getAzimuth() + " Elevation:" + sat.getElevation() + "\n";


                        Marker m;
                        MarkerOptions optSatMak = new MarkerOptions();

                        satsMak = new ArrayList<Marker>();

                        Point pp = getGPSCoord(sat.getAzimuth(), sat.getElevation());
                        LatLng LLp = convPointToLatLng(pp);// p.fromScreenLocation(pp);
                        // sat.getAzimuth() + " Elevation:" + sat.getElevation()

                        optSatMak.position(LLp);

                        optSatMak.title("PRN: " +String.valueOf(sat.getPrn()));
                        optSatMak.snippet(" FIX:" + sat.usedInFix());

                    if(sat.usedInFix() == false){
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_sat);
                    }else{
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_satfix);
                    }
                        optSatMak.icon(icon);
                        m = mMap.addMarker(optSatMak);
                        satsMak.add(m);
                        // m = mMap.addMarker(optSatMak);
                    }

            }
        } catch (SecurityException e) {
            // txtfull = "Erro: Sem status dos satelites";
            // implementar erro caso não retorne o status do gps
        }*/

    }



    @Override
    protected void onPause() {
        super.onPause();
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
        if (locManager.isProviderEnabled(locProvider.getName())) {
            try {
                locManager.requestLocationUpdates(locProvider.getName(), 30000, 1, this);
                locManager.addGpsStatusListener(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private Point getGPSCoord(float az, float elev){
        int x,y, Xy,Yy;
        double W,H;


        //  View v = (View)findViewById(R.id.activity_creditos);
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.activity_creditos);

        //  H = v.getLayoutParams().height;/*sMapa.getView()*/
        //  W= v.getLayoutParams()/*sMapa.getView().getLayoutParams()*/.width;
        H = sMapa.getView().getHeight();//rel.getHeight();
        W = sMapa.getView().getWidth(); //rel.getWidth();



        Xy = (int)(W/2 * Math.cos(elev) * Math.sin(az));
        Yy = (int)(W/2 * Math.cos(elev) * Math.cos(az));

        x = (int)(Xy + W/2); //2
        y = (int)(- Yy + H/2); //2 no lugar do 4


        return new Point(x,y);
    }


    private void plotSats(){


        try {

            GpsStatus gpsStatus;
            Iterable<GpsSatellite> sats;

            gpsStatus=locManager.getGpsStatus(null);
            sats=gpsStatus.getSatellites();


            if(btnSat) {

                if (!satsMak.isEmpty()) {

                    for (Marker s : satsMak) {

                        s.remove();
                    }
                    satsMak.clear();

                }

                for (GpsSatellite sat : sats) {

                    Marker m;
                    MarkerOptions optSatMak = new MarkerOptions();

                    Point pp = getGPSCoord(sat.getAzimuth(), sat.getElevation());

                    try {
                        LatLng LLp = mMap.getProjection().fromScreenLocation(pp);
                        optSatMak.position(LLp);
                    } catch (Exception e) {
                        //tratamento de erro se não conseguir pegar e converter as coordenadas
                    }

                    optSatMak.title("PRN: " + String.valueOf(sat.getPrn()) + " SNR: " + String.valueOf(sat.getSnr()));
                    optSatMak.snippet(" FIX:" + sat.usedInFix());

                    if (sat.usedInFix() == false) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_sat);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_satfix);
                    }
                    optSatMak.icon(icon);
                    m = mMap.addMarker(optSatMak);
                    satsMak.add(m);

                }

            }


        } catch (SecurityException e) {

            // implementar erro caso não retorne o status do gps
        }
    }


}