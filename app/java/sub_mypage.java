package kr.icehs.intec.mdp_login;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class sub_mypage extends AppCompatActivity {
    static String value = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        TextView tv4 = findViewById(R.id.textView4);
        tv4.setText(value);

    }
}
