package kr.icehs.intec.nocovice_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import kr.icehs.intec.nocovice_01.databinding.LoginBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class LoginActivity extends AppCompatActivity {

    LoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("remeberedLoginData", MODE_PRIVATE);

        binding.editTextId.setText(preferences.getString("id", ""));
        binding.editTextPassword.setText(preferences.getString("pw", ""));

        if(!preferences.getString("id", "").equals("")) {
            binding.rememberCheckBox.setChecked(true);
        }

        binding.buttonLogin.setOnClickListener(v -> {
            if(binding.rememberCheckBox.isChecked()) {
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("id", binding.editTextId.getText().toString());
                editor.putString("pw", binding.editTextPassword.getText().toString());
                editor.apply();
            } else {
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("id", null);
                editor.putString("pw", null);
                editor.apply();
            }

            Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, Void>() {

                // network process thread
                @Override
                protected String doWork() {
                    // capture currnt input
                    final String idInput = binding.editTextId.getText().toString();
                    final String pwInput = binding.editTextPassword.getText().toString();


                    try {
                        // handle empty input
                        if(idInput.isEmpty()) {
                            return "idEmpty";
                        }
                        if(pwInput.isEmpty()) {
                            return "pwEmpty";
                        }

                        // bring database
                        String originalData = DB.sendGet();
                        DbTop dbTop = new Gson().fromJson(originalData, DbTop.class);

                        // find if match exists
                        int loginResult = DB.findOnLogin(dbTop, idInput, pwInput);

                        if(loginResult == -1) {
                            // no such user
                            return "idNoExist";
                        } else if(loginResult == -2) {
                            // wrong password
                            return "pwNoExist";
                        }

                        // returns uNo if match
                        return String.valueOf(loginResult);

                    } catch(Exception e) {
                        e.printStackTrace();
                        return "Exception";
                    }
                }

                // after network process is done
                @Override
                protected void thenDoUiRelatedWork(String s) {
                    switch(s) {
                        case "idNoExist":
                            // id not existing or wrong
                            Toast.makeText(LoginActivity.this, "해당 계정을 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                            break;

                        case "pwNoExist":
                            // password not existing or wrong
                            Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                            break;

                        case "idEmpty":
                            // user didn't enter id
                            Toast.makeText(LoginActivity.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                            break;

                        case "pwEmpty":
                            // user didn't enter password
                            Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                            break;

                        case "Exception":
                            // cannot access server
                            Toast.makeText(LoginActivity.this, "데이터베이스에 접속할 수 없습니다", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            // login success
                            DB.session.uNo = Integer.parseInt(s);
                            Intent nextScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
                            nextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(nextScreenIntent);
                            break;
                    }
                }

                @Override
                protected void onProgressUpdate(Void unused) {
                }
            });
        });
    }
}