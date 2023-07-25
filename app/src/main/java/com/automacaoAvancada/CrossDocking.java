package com.automacaoAvancada;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.automacaoAvancada.model.CrossDockingModel;
import com.automacaoAvancada.utils.Constantes;
import com.automacaoAvancada.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.automacaoAvancada.databinding.ActivityCrossDockingBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CrossDocking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCrossDockingBinding binding;

    private AutocompleteSupportFragment autocompleteFragment1;
    private AutocompleteSupportFragment autocompleteFragment2;
    private AutocompleteSupportFragment autocompleteFragmentDestino;
    private LatLng origem1;
    private LatLng origem2;
    private LatLng destino;
    private Button btnTracarRota;
    private Button btnIniciarCrossDock;
    private CrossDockingModel crossDockingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCrossDockingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);

        btnTracarRota = findViewById(R.id.btnTracarRota);
        btnIniciarCrossDock = findViewById(R.id.btnIniciarCrossDock);


        Places.initialize(getApplicationContext(), Constantes.API_KEY_MAPS);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnTracarRota.setOnClickListener(v->{
            tracarRotas();
        });
        btnIniciarCrossDock.setOnClickListener(view -> {
            if(crossDockingModel == null){
                Utils.showErrorMessageDialog(this, "É necessário traçar as rotas antes de iniciar o CrossDocking");
                return;
            }
            mudarTela();
        });

        autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.idOrigem1);

        autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.idOrigem2);

        autocompleteFragmentDestino = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.idDestino);

        autocompleteFragment1.setHint("Pesquisa de endereço 1");
        autocompleteFragment2.setHint("Pesquisa de endereço 2");
        autocompleteFragmentDestino.setHint("Pesquisa de Destino");

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG));
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG));
        autocompleteFragmentDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG));


        preencheMapa();

    }



    private void mudarTela() {
        Intent intent = new Intent(this, CrossDockingDetail.class);
        intent.putExtra("crossDockModel", crossDockingModel);
        startActivity(intent);
    }

    private void preencheMapa(){

        float zoomLevel = 15.0f;
        float zoomLevelDestino = 100.0f;

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Recupera a latitude e longitude do local selecionado

                origem1 = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(origem1).title("Origem 1"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem1,zoomLevel));
            }

            @Override
            public void onError(Status status) {
                Log.i("1", "Um erro ocorreu: " + status);
            }
        });

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Recupera a latitude e longitude do local selecionado
                origem2 = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(origem2).title("Origem 2"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origem2,zoomLevel));
            }

            @Override
            public void onError(Status status) {
                Log.i("1", "Um erro ocorreu: " + status);
            }
        });

        autocompleteFragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Recupera a latitude e longitude do local selecionado
                destino = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino,zoomLevelDestino));
            }

            @Override
            public void onError(Status status) {
                Log.i("1", "Um erro ocorreu: " + status);
            }
        });
    }



    private void tracarRotas(){
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAX64wzJZBPbpiYgzzBx0TYmLsoeCYPIgY")
                .build();
        if(origem1 == null){
            Utils.showErrorMessageDialog(this, "É necessário definir o endereço da origem 1 para continuar !");
            return;
        }
        if(origem2 == null){
            Utils.showErrorMessageDialog(this, "É necessário definir o endereço da origem 2 para continuar !");
            return;
        }
        if(destino == null){
            Utils.showErrorMessageDialog(this, "É necessário definir o endereço do destino para continuar !");
            return;
        }

        crossDockingModel = new CrossDockingModel();
        crossDockingModel.setVelocidadeCarro2(30);
        crossDockingModel.setVelocidadeCarro1(40);

        //Solicitação para a primeira rota
        DirectionsApiRequest request1 = DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .units(Unit.METRIC)
                .origin(origem1.latitude + "," + origem1.longitude)
                .destination(destino.latitude + "," + destino.longitude);


        //Solicitação para a segunda rota
        DirectionsApiRequest request2 = DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .units(Unit.METRIC)
                .origin(origem2.latitude + "," + origem2.longitude)
                .destination(destino.latitude + "," + destino.longitude);

        // Criar e executar uma nova Thread para fazer as solicitações de rota em segundo plano
        new Thread(() -> {
            // Solicitação para a primeira rota
            DirectionsResult result1 = null;
            try {
                result1 = request1.await(); // Faz a solicitação de forma síncrona
                processarResultadoRota(result1);
            } catch (Exception e) {
                System.out.println(e);
            }

            // Solicitação para a segunda rota
            DirectionsResult result2 = null;
            try {
                result2 = request2.await(); // Faz a solicitação de forma síncrona
                processarResultadoRota(result2);
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();



    }

    private void processarResultadoRota(DirectionsResult result) {
        if (result.routes != null && result.routes.length > 0) {

            com.google.maps.model.LatLng[] path = result.routes[0].overviewPolyline.decodePath().toArray(new com.google.maps.model.LatLng[0]);

            List<LatLng> polylinePoints = new ArrayList<>();
            for (com.google.maps.model.LatLng latLng : path) {
                polylinePoints.add(new LatLng(latLng.lat, latLng.lng));
            }

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(polylinePoints)
                    .width(10f)
                    .color(Color.BLUE);

            runOnUiThread(() -> {
                Polyline polyline = mMap.addPolyline(polylineOptions);
            });

            DirectionsRoute route = result.routes[0];
            Distance distance = route.legs[0].distance;
            String distanceText = distance.humanReadable;
            long distanceValue = distance.inMeters / 1000;

            double tolerance = 0.00020; // Ajuste de acordo com a precisão necessária

            // Verifica a origem da rota para exibir a distância correta
            if (isCoordenadaIgual(route.legs[0].startLocation, origem1, tolerance)) {
                crossDockingModel.setDistanciaCarro1(distanceValue);
                Log.d("Distância (Origem 1)", distanceText);
                Log.d("Distância em km (Origem 1)", String.valueOf(distanceValue));
            } else if (isCoordenadaIgual(route.legs[0].startLocation, origem2, tolerance)) {
                crossDockingModel.setDistanciaCarro2(distanceValue);
                Log.d("Distância (Origem 2)", distanceText);
                Log.d("Distância em km (Origem 2)", String.valueOf(distanceValue));
            }
        }
    }

    private boolean isCoordenadaIgual(com.google.maps.model.LatLng coordenada1, LatLng coordenada2, double tolerance) {
        double latDiff = Math.abs(coordenada1.lat - coordenada2.latitude);
        double lngDiff = Math.abs(coordenada1.lng - coordenada2.longitude);
        return latDiff < tolerance && lngDiff < tolerance;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

