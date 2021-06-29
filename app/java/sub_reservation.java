package kr.icehs.intec.mdp_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class sub_reservation extends AppCompatActivity {

    Button reservation_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);

        reservation_btn = findViewById(R.id.button11);

        reservation_btn.setOnClickListener(v -> {
            Toast.makeText(sub_reservation.this, "회의실이 예약되었습니다.", Toast.LENGTH_LONG).show();
            sub_mypage.value += "회의실 예약\n";
        });
    }

    private static String randomKeyGenerator() {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();

        for(int i=0; i<10; i++) {
            stringBuffer.append(Integer.toHexString(random.nextInt(17)));
        }

        return stringBuffer.toString();
    }
}