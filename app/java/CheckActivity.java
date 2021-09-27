package kr.icehs.intec.nocovice_01;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.icehs.intec.nocovice_01.databinding.ActivityMainBinding;
import kr.icehs.intec.nocovice_01.databinding.CheckBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class CheckActivity extends AppCompatActivity {

    CheckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CheckBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
            Needle.onBackgroundThread().execute(new UiRelatedProgressTask<DbTop, Void>() {
                @Override
                protected DbTop doWork() {
                    try {
                        // prepare database
                        return new Gson().fromJson(DB.sendGet(), DbTop.class);
                    } catch(Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void thenDoUiRelatedWork(DbTop data) {
                    // set showing data
                    binding.checkDay.setText(data.today);
                    binding.checkTime.setText(data.now.substring(0, 5));
                    binding.checkName.setText(data.users.get(DB.session.uNo - 1).uName);

                    // if user have already checked today
                    if(data.users.get(DB.session.uNo - 1).isAvailable == 0) {
                        enableAttendance();
                    } else {
                        enableLeavingWork();
                    }

                    binding.checkBtn.setOnClickListener(v -> {
                        // first tap of the day
                        if(data.users.get(DB.session.uNo - 1).isAvailable == 0 && !data.users.get(DB.session.uNo - 1).lastCheck.substring(0, 10).equals(data.today)) {

                            enableLeavingWork();

                            // background thread for network process
                            Needle.onBackgroundThread().execute(() -> {
                                try {
                                    // get precise time and update database
                                    DbTop dbTop = new Gson().fromJson(DB.sendGet(), DbTop.class);
                                    DB.updateUser(
                                            dbTop.users.get(DB.session.uNo - 1).uId,
                                            dbTop.users.get(DB.session.uNo - 1).uPw,
                                            dbTop.today + " " + dbTop.now,
                                            1
                                    );
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            // no going to work twice a day
                        } else if(data.users.get(DB.session.uNo - 1).isAvailable == 0 && data.users.get(DB.session.uNo - 1).lastCheck.substring(0, 10).equals(data.today)) {
                            Toast.makeText(CheckActivity.this, "오늘은 이미 출근하셨습니다", Toast.LENGTH_SHORT).show();

                            // dayend
                        } else {
                            // new thread task for network process
                            Needle.onBackgroundThread().execute(new UiRelatedProgressTask<Integer, Void>() {
                                @Override
                                protected Integer doWork() {
                                    // at least 8 hours
                                    return calcRemainingTime();
                                }

                                @Override
                                protected void thenDoUiRelatedWork(Integer integer) {
                                    System.out.println(integer);
                                    if(integer >= 8) {
                                        enableAttendance();
                                    } else {
                                        Toast.makeText(CheckActivity.this, "아직 퇴근하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                protected void onProgressUpdate(Void unused) {

                                }
                            });
                        }
                    });
                }

                @Override
                protected void onProgressUpdate(Void unused) {

                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private int calcRemainingTime() {
        try {
            // get current time
            DbTop dbTop = new Gson().fromJson(DB.sendGet(), DbTop.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

            // get different between
            Date userCheckTime = dateFormat.parse(dbTop.users.get(DB.session.uNo - 1).lastCheck);
            Date currentTime = dateFormat.parse(dbTop.today + " " + dbTop.now);

            long diffms = currentTime.getTime() - userCheckTime.getTime();
            double diffHour = (double) diffms / 3600000;

            if(diffHour >= 8) {
                DB.updateUser(
                        dbTop.users.get(DB.session.uNo - 1).uId,
                        dbTop.users.get(DB.session.uNo - 1).uPw,
                        dbTop.users.get(DB.session.uNo - 1).lastCheck,
                        0
                );
            }
            return (int) Math.floor(diffHour);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void enableAttendance() {
        binding.checkTodayAttend.setText("출근 여부 : 미출근");
        binding.checkBtn.setText("출근");
        binding.textView3.setText("출근하시겠습니까?");
    }

    private void enableLeavingWork() {
        binding.checkTodayAttend.setText("출근 여부 : 출근 완료");
        binding.checkBtn.setText("퇴근");
        binding.textView3.setText("퇴근하시겠습니까?");
    }
}
