package kr.icehs.intec.mdp_login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;

public class sub_check extends AppCompatActivity {

    Button check_btn;

    int currentSessionUNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);

        check_btn = findViewById(R.id.button10);
        currentSessionUNo = Integer.parseInt(getIntent().getStringExtra("uNo"));

        check_btn.setOnClickListener(v -> {
            CheckTask task = new CheckTask();
            task.execute();
        });
    }

    public void makeToast(String data) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(() -> Toast.makeText(sub_check.this, data, Toast.LENGTH_SHORT).show(), 0);
    }

    public class CheckTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DB db = new DB();

            try {
                String serverData = db.sendGet();
                JSONArray jsonArray = db.jsonArrayParser(serverData, "users");
                String uName = db.getByNum(jsonArray, "uName", currentSessionUNo);
                String uId = db.getByNum(jsonArray, "uId", currentSessionUNo);
                String uPw = db.getByNum(jsonArray, "uPw", currentSessionUNo);
                String lastCheck = db.getByNum(jsonArray, "lastCheck", currentSessionUNo);
                String todaysDate = db.getDate();

                if(lastCheck.equals(todaysDate)) {
                    // 오늘 이미 출석한 경우
                    makeToast("오늘은 이미 출석하셨습니다.");
                } else {
                    if(db.updateUser(String.valueOf(currentSessionUNo+1), uId, uPw, todaysDate) == 0) {
                        // 출석 완료
                        makeToast( uName + "님 " + todaysDate + " 출석");
                    } else {
                        // 서버 문제
                        makeToast("출석에 실패하였습니다, 나중에 다시 시도해주세요.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}