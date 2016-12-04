package com.example.denis.sgmapapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.widget.TextView;


//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GpsStatus.Listener {

    private LocationManager locManager;
    private LocationProvider locProvider;

    private GoogleMap mMap;
    //api do google
   // private GoogleApiClient mGoogleApiClient;
  //  Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    private SharedPreferences sp;
    private int grau; // 0 = grau decimal; 1 = grau-minuto decimal; 2 = grau=minuto-segundo
    private int undMedida; // 0 = metro; 1 = pes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locProvider = locManager.getProvider(LocationManager.GPS_PROVIDER);


        //sharedpreferences do app
        sp = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        //inicializando api do google
      /*  mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

// Define tipo do mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // desabilita mapas indoor e 3D
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        // cria um ponto com as coordenadas do PA7
        LatLng UnifacsPA7_location = new LatLng(-13.011692, -38.490162);
        // cria um marcador para o ponto
        MarkerOptions UnifacsPA7_markerOptions=new MarkerOptions();
        UnifacsPA7_markerOptions.position(UnifacsPA7_location);
        UnifacsPA7_markerOptions.title("Unifacs PA7");
        UnifacsPA7_markerOptions.snippet("Cursos de Engenharia");
        // adiciona marcador ao mapa
        mMap.addMarker(UnifacsPA7_markerOptions);

        // posiciona o ponto de vista (centraliza em um ponto)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( UnifacsPA7_location, 15.0f ));
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
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

}