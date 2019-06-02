
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cricketworldcup.worldcup.R;

public class ScoreGeneratorActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private DatabaseReference Users;
    private DatabaseReference datab;
    private DatabaseReference sub;

    String key;
    String userdsmcode, username, userphoneno;
    String dsmname, smname, rsmname;
    DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_score);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("team_selection").child("4");


        Users = FirebaseDatabase.getInstance().getReference().child("Users");
        datab = FirebaseDatabase.getInstance().getReference().child("data");
        sub = FirebaseDatabase.getInstance().getReference().child("MainTeamScore").child("4");
        //sub.child("userid").setValue("any");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    FindOutUserDetails(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FindOutUserDetails(final String key) {
        Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String childkey = ds.getKey();
                    if (key.equals(childkey)) {
                        // Log.d("Match","yes");

                        String userdsmcode = String.valueOf(ds.child("userdsmcode").getValue());
                        String username = String.valueOf(ds.child("username").getValue());
                        String userphoneno = String.valueOf(ds.child("userphoneno").getValue());

                        GenerateScore(key, userdsmcode, username, userphoneno);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GenerateScore(final String key, final String userdsmcode, final String username, final String userphoneno) {

        datab.orderByChild("G").equalTo(userdsmcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String dsmcode = String.valueOf(ds.child("G").getValue());
                    if (userdsmcode.equals(dsmcode)) {

                        String rsmname = String.valueOf(ds.child("D").getValue());
                        String dsmname = String.valueOf(ds.child("A").getValue());
                        String smname = String.valueOf(ds.child("B").getValue());


                        SaveAll(dsmname,rsmname,smname,username,userdsmcode,userphoneno,key);
                        Log.d("rsmname", rsmname);

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SaveAll(String dsmname, String rsmname, String smname, String username, String userdsmcode, String userphoneno, String key) {
       HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Dsmname", dsmname);
        hashMap.put("rsmname", rsmname);
        hashMap.put("smname", smname);
        hashMap.put("score", "0");
        hashMap.put("username", username);
        hashMap.put("userdsmcode", userdsmcode);
        hashMap.put("userphone", userphoneno);

Log.d("username",username);
        sub.child(key).setValue(hashMap);
    }


}

