package hu.nje.plantcare.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.database.entity.BasicPlant;
import hu.nje.plantcare.database.entity.Plant;
import hu.nje.plantcare.database.PlantDao;
import hu.nje.plantcare.database.PlantDatabase;

public class ApiService {



    //URL alapok
    private static final String detailsUrl = "https://perenual.com/api/v2/species/details/";


    public static void ApiRequest(Context context, int id, String apiKey) {
        // Az API URL összeállítása a kapott ID és API kulcs alapján
        String species_url = detailsUrl + id + "?key=" + apiKey;

        // Adatbázis elérése
        PlantDatabase db = PlantDatabase.getDatabase(context);
        PlantDao plantDao = db.plantDao();

        // API kérés indítása Volley-val
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, species_url, null,
                species -> {  // Ha sikeres a kérés
                    try {
                        // Az API válasz feldolgozása
                        String commonName = species.optString("common_name", "N/A");
                        JSONArray scientificNamesArray = species.optJSONArray("scientific_name");
                        String scientificName = (scientificNamesArray != null && scientificNamesArray.length() > 0)
                                ? scientificNamesArray.getString(0) : "N/A";
                        String type = species.optString("type", "N/A");
                        String cycle = species.optString("cycle", "N/A");
                        String watering = species.optString("watering", "N/A");

                        JSONArray imgUrls = species.optJSONArray("default_image");
                        String imgUrl = (imgUrls != null && imgUrls.length()>0)
                                ? imgUrls.getString(6):"N/A";

                        String description = species.optString("description","N/A");

                        boolean isFavorite = false;

                        // Az adatokat egy új szálban mentjük el az adatbázisba
                        new Thread(() -> {
                            plantDao.insert(new Plant(commonName, scientificName, type, cycle, watering,imgUrl,description,isFavorite));
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Hiba esetén
                    error.printStackTrace();
                }
        );

        // Az API kérést hozzáadjuk a Volley kérési sorához
        Volley.newRequestQueue(context).add(request);
    }

    public interface SearchResultCallback {
        void onResult(List<BasicPlant> results);
    }

    public static void SearchApiRequest(Context context, String keyword, String API_KEY, String baseURL, SearchResultCallback callback) {
        String search_url = baseURL + "key=" + API_KEY + "&q=" + keyword;
        List<BasicPlant> results = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, search_url, null,
                response -> {  // Ha sikeres a kérés
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject species = dataArray.getJSONObject(i);

                            int id = Integer.parseInt(species.optString("id",null));
                            System.out.println(id);

                            String commonName = species.optString("common_name", "N/A");
                            System.out.println(commonName);


                            JSONArray scientificNamesArray = species.optJSONArray("scientific_name");


                            String scientificName = (scientificNamesArray != null && scientificNamesArray.length() > 0)
                                    ? scientificNamesArray.getString(0) : "N/A";
                            System.out.println(scientificName);

                            //azért dob hibát, mert ha null értéket vesz fel az img érték, nem tudja se objectként se array-ként értelmezni
                            JSONObject imgUrls = species.optJSONObject("default_image");
                            JSONArray arrayImgs = species.optJSONArray("default_image");
                            System.out.println("Object --> "+imgUrls);
                            System.out.println("Array --> "+imgUrls);



                            String imgUrl = imgUrls.optString("small_url","N/A");



                            System.out.println("EZ itt a kép urlje: --> ("+imgUrl+") <--");
                            if(!imgUrl.equals("null"))
                            {
                                if(!imgUrl.equals("https://perenual.com/storage/image/upgrade_access.jpg"))
                                {
                                    results.add(new BasicPlant(id,commonName, scientificName,imgUrl));
                                }
                            }


                        }

                        // Hívjuk meg a callback-et az eredményekkel
                        callback.onResult(results);

                    } catch (Exception e) {
                        System.out.println("Hiba a JSON feldolgozásakor");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    System.out.println("ERROR");
                    error.printStackTrace();
                }
        );

        // Az API kérést hozzáadjuk a Volley kérési sorához
        Volley.newRequestQueue(context).add(request);
    }
}
