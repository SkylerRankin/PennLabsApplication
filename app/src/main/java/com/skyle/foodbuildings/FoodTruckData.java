package com.skyle.foodbuildings;

import android.content.Context;
import android.location.Location;

import java.util.Arrays;
import java.util.Comparator;

//A class to quickly hold all the food truck data and provide the sorting functionality

public class FoodTruckData {

    public enum SortMethod {Alphabetical, Closest, Rating};

    public static FoodTruck[] trucks = {
            new FoodTruck("Mikey's Grill", 5, "34th and Market", null, 39.955854, -75.191432, "American, Sandwiches", null),
            new FoodTruck("Lyn's", 4.5, "36th and Spruce", null, 39.950792, -75.195304, "American, Sandwiches", "6:00am-3:00pm (MF)"),
            new FoodTruck("John's Lunch Cart", 4.5, "33rd and Spruce", null, 39.950331, -75.191717, "American, Sandwiches", null),
            new FoodTruck("Rami's", 4.5, "40th and Locust", null, 39.952977, -75.202820, "Middle-Eastern", null),
            new FoodTruck("Sandwich Cart at 35th/Market", 4.5, "35th and Market", null, 39.956213, -75.193475, "American, Sandwiches", null),
            new FoodTruck("Gul's Breakfast and Lunch Cart", 4.5, "36th and Market", null, 39.956191, -75.194148, "American, Sandwiches", null),
            new FoodTruck("Pete's Little Lunch Box", 4.5, "33rd and Lancaster", "(215) 605-1228", 39.956425, -75.189327, "American, Sandwiches", "6:00am-4:00pm (MF)\n6:00am-4:00pm (Sa)"),
            new FoodTruck("Magic Carpet at 36th/Spruce", 4.5, "36th and Spruce", "(215)327-7533", 39.950792, -75.195304, "Vegetarian", "11:30am-3:00pm (MF)"),
            new FoodTruck("Troy Mediterranean at 38th/Spruce", 4.5, "38th and Spruce", "(610) 659-8855", 39.951287, -75.199274, "Middle-Eastern", null),
            new FoodTruck("Fruit Truck at 37th/Spruce", 4.5, "37th and Spruce", null, 39.951020, -75.197285, "Fruit", "11:30am-6:30pm (MF)"),
            new FoodTruck("Memo's Lunch Truck", 4.5, "33rd and Arch", "(215) 939-4386", 39.957611, -75.189076, "Middle-Eastern", "12:00am-12:00pm (MF)"),
            new FoodTruck("Hanan House of Pita", 4, "38th and Walnut", "(267) 226-5692", 39.953640, -75.198767, "Middle-Eastern", "11:00am-8:00pm (MF)\n11:00am-8:00pm (Sa)"),
            new FoodTruck("Fruit Truck at 35th/Market", 4, "35th and Market", null, 39.956213, -75.193475, "Fruit", null),
            new FoodTruck("Troy Mediterranean at 40th/Spruce", 4, "40th and Spruce", "(610) 659-8855", 39.951756, -75.203074, "Middle-Eastern", null),
            new FoodTruck("Sonic's", 4, "37th and Spruce", null, 39.951030, -75.197268, "American, Sandwiches", "8:30am-5:30pm (MF)"),
            new FoodTruck("Ali Baba", 4, "37th and Walnut", null, 39.953388, -75.196763, "Middle-Eastern", "8:00am-4:00pm (MF)"),
    };

    public static void sort(FoodTruckData.SortMethod m, Context context, final Location l) {

        switch (m) {
            //sort based on comparing names alphabetically
            case Alphabetical:
                Arrays.sort(trucks, new Comparator<FoodTruck>() {
                    @Override
                    public int compare(FoodTruck a, FoodTruck b) {
                        return a.name.compareTo(b.name);
                    }
                });
                break;
                //sort based on the distance from the location argument
            case Closest:
                Arrays.sort(trucks, new Comparator<FoodTruck>() {
                    @Override
                    public int compare(FoodTruck a, FoodTruck b) {
                        Location la = new Location("");
                        la.setLatitude(a.coordinate_x);
                        la.setLongitude(a.coordinate_y);
                        Location lb = new Location("");
                        lb.setLatitude(b.coordinate_x);
                        lb.setLongitude(b.coordinate_y);

                        float da = l.distanceTo(la);
                        float db = l.distanceTo(lb);

                        return da == db ? 0 : (da > db ? 1 : -1);
                    }
                });
                break;
                //sort based on the rating
            case Rating:
                Arrays.sort(trucks, new Comparator<FoodTruck>() {
                    @Override
                    public int compare(FoodTruck a, FoodTruck b) {
                        return a.rating == b.rating ? 0 : (a.rating < b.rating ? 1 : -1);
                    }
                });
                break;
        }
    }

    public static class FoodTruck {
        public String name;
        public double rating;
        public String address;
        public double coordinate_x;
        public double coordinate_y;
        public String description;
        public String hours;
        public String phone;
        public FoodTruck(String n, double r, String a, String p, double x, double y, String d, String h) {
            name = n;
            rating = r;
            address = a;
            phone = p;
            coordinate_x = x;
            coordinate_y = y;
            description = d;
            hours = h;
        }
    }

}
