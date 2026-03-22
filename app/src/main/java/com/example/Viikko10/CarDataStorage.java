package com.example.Viikko10;

import java.util.ArrayList;

public class CarDataStorage {
    private String city;
    private int year;
    ArrayList<CarData> carData = new ArrayList<>();

    static private CarDataStorage help;

    private CarDataStorage() {
    }

    static public CarDataStorage getInstance(){
        if (CarDataStorage.help == null) {
            help = new CarDataStorage();
        }
        return help;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<CarData> getCarData() {
        return carData;
    }

    public int getYear() {
        return year;
    }

    public String getCity() {
        return city;
    }

    public void addCarData(CarData carData) {
        this.carData.add(carData);
    }
    public void clearData(){
        this.carData = new ArrayList<>();
    }
}
