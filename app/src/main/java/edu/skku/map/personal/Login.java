package edu.skku.map.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    String username = "";
    String password = "";

    String sort = "username";

    EditText usernameET;
    EditText passwordET;
    Button login;
    Button signUp;

    static ArrayList<String> arrayusername = new ArrayList<String>();
    static ArrayList<String> arraypassword = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Intent receiveintent = getIntent();

        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signUp = (Button) findViewById(R.id.signup);

        usernameET.setText(receiveintent.getStringExtra("username"));

        readData(new MyCallback() {
            @Override
            public void onCallback() {
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check하고 넘기기.
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                if(isExistId()) {
                    //newInstance(username);
                    if (isMatch()) {

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Login.this, "No exist ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public interface MyCallback {
        //this is for callbacks
        void onCallback();
    }
    public boolean isExistId(){
        boolean IsExist = arrayusername.contains(username);
        Log.d("gg","isexist? " +IsExist);
        return IsExist;
    }
    public boolean isMatch(){
        boolean IsMatch;
        int index = arrayusername.indexOf(username);
        String rightpwd = arraypassword.get(index);
        IsMatch = (rightpwd.equals(password));
        return IsMatch;
    }
    public void readData(final MyCallback myCallback) {
        arrayusername.clear();
        arraypassword.clear();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayusername.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    LoginService get = postSnapshot.getValue(LoginService.class);
                    arrayusername.add(key);
                    arraypassword.add(get.password);
                    Log.d("getFirebaseDatabase", "key: " + key);
                }
                myCallback.onCallback();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase", "loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild(sort);
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }
}
