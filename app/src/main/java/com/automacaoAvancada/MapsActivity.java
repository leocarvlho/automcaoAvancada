package com.automacaoAvancada;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.automacaoAvancada.route.Route;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback { //Receber um callback quando o mapa estiver pronto para uso


    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap map;
    protected List<Route> route;
    protected LatLng dest;
    protected JSONObject jObject;
    ArrayList markerPoints = new ArrayList();
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-19.912998, -43.940933);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private TextView textVelocidade;
    private TextView textVelocidadeRecomendada;
    private TextView textTempoChegada;
    private TextView textTempoDeslocamento;
    private TextView textDistanciaPercorrida;
    private ImageButton imageButtonIniciaTrajeto;
    private CountDownTimer countDownTimer;
    private long startTimeMillis;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupere a localização e a posição da câmera do estado da instância salva.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        // Recupere a exibição de conteúdo que renderiza o mapa.
        setContentView(R.layout.activity_maps);

        // Inicializa Places
        Places.initialize(getApplicationContext(), "AIzaSyBGgkO6xusRHK_ryDFUu_HvcixyOFhLsEg");

        // Construir a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Construa o mapa.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Recupera os TextView
        textVelocidade = findViewById(R.id.textVelocidade);
        textVelocidadeRecomendada = findViewById(R.id.textVelocidadeRecomendada);
        textTempoChegada = findViewById(R.id.textTempoChegada);
        textTempoDeslocamento = findViewById(R.id.textTempoDeslocamento);
        textDistanciaPercorrida = findViewById(R.id.textDistanciaPercorrida);
        // Recupera ImageButton
        imageButtonIniciaTrajeto = findViewById(R.id.imageButtonIniciaTrajeto); //atributo da classe
        ImageButton imageButtonTerminaTrajeto = findViewById(R.id.imageButtonTerminaTrajeto); //atributo do metodo
        // Deixa Invisível até criar rota ou inicializar corrida
        imageButtonTerminaTrajeto.setVisibility(View.INVISIBLE);
        imageButtonIniciaTrajeto.setVisibility(View.INVISIBLE);

        // Fica escutando se o botão de Iniciar foi clicado
        imageButtonIniciaTrajeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonIniciaTrajeto.setVisibility(View.INVISIBLE);
                imageButtonTerminaTrajeto.setVisibility(View.VISIBLE);
                // Inicializa Timer
                startTimer();
                // Inicializa Navegação/Corrida
                // TODO: Ainda precisa ser implementado
            }
        });

        // Fica escutando se o botão de encerrar foi clicado
        imageButtonTerminaTrajeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonIniciaTrajeto.setVisibility(View.VISIBLE);
                imageButtonTerminaTrajeto.setVisibility(View.INVISIBLE);
                stopTimer();
            }
        });


        // Fica verificando teve alteração na localização
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Obtém a velocidade em metros por segundo
                float velocidadeMetrosPorSegundo = location.getSpeed();

                // Converte para km/h
                double velocidadeKmPorHora = velocidadeMetrosPorSegundo * 3.6;

                // Atualiza o TextView com a velocidade em tempo real
                textVelocidade.setText(String.format("%.2f km/h", velocidadeKmPorHora));
            }
        };

        // Recupera o Botão de Pesquisar Localização
        AutocompleteSupportFragment searchLocation = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.searchLocation);
        // Remove texto padrão
        searchLocation.setHint("");

        // Indica os campos que serão recuperados da localização selecionada
        searchLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG));

        // Define a localização selecionada na pesquisa
        searchLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (dest != null) {
                    markerPoints.clear();
                    map.clear();
                }

                getCreateRoute(place.getLatLng());

                Log.i(TAG, "Lugar: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Precisa ser implementado o que fazer qunado der erro .
                Log.i(TAG, "Um erro ocorreu: " + status);
            }
        });
    }

    /**
     * Salva o estado do mapa quando a atividade é pausada.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Manipula o mapa quando está disponível.
     * Este callback é acionado quando o mapa está pronto para ser usado.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Solicitar permissão ao usuário
        getLocationPermission();

        // Ative a camada Meu local e o controle relacionado no mapa.
        updateLocationUI();

        // Obtenha a localização atual do dispositivo e defina a posição do mapa.
        getDeviceLocation();

        // Espera o clice na tela para marcar no mapa ou desmarcar
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Verifica se já tem um destino selecionado
                if (dest != null) {
                    // Remove marcações no mapa
                    markerPoints.clear();
                    map.clear();
                } else {
                    // Cria a rota
                    getCreateRoute(latLng);
                }
            }
        });
    }

    private void getCreateRoute(LatLng latLng) {

        if (lastKnownLocation != null) {
            // Verifica se os locais de início e fim são capturados
            LatLng origin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            dest = latLng;

            // Obtendo URL para a API Google Directions
            String url = getDirectionsUrl(origin, dest);

            // Classe que cria uma Thread ao ser executada
            DownloadTask downloadTask = new DownloadTask();

            // Comece a baixar os dados json da API Google Directions
            downloadTask.execute(url);
        }
    }

    /**
     * Obtém a localização atual do dispositivo e posiciona a câmera do mapa.
     */
    private void getDeviceLocation() {
        /*
         * Obtenha a melhor e mais recente localização do dispositivo, que pode ser nula em casos raros
         * casos em que um local não está disponível.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Defina a posição da câmera do mapa para a localização atual do dispositivo.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                // Move o mapa para onde está a localização atual
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                // Permite mudar o zoom do mapa
                                map.getUiSettings().setZoomGesturesEnabled(true);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                            map.getUiSettings().setZoomGesturesEnabled(true);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Solicita permissão ao usuário para usar a localização do dispositivo.
     */
    private void getLocationPermission() {
        /*
         * Solicite permissão de localização, para que possamos obter a localização do
         * dispositivo. O resultado da solicitação de permissão é tratado por um retorno de chamada,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Manipula o resultado da solicitação de permissões de localização.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// Se a solicitação for cancelada, as matrizes de resultado estarão vazias.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    /**
     * Atualiza as configurações de interface do usuário do mapa com base no fato de o usuário ter
     * concedido permissão de localização.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    //Baixar os dados JSON da API Google Directions em segundo plano
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        //Analisar os dados JSON da API Google Directions e exibir a rota no mapa
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);

        }
    }


    /**
     * Uma classe para analisar o Google Places no formato JSON
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Obtem dados da rota e preenche objeto da classe Route que é usado nos dados de
                // destino
                route = parser.parseString(jObject);

                // Obtem dados que criaram a rota marcada de vermelho
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Desenhando polilinha no Google Map para a i-ésima rota
            map.addPolyline(lineOptions);

            // Altera valores da tela
            textTempoChegada.setText(route.get(0).getDurationText());


            // Adiciona informações sobre o Destino
            MarkerOptions options = new MarkerOptions()
                    .position(dest)
                    .title("Destino: " + route.get(0).getEndAddressText())
                    .snippet("Distância: " +  route.get(0).getDistanceText() + ", Duração: "  + route.get(0).getDurationText())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end));

            // Exibe opção de iniciar
            imageButtonIniciaTrajeto.setVisibility(View.VISIBLE);

            // Exibe caixa de informação do destino
            Objects.requireNonNull(map.addMarker(options)).showInfoWindow();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origem da rota
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destino da rota
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Habilitando sensor
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=AIzaSyBGgkO6xusRHK_ryDFUu_HvcixyOFhLsEg";
        // Construindo os parâmetros para o serviço web
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Formato de saída
        String output = "json";

        // Construindo a url para o serviço da web
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * Um método para baixar dados json do url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void startTimer() {
        startTimeMillis = System.currentTimeMillis();

        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
                updateTimer(elapsedTimeMillis);
            }

            @Override
            public void onFinish() {
                // O cronômetro terminou
            }
        };

        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void updateTimer(long elapsedTimeMillis) {
        long minutes = (elapsedTimeMillis / 1000) / 60;

        String time = String.format(Locale.getDefault(), "%02d'", minutes);
        textTempoDeslocamento.setText(time);
    }
}