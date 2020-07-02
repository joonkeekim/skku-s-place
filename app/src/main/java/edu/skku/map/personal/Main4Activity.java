package edu.skku.map.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main4Activity extends AppCompatActivity {
    DatabaseReference mdata;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    List<Map<String, Object>> dialogItemList;

    String secretid = "";
    static ArrayList<FirebaseLocation> info = new ArrayList<FirebaseLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main4);
        Intent receiveintent = getIntent();
        info = (ArrayList<FirebaseLocation>) receiveintent.getSerializableExtra("info");

        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CustomAdapter(info);
        recyclerView.setAdapter(adapter);

    }

    public interface MyCallback {
        //this is for callbacks
        void onCallback();
    }

    public void readData(final Main4Activity.MyCallback myCallback) {

        mdata = FirebaseDatabase.getInstance().getReference();
        mdata.child("list").child(secretid).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialogItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    String get = postSnapshot.getValue(String.class);
                    Map<String, Object> itemMap = new HashMap<>();
                    if(!"".equals(get)){
                        itemMap.put("id", key+" : "+get);
                        itemMap.put("review", get);
                        dialogItemList.add(itemMap);
                        Log.d("getpublicposting", "key: " + key);
                        Log.d("getpublicposting", "key: " + get);
                    }

                }
                myCallback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("gg", databaseError.getMessage());
            }
        });
    }

    private void showAlertDialog(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Main4Activity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.poppop, null);
        builder.setView(view);

        final ListView listview = (ListView) view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();

        dialogItemList = new ArrayList<>();
        ArrayList<String> rev = new ArrayList<String>();
        readData(new MyCallback() {
            @Override
            public void onCallback() {
                Log.d("gg", "다 받아옴!");
                if(dialogItemList.size() != 0){
                    SimpleAdapter simpleAdapter = new SimpleAdapter(Main4Activity.this, dialogItemList,
                            R.layout.alert_dialog_row,new String[]{"id"},
                            new int[]{R.id.alertDialogId});

                    listview.setAdapter(simpleAdapter);

                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                }
                else{
                    Toast.makeText(Main4Activity.this, "No reviews yet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button dismiss = (Button) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //dialog.dismiss();
        //이거 위치바꿈 view밑에서

    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder> {

        ArrayList<FirebaseLocation> arrayList;
        private Context context;

        public CustomAdapter(ArrayList<FirebaseLocation> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_list, viewGroup, false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(viewHolder viewHolder, int position) {
            viewHolder.name.setText(info.get(position).name);
            viewHolder.address.setText(info.get(position).addr);
            viewHolder.phonenum.setText(info.get(position).phonenumber);
            viewHolder.ratingBar.setRating(info.get(position).rating);
            viewHolder.secret.setText(info.get(position).id);
            viewHolder.secret.setVisibility(View.INVISIBLE);
            final viewHolder viewHolder1 = viewHolder;
            //스트링으로 받고 여기서 해야되나?
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView address;
            TextView phonenum;
            RatingBar ratingBar;
            TextView secret;

            public viewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                address = (TextView) itemView.findViewById(R.id.address);
                phonenum = (TextView) itemView.findViewById(R.id.phonenum);
                ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
                secret = (TextView) itemView.findViewById(R.id.secret);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        secretid = secret.getText().toString();
                        showAlertDialog(secret.getText().toString());
                        //Toast.makeText(Main4Activity.this, secret.getText(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
    }


}
