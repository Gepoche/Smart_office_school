package kr.icehs.intec.nocovice_01;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import kr.icehs.intec.nocovice_01.databinding.LightBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;
import needle.UiRelatedTask;

public class LightActivity extends AppCompatActivity {

    final int[] VISIBILITY = new int[]{View.INVISIBLE, View.VISIBLE};

    LightBinding binding;

    ImageView[] imageViewsOn;
    ImageButton[] powerOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeLayout();
        binding.refreshLayout.setOnRefreshListener(() -> {
            initializeLayout();
            binding.refreshLayout.setRefreshing(false);
        });

        powerOnOff[0].setOnClickListener(v -> {
            int statusToChange = getStatusToChange(0);

            Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
                @Override
                protected Void doWork() {
                    for(int i = 1; i < 9; i++) {
                        try {
                            DB.updateLED(i, statusToChange);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(Void unused) {
                    for(int i = 0; i < 9; i++) {
                        imageViewsOn[i].setVisibility(VISIBILITY[statusToChange]);
                    }
                    requestUpdateOnLed();
                }
            });
        });

        for(int i = 1; i < 9; i++) {
            int finalI = i;
            powerOnOff[i].setOnClickListener(v -> {
                int statusToChange = getStatusToChange(finalI);

                Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
                    @Override
                    protected Void doWork() {
                        try {
                            DB.updateLED(finalI, statusToChange);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void thenDoUiRelatedWork(Void unused) {
                        imageViewsOn[finalI].setVisibility(VISIBILITY[statusToChange]);
                        requestUpdateOnLed();

                        if(checkIfAllOn()) {
                            imageViewsOn[0].setVisibility(View.VISIBLE);
                        } else {
                            imageViewsOn[0].setVisibility(View.INVISIBLE);
                        }
                    }
                });


            });
        }
    }

    private void initializeLayout() {
        buildComponentArray();
        Needle.onBackgroundThread().execute(new UiRelatedTask<StringBuffer>() {
            @Override
            protected StringBuffer doWork() {
                try {
                    return DB.getLedCurrentlyOn();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(StringBuffer currentlyOn) {
                initializeLightStatus(currentlyOn);
            }
        });
    }

    private boolean checkIfAllOn() {
        for(int i = 1; i < 9; i++) {
            if(imageViewsOn[i].getVisibility() == View.INVISIBLE) {
                return false;
            }
        }
        return true;
    }

    private int getStatusToChange(int index) {
        if(imageViewsOn[index].getVisibility() == View.VISIBLE) {
            return 0;
        } else {
            return 1;
        }
    }

    private void initializeLightStatus(StringBuffer currentlyOn) {
        for(int i = 0; i < currentlyOn.length(); i++) {
            int index = Integer.parseInt(String.valueOf(currentlyOn.toString().charAt(i)));
            imageViewsOn[index].setVisibility(View.VISIBLE);
        }

        if(checkIfAllOn()) {
            imageViewsOn[0].setVisibility(View.VISIBLE);
        } else {
            imageViewsOn[0].setVisibility(View.INVISIBLE);
        }
    }

    private void requestUpdateOnLed() {
        Volley.newRequestQueue(LightActivity.this).add(new StringRequest(
                Request.Method.GET,
                ServerInfo.serverHttp + ":5000/led",
                response -> {
                },
                null
        ));
    }

    private void buildComponentArray() {
        imageViewsOn = new ImageView[]{
                binding.lightOnPicture1,
                binding.lightOnPicture2,
                binding.lightOnPicture3,
                binding.lightOnPicture4,
                binding.lightOnPicture5,
                binding.lightOnPicture6,
                binding.lightOnPicture7,
                binding.lightOnPicture8,
                binding.lightOnPicture9
        };

        powerOnOff = new ImageButton[]{
                binding.lightButton1,
                binding.lightButton2,
                binding.lightButton3,
                binding.lightButton4,
                binding.lightButton5,
                binding.lightButton6,
                binding.lightButton7,
                binding.lightButton8,
                binding.lightButton9
        };
    }
}
