package kr.icehs.intec.nocovice_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import kr.icehs.intec.nocovice_01.databinding.ActivityMainBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CheckActivity.class)));
        binding.elevatorInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ElevatorActivity.class)));
        binding.lightInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LightActivity.class)));
        binding.myPageInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MyPageActivity.class)));
        binding.reservationInBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ReservationActivity.class)));

        userDataInitialize();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        userDataInitialize();
    }

    private void userDataInitialize() {
        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<DbTop, Void>() {
            @Override
            protected DbTop doWork() {
                try {
                    return new Gson().fromJson(DB.sendGet(), DbTop.class);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(DbTop dbTop) {
                binding.sessionNameId.setText(dbTop.users.get(DB.session.uNo - 1).uName + " " + (int) dbTop.users.get(DB.session.uNo - 1).uName.charAt(0) + DB.session.uNo);
                if(dbTop.users.get(DB.session.uNo - 1).isAvailable == 1) {
                    binding.statusAttend.setVisibility(View.VISIBLE);
                    binding.statusNotAttend.setVisibility(View.INVISIBLE);
                } else {
                    binding.statusAttend.setVisibility(View.INVISIBLE);
                    binding.statusNotAttend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onProgressUpdate(Void unused) {

            }
        });
    }
}