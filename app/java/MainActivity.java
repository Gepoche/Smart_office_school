package kr.icehs.intec.mdp_login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;

public class MainActivity extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPassword;
    Button buttonLogin;
    ImageView imageView;
    GestureDetector detector;

    @SuppressLint("ClickableViewAccessibility") // ignore warning from onClickListener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        imageView = findViewById(R.id.imageView);

        // login button click event
        buttonLogin.setOnClickListener(v -> {
            LoginTask task = new LoginTask();
            task.execute();
        });

        // Easter Egg - longpressing logo makes it rotate
        //              tapping logo again makes it stop
        imageView.setOnTouchListener((v, event) -> {
            detector.onTouchEvent(event);
            return true;
        });
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                imageView.clearAnimation();
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim);
                imageView.startAnimation(animation);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    public class LoginTask extends AsyncTask<Void, Void, String> {
        final private String idInput = editTextId.getText().toString().trim();
        final private String pwInput = editTextPassword.getText().toString().trim();
        final private DB db = new DB();

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if(idInput.isEmpty()) {
                    // user didn't enter id
                    return "idEmpty";
                }
                if(pwInput.isEmpty()) {
                    // user didn't enter password
                    return "pwEmpty";
                }

                // get user data from the DB
                String originalData = db.sendGet();
                JSONArray jsonArray = db.jsonArrayParser(originalData, "users");

                // if id user entered exists in the db
                int idNum = db.getNum(jsonArray, "uId", idInput);
                if(idNum != -1) {
                    // if pw is correct
                    int pwNum = db.getNum(jsonArray, "uPw", pwInput);
                    if(pwNum != -1 && idNum == pwNum) {
                        // password for id is correct
                        // send next activity user data
                        // "userNumber:userName:userId:userPassword"
                        return pwNum + ":" +
                                db.getByNum(jsonArray, "uName", pwNum) + ":" +
                                db.getByNum(jsonArray, "uId", pwNum) + ":" +
                                db.getByNum(jsonArray, "uPw", pwNum);
                    } else {
                        // password is correct or not existing
                        return "pwNoExist";
                    }
                } else {
                    // id not existing
                    return "idNoExist";
                }
            } catch (Exception e) {
                // error occured, this should be seeing when the server is not available
                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {
                case "idNoExist":
                    // id not existing or wrong
                    Toast.makeText(MainActivity.this, "해당 계정을 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                    break;
                case "pwNoExist":
                    // password not existing or wrong
                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                    break;
                case "idEmpty":
                    // user didn't enter id
                    Toast.makeText(MainActivity.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                    break;
                case "pwEmpty":
                    // user didn't enter password
                    Toast.makeText(MainActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    break;
                case "Exception":
                    // cannot access server
                    Toast.makeText(MainActivity.this, "데이터베이스에 접속할 수 없습니다", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    // login success
                    Intent intent = new Intent(getApplicationContext(), sub_loginnext.class);
                    intent.putExtra("session", s);
                    startActivity(intent);
                    break;
            }
        }
    }
}