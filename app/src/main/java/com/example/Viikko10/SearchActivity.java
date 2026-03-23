package com.example.Viikko10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchActivity extends AppCompatActivity {
    ObjectMapper objectMapper = new ObjectMapper();
    EditText cityName;
    EditText year;
    TextView error;
    JsonNode areas;

    JsonNode cars;
    CarData normalCars;
    CarData vans;
    CarData trucks;
    CarData busses;
    CarData specialCars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        cityName = findViewById(R.id.CityNameEdit);
        year = findViewById(R.id.YearEdit);
        error = findViewById(R.id.StatusText);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void getData(Context context, String City, int year) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    areas = objectMapper.readTree(new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px"));
                    ArrayList<String> keys = new ArrayList<>();
                    ArrayList<String> values = new ArrayList<>();
                    for (JsonNode node: areas.get("variables").get(0).get("values")) {
                        keys.add(node.asText());
                    }
                    for (JsonNode node: areas.get("variables").get(0).get("valueTexts")) {
                        values.add(node.asText());
                    }
                    HashMap<String, String> helloWorld = new HashMap<>();
                    for (int i = 0; i< keys.size(); i++) {
                        helloWorld.put(values.get(i), keys.get(i));
                    }
                    URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.esimerkkihaku));
                    String code = helloWorld.get(City);
                    ((ObjectNode) jsonInputString.get("query").get(0).get("selection")).putArray("values").add(code);
                    ((ObjectNode) jsonInputString.get("query").get(3).get("selection")).putArray("values").add(year);
                    byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
                    OutputStream os = con.getOutputStream();
                    os.write(input, 0, input.length);


                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    Log.d("Nico", response.toString());

                    cars = objectMapper.readTree(response.toString()).get("value");
                    CarDataStorage carDataStorage = CarDataStorage.getInstance();
                    carDataStorage.clearData();
                    carDataStorage.setYear(year);
                    carDataStorage.setCity(City);
                    normalCars = new CarData("Henkilöautot", Integer.parseInt(cars.get(0).toString()));
                    vans = new CarData("Pakettiautot", Integer.parseInt(cars.get(1).toString()));
                    trucks = new CarData("Kuorma-autot", Integer.parseInt(cars.get(2).toString()));
                    busses = new CarData("Linja-autot", Integer.parseInt(cars.get(3).toString()));
                    specialCars = new CarData("Erikoisautot", Integer.parseInt(cars.get(4).toString()));
                    carDataStorage.addCarData(normalCars);
                    carDataStorage.addCarData(vans);
                    carDataStorage.addCarData(trucks);
                    carDataStorage.addCarData(busses);
                    carDataStorage.addCarData(specialCars);

                    Log.d("Nico", String.valueOf(cars.get("value")));


                } catch (IOException e) {
                    System.out.println("hello world");

                }


            }
        });

    }

    public void btnSearch(View view) {
        if (cityName.getText() == null) {
            return;
        }
        try {

            int yr = Integer.parseInt(year.getText().toString());
            error.setText("I got somejksadklasö");
            if ( yr > 2024 || yr < 2011 ) {
                error.setText("Ei ole 2011-2024");
            }
            Context context = this;

            getData(context, cityName.getText().toString(), yr);
            Log.d("Lut", "i sdadkaslökdaslökdasöl");

        }catch (NullPointerException e) {
            error.setText("Ei ole luku");
        }



    }
    public void btnSwitchToInfoView(View view) {
        Intent intent = new Intent(this, ListInfoActivity.class);
        startActivity(intent);
    }


}
