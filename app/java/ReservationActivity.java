package kr.icehs.intec.nocovice_01;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import kr.icehs.intec.nocovice_01.databinding.ReservationBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class ReservationActivity extends AppCompatActivity {

    ReservationBinding binding;

    Button[] reserveButtons;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buildComponentArray();
        initializeButtons();

        binding.refreshLayout.setOnRefreshListener(() -> {
            initializeButtons();
            binding.refreshLayout.setRefreshing(false);
        });

        binding.datePicker.setOnDateChangedListener(((view1, year, monthOfYear, dayOfMonth) -> initializeButtons()));

        for (int i = 9; i < 19; i++) {
            int finalI = i;
            reserveButtons[i].setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);

                if (reserveButtons[finalI].getText().equals("예약하기")) {
                    builder.setTitle("예약하시겠습니까?");
                    builder.setPositiveButton("확인", (dialog, which) -> {
                        String randomKey = generateRandomKey();
                        System.out.println(randomKey);
                        Needle.onBackgroundThread().serially().execute(() -> {
                            try {
                                DB.updateResv(
                                        DB.resvUpdateMode.INSERT,
                                        binding.datePicker.getYear() + "-" + String.format("%02d", binding.datePicker.getMonth() + 1) + "-" + String.format("%02d", binding.datePicker.getDayOfMonth()) + " " + String.format("%02d", finalI) + ":00:00",
                                        binding.datePicker.getYear() + "-" + String.format("%02d", binding.datePicker.getMonth() + 1) + "-" + String.format("%02d", binding.datePicker.getDayOfMonth()) + " " + String.format("%02d", finalI + 1) + ":00:00",
                                        randomKey
                                );
                                initializeButtons();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        saveQrCode(randomKey, binding.datePicker.getYear() + String.format("%02d", binding.datePicker.getMonth() + 1) + String.format("%02d", binding.datePicker.getDayOfMonth()) + String.format("%02d", finalI));
                    });
                    builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
                } else if (reserveButtons[finalI].getText().equals("예약취소")) {
                    builder.setTitle("취소하시겠습니까?");
                    builder.setPositiveButton("확인", (dialog, which) -> {
                        Needle.onBackgroundThread().serially().execute(() -> {
                            try {
                                DB.updateResv(
                                        DB.resvUpdateMode.DELETE,
                                        binding.datePicker.getYear() + "-" + String.format("%02d", binding.datePicker.getMonth() + 1) + "-" + String.format("%02d", binding.datePicker.getDayOfMonth()) + " " + String.format("%02d", finalI) + ":00:00",
                                        null,
                                        null
                                );
                                initializeButtons();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        removeQrCode(binding.datePicker.getYear() + String.format("%02d", binding.datePicker.getMonth() + 1) + String.format("%02d", binding.datePicker.getDayOfMonth()) + String.format("%02d", finalI));
                    });
                    builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
                }

                if (!reserveButtons[finalI].getText().equals("예약불가")) {
                    builder.create().show();
                }
            });
        }
    }

    private String generateRandomKey() {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            stringBuffer.append(Integer.toHexString(random.nextInt(17)));
        }

        return stringBuffer.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveQrCode(String randomKey, String datetime) {
        Needle.onBackgroundThread().serially().execute(() -> {
            try {
                URL url = new URL("https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + randomKey);
                InputStream in = url.openStream();
                Path imagePath = Paths.get(getFileStreamPath("qr").getPath() + "resvon" + datetime + "by" + DB.session.uNo + ".png");
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                }
                Files.copy(in, imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void removeQrCode(String datetime) {
        Path imagePath = Paths.get(getFileStreamPath("qr").getPath() + "resvon" + datetime + "by" + DB.session.uNo + ".png");
        System.out.println(imagePath);
        if (Files.exists(imagePath)) {
            try {
                Files.delete(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void initializeButtons() {
        int selectedYear = binding.datePicker.getYear();
        int selectedMonth = binding.datePicker.getMonth() + 1;
        int selectedDay = binding.datePicker.getDayOfMonth();

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<DbTop, Void>() {
            @Override
            protected DbTop doWork() {
                try {
                    return new Gson().fromJson(DB.sendGet(), DbTop.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(DbTop dbTop) {
                for (int i = 9; i < 19; i++) {
                    reserveButtons[i].setBackgroundColor(0xff26b5ff);
                    reserveButtons[i].setText("예약하기");

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                        Date currentDate = dateFormat.parse(dbTop.today + " " + dbTop.now);
                        Date selectedDate = dateFormat.parse(binding.datePicker.getYear() + "-" + String.format("%02d", binding.datePicker.getMonth() + 1) + "-" + String.format("%02d", binding.datePicker.getDayOfMonth()) + " " + String.format("%02d", i) + ":00:00");
                        long diffms = selectedDate.getTime() - currentDate.getTime();
                        if(diffms <= 0) {
                            reserveButtons[i].setBackgroundColor(0xff999999);
                            reserveButtons[i].setText("예약불가");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < dbTop.roombooks.size(); i++) {
                    String bookedDateTime = dbTop.roombooks.get(i).startTime;

                    int bookedYear = Integer.parseInt(bookedDateTime.substring(0, 4));
                    int bookedMonth = Integer.parseInt(bookedDateTime.substring(5, 7));
                    int bookedDay = Integer.parseInt(bookedDateTime.substring(8, 10));
                    int bookedHour = Integer.parseInt(bookedDateTime.substring(11, 13));

                    if (bookedYear == selectedYear && bookedMonth == selectedMonth && bookedDay == selectedDay) {
                        if(!reserveButtons[bookedHour].getText().equals("예약불가")) {
                            reserveButtons[bookedHour].setBackgroundColor(0xffff455b);
                            reserveButtons[bookedHour].setText("예약불가");
                        }
                        if (dbTop.roombooks.get(i).uNo == DB.session.uNo) {
                            reserveButtons[bookedHour].setBackgroundColor(0xff7aff56);
                            reserveButtons[bookedHour].setText("예약취소");
                        }
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Void unused) {

            }
        });
    }

    private void buildComponentArray() {
        reserveButtons = new Button[]{
                null, null, null, null, null, null, null, null, null,
                binding.reserveButton9,
                binding.reserveButton10,
                binding.reserveButton11,
                binding.reserveButton12,
                binding.reserveButton13,
                binding.reserveButton14,
                binding.reserveButton15,
                binding.reserveButton16,
                binding.reserveButton17,
                binding.reserveButton18
        };
    }
}
