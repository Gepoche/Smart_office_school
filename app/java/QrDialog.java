package kr.icehs.intec.nocovice_01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import java.io.File;

import kr.icehs.intec.nocovice_01.databinding.ActivityQrDialogBinding;

public class QrDialog extends AppCompatActivity {
    ActivityQrDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        System.out.println(getFileStreamPath("qr").getPath() + "resvon" + intent.getStringExtra("filename") + "by" + DB.session.uNo + ".png");
        Glide.with(getApplicationContext()).load(new File(intent.getStringExtra("filename"))).into(binding.imageView);

        binding.button.setOnClickListener(v -> {
            finish();
        });
    }
}