package com.example.my_notes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocation {

    public static void getAdress(String locationAdress, Context context, Handler handler){
        Thread thread = new Thread() {
            @Override
            public void run(){
                String country = null, locality = null, countrycode = null;
                Double latitude = null, longitude = null;
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try{
                    List adressList = geocoder.getFromLocationName(locationAdress, 1);
                    if (adressList != null & adressList.size() > 0){
                        Address address = (Address) adressList.get(0);
                        StringBuilder stringBuilder = new StringBuilder();
                        country = address.getCountryName();
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        locality = address.getLocality();
                        countrycode = address.getCountryCode();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (country != null && latitude != null && longitude != null){
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("country", country);
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        bundle.putString("locality", locality);
                        bundle.putString("countrycode", countrycode);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };thread.start();
    }
}
