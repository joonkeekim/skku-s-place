package edu.skku.map.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    String username = "";
    String password = "";
    String passwordcheck = "";
    String found = "";
    Boolean friendcheck = false;

    String sort = "username";

    EditText usernameET;
    EditText passwordET;
    EditText passwordcheckET;
    EditText foundET;
    Button signup;
    private DatabaseReference mPostReference;
    static ArrayList<String> arrayusername = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        readData(new MyCallback() {
            @Override
            public void onCallback() {
            }
        });

        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        passwordcheckET = (EditText) findViewById(R.id.passwordcheck);
        foundET = (EditText) findViewById(R.id.foundedYear);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                passwordcheck = passwordcheckET.getText().toString();
                found = foundET.getText().toString();

                if ("".equals(username) || "".equals(password) || "".equals(passwordcheck)) {
                    Toast.makeText(SignUp.this, "Fill usrname&pwd&pwdcheck", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(passwordcheck)) {
                        Toast.makeText(SignUp.this, "Your password is different", Toast.LENGTH_SHORT).show();
                    } else {
                        if (IsExistID(username)) {//1나오면 있는거임
                            Toast.makeText(SignUp.this, "Your username is already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            if ("".equals(found)) {
                                Toast.makeText(SignUp.this, "Please write founded year", Toast.LENGTH_SHORT).show();
                                /*postFirebaseDatabase(true);
                                Intent intent = new Intent(SignUp.this, Login.class);
                                startActivity(intent);*/
                            } else {
                                if (found.equals("1398")) {
                                    postFirebaseDatabase(true);
                                    Intent intent = new Intent(SignUp.this, Login.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUp.this, "Founded year is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                }
            }
        });

    }

    public interface MyCallback {
        //this is for callbacks
        void onCallback();
    }

    public void postFirebaseDatabase(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {
            LoginService post = new LoginService(username, password);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + username, postValues);
        mPostReference.updateChildren(childUpdates);




    }

    public void readData(final MyCallback myCallback) {
        arrayusername.clear();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayusername.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    arrayusername.add(key);
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

    public boolean IsExistID(String Username) {
        boolean IsExist = arrayusername.contains(Username);
        Log.d("gg", "isexist? " + IsExist);
        return IsExist;
    }
}
