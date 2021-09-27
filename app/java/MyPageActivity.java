package kr.icehs.intec.nocovice_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.icehs.intec.nocovice_01.databinding.MypageBinding;

public class MyPageActivity extends AppCompatActivity {

    MypageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MypageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.showQrButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), QrActivity.class)));
    }
}
