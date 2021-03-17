package com.smartgenix.gps;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {

    private String latitude, longitude, date, zone;

    public Location(String latitude, String longitude, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try{
            obj.put("latitud", getLatitude());
            obj.put("longitud", getLongitude());
            obj.put("date", getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
