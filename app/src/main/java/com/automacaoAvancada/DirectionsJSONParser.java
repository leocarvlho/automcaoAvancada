package com.automacaoAvancada;

import android.util.Log;

import com.automacaoAvancada.route.Route;
import com.automacaoAvancada.route.RouteException;
import com.automacaoAvancada.route.Segment;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {

    private static final String VALUE = "value";
    private static final String DISTANCE = "distance";

    private int distance;

    /* Código de status retornado quando a solicitação foi bem-sucedida */
    private static final String OK = "OK";

    /** Recebe um JSONObject e retorna uma lista de listas contendo latitude e longitude */
    public final List<Route> parseString(JSONObject jObject) throws RouteException {
        List<Route> routes = new ArrayList<>();

        try {

            if(!jObject.getString("status").equals(OK)){
                throw new RouteException(jObject);
            }

            JSONArray jsonRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jsonRoutes.length(); i++) {
                Route route = new Route();
                //Criar um segmento vazio
                Segment segment = new Segment();

                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                //Obter os limites - nordeste e sudoeste
                final JSONObject jsonBounds = jsonRoute.getJSONObject("bounds");
                final JSONObject jsonNortheast = jsonBounds.getJSONObject("northeast");
                final JSONObject jsonSouthwest = jsonBounds.getJSONObject("southwest");

                route.setLatLgnBounds(new LatLng(jsonNortheast.getDouble("lat"), jsonNortheast.getDouble("lng")), new LatLng(jsonSouthwest.getDouble("lat"), jsonSouthwest.getDouble("lng")));

                //Obtem apenas uma leg, pois não suporta pontos de passagem
                final JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
                //Obter os passos
                final JSONArray steps = leg.getJSONArray("steps");
                //Número de etapas para uso no loop
                final int numSteps = steps.length();
                //Definir o nome desta rota usando os endereços inicial e final
                route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));
                //Obter o aviso de direitos autorais do Google (requisito de tos)
                route.setCopyright(jsonRoute.getString("copyrights"));
                //Obter a estimativa de distância e tempo
                route.setDurationText(leg.getJSONObject("duration").getString("text"));
                route.setDurationValue(leg.getJSONObject("duration").getInt(VALUE));
                route.setDistanceText(leg.getJSONObject(DISTANCE).getString("text"));
                route.setDistanceValue(leg.getJSONObject(DISTANCE).getInt(VALUE));
                route.setEndAddressText(leg.getString("end_address"));
                //Obter o comprimento total da rota
                route.setLength(leg.getJSONObject(DISTANCE).getInt(VALUE));
                //Obter todos os avisos fornecidos (requisito do tos)
                if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                    route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
                }

                routes.add(route);
            }

        } catch (JSONException e) {
            throw new RouteException("JSONException. Msg: "+e.getMessage());
        }
        return routes;
    }

    // converter uma resposta que está em formato de fluxo de entrada em uma string legível
    private static String convertStreamToString(final InputStream input) {
        if (input == null) return null;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final StringBuilder sBuf = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sBuf.append(line);
            }
        } catch (IOException e) {
            Log.e("Routing Error", e.getMessage());
        } finally {
            try {
                input.close();
                reader.close();
            } catch (IOException e) {
                Log.e("Routing Error", e.getMessage());
            }
        }
        return sBuf.toString();
    }

    //Recebe um objeto JSONObject e retorna uma lista contendo latitude e longitude
    //Desenha o trajeto
    //Usa a classe Route
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Percorrendo todas as rotas */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Percorrendo todas as legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Percorrendo todas as etapas */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePoly(polyline);

                        /** Percorrendo todos os pontos */
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    //Percorre a sequência de caracteres, decodifica os valores e cria objetos
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}