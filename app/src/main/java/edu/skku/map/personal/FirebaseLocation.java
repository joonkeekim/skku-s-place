package edu.skku.map.personal;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FirebaseLocation implements Serializable {
    public String id;
    public String addr;
    public String name;
    public String phonenumber;
    public Integer count;
    public Float rating;
    public Double lat;
    public Double lng;
    //public LatLng latLng;

    public FirebaseLocation(){}

    public FirebaseLocation( String addr, String name, String phonenumber, String id,Integer count, Float rating, Double lat,Double lng){//LatLng latLng){
        this.id = id;
        this.addr = addr;
        this.name = name;
        this.phonenumber = phonenumber;
        this.count = count;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        //this.latLng = latLng;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("id",id);
        result.put("addr",addr);
        result.put("name",name);
        result.put("phonenumber",phonenumber);
        result.put("rating",rating);
        result.put("count",count);
        result.put("lat",lat);
        result.put("lng",lng);
        return result;
    }

}
