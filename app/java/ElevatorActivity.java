package kr.icehs.intec.nocovice_01;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.icehs.intec.nocovice_01.databinding.ElevatorBinding;

public class ElevatorActivity extends AppCompatActivity {

    ElevatorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ElevatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
