package edu.skku.map.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {
    String TAG = "gg";
    TextView placename;
    TextView placeaddr;
    ImageView imageView;
    RatingBar ratingBar;
    EditText comment;
    Button submit;
    String id;
    String addr;
    String name;
    String phonenumber;
    String username;
    LatLng ll;

    String review;
    Float rating;
    Integer count;
    Float currrating;
    String sort = "id";



    private DatabaseReference mPostReference;
    DatabaseReference mdata;
    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<Float> arrayRating =  new ArrayList<Float>();
    static ArrayList<Integer> arrayCount =  new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String apiKey = "AIzaSyCMgePs1y2PT41_RgEykuSlSqCl-ECN3Nw";

        Intent receiveintent = getIntent();
        username = receiveintent.getStringExtra("username");

        placename = findViewById(R.id.placeName);
        placeaddr = findViewById(R.id.placeAddr);

        ratingBar = findViewById(R.id.ratingBar);
        comment = findViewById(R.id.comment);

        submit = findViewById(R.id.submit);







        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        readData(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> arrayIndex) {
                Log.d(TAG,"gg");
            }
        });
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,Place.Field.TYPES,Place.Field.PHONE_NUMBER,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", "+place.getAddress()+","+place.getLatLng());

                id= place.getId();
                name = place.getName();
                addr = place.getAddress();
                phonenumber = place.getPhoneNumber();
                ll = place.getLatLng();
                placename.setText(place.getName());
                placeaddr.setText(place.getAddress());



            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review = comment.getText().toString();
                rating = ratingBar.getRating();
                if(!IsExistID()){
                    count = 1;
                    postFirebaseDatabase(true);
                    Intent intent = new Intent(Search.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else{
                    int index = arrayIndex.indexOf(id);
                    count = arrayCount.get(index);


                    float temprating = arrayRating.get(index);
                    rating = ((temprating*count)+rating)/(float)(count+1);
                    count = count+1;
                    postFirebaseDatabase2(true);

                    Intent intent = new Intent(Search.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }
    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> childUpdates2 = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebaseLocation post = new FirebaseLocation( addr, name, phonenumber, id,count,rating,ll.latitude,ll.longitude);
            postValues = post.toMap();
        }
        childUpdates.put("/list/" + id, postValues);
        childUpdates2.put("/list/"+id+"/reviews/"+username,review);
        mPostReference.updateChildren(childUpdates);
        mPostReference.updateChildren(childUpdates2);
    }
    public void postFirebaseDatabase2(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference().child("list");
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + id +"/count", count);
        childUpdates.put("/" + id +"/rating", rating);
        childUpdates.put("/"+id+"/reviews/"+username,review);
        mPostReference.updateChildren(childUpdates);
    }

    public interface MyCallback {
        //this is for callbacks
        void onCallback(ArrayList<String> arrayIndex);
    }
    public boolean IsExistID(){
        boolean IsExist = arrayIndex.contains(id);
        Log.d("gg","isexist? " +id+", "+IsExist);
        return IsExist;
    }
    public void readData(final MyCallback myCallback){

        mdata =  FirebaseDatabase.getInstance().getReference();
        mdata.child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebaseLocation get = postSnapshot.getValue(FirebaseLocation.class);
                    arrayIndex.add(key);
                    arrayCount.add(get.count);
                    arrayRating.add(get.rating);
                    Log.d("getpublicposting", "key: " + key);
                }
                myCallback.onCallback(arrayIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("gg",databaseError.getMessage());
            }
        });
    }


}
