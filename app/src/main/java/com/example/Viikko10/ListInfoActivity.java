package com.example.Viikko10;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListInfoActivity extends AppCompatActivity {
    TextView city;
    TextView year;
    TextView carInfo;

    CarDataStorage carDataStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_info);

        city = findViewById(R.id.CityText);
        year = findViewById(R.id.YearText);
        carInfo = findViewById(R.id.CarInfoText);

        carDataStorage = CarDataStorage.getInstance();

        city.setText(carDataStorage.getCity());
        year.setText(String.valueOf(carDataStorage.getYear()));
        ArrayList<CarData> cars = carDataStorage.getCarData();
        StringBuilder carsString = new StringBuilder();

        for (CarData car: cars) {
            carsString.append(car.getType()).append(": ").append(String.valueOf(car.getAmount())).append("\n");

        }
        carInfo.setText(carsString.toString());



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}