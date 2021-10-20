package kr.icehs.intec.nocovice_01;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import kr.icehs.intec.nocovice_01.databinding.ElevatorBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;
import needle.UiRelatedTask;

public class ElevatorActivity extends AppCompatActivity {

    ElevatorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ElevatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Needle.onBackgroundThread().execute(new UiRelatedTask<DbTop>() {
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
                if(dbTop == null) {
                    binding.floorTextView.setText("정보를 불러올 수 없습니다.");
                    return;
                }

                binding.floorTextView.setText(dbTop.elevator + "층");
            }
        });

        binding.upButton.setOnClickListener(v -> moveElevator("evup"));
        binding.downButton.setOnClickListener(v -> moveElevator("evdown"));
    }

    private void moveElevator(String path) {
        Volley.newRequestQueue(ElevatorActivity.this).add(new StringRequest(
                Request.Method.GET,
                ServerInfo.serverHttp + ":5000/" + path,
                response -> { },
                null
        ));
    }
}
