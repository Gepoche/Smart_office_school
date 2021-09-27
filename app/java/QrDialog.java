package kr.icehs.intec.nocovice_01;

import androidx.appcompat.app.AppCompatActivity;

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

        // getStringExtra 해서 bindng.imageView에 이미지 넣기
        // binding.button에 onClickListener로 나가는거 넣기
    }
}