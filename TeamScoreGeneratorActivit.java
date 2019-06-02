

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

import cricketworldcup.worldcup.R;

public class TeamScoreGeneratorActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceSimulation;
    String Userkey;
    DatabaseReference databaseReferenceScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_score_generator);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MainTeamScore").child("4");
  databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for(DataSnapshot ds : dataSnapshot.getChildren()){
              @SuppressLint({"NewApi", "LocalSuppress"}) int value = ThreadLocalRandom.current().nextInt(20, 200);
              SetResult(ds.getKey(), String.valueOf(value));
          }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
  });

    }

    private void SetResult(String key,String value) {
databaseReference.child(key).child("score").setValue(value);
Log.d("done","done");
    }

}
