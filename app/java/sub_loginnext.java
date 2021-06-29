package kr.icehs.intec.mdp_login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class sub_loginnext extends AppCompatActivity {

    Button check_in_btn;
    Button reservation_in_btn;
    Button light_in_btn;
    Button myPage_in_btn;

    String currentSessionUser;
    String[] userData = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_next);

        check_in_btn = findViewById(R.id.button);
        reservation_in_btn = findViewById(R.id.button2);
        light_in_btn = findViewById(R.id.button3);
        myPage_in_btn = findViewById(R.id.button4);

        // user data is sent from MainActivity as String format
        // "userNumber:userName:userId:userPassword"
        currentSessionUser = getIntent().getStringExtra("session");
        userData = currentSessionUser.split(":");

        Toast.makeText(this, userData[1] + "님 환영합니다.", Toast.LENGTH_SHORT).show();

        // check
        check_in_btn.setOnClickListener((v -> {
            Intent intent = new Intent(getApplicationContext(), sub_check.class);
            intent.putExtra("uNo", userData[0]);
            startActivity(intent);
        }));

        // conference room reservation
        reservation_in_btn.setOnClickListener((v -> {
            Intent intent = new Intent(getApplicationContext(), sub_reservation.class);
            startActivity(intent);
        }));

        // light control
        light_in_btn.setOnClickListener((v -> {
            Intent intent = new Intent(getApplicationContext(), sub_light.class);
            startActivity(intent);
        }));

        // mypage
        myPage_in_btn.setOnClickListener((v -> {
            Intent intent = new Intent(getApplicationContext(), sub_mypage.class);
            startActivity(intent);
        }));
    }

}