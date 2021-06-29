package kr.icehs.intec.mdp_login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.util.Properties;

public class sub_light extends AppCompatActivity {

    ImageButton[] imageButtonsOn = new ImageButton[9];
    ImageButton[] imageButtonsOff = new ImageButton[9];

    volatile boolean loaded;
    StringBuffer currentlyOn = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light);

        linkComponents();
        loaded = false;

        GetCurrentLed getCurrentLed = new GetCurrentLed();
        getCurrentLed.execute();

        // dispatch thread while the layout is ready
        while(!loaded) { }

        // enables leds that are already on
        for(int i=0; i<currentlyOn.length(); i++) {
            int index = Integer.parseInt(String.valueOf(currentlyOn.toString().charAt(i)));
            imageButtonsOff[index].setVisibility(View.INVISIBLE);
            imageButtonsOn[index].setVisibility(View.VISIBLE);
        }
        onAllOn();

        for(int i=0; i<9; i++) {
            int finalI = i;

            // setting all click listeners
            // basically these listeners are set to:
            // update DB,
            // update UI,
            // request raspberry to execute led
            // check if all leds are on

            if(i == 8) {
                imageButtonsOff[i].setOnClickListener(v -> {
                    for(int j=0; j<9; j++) {
                        imageButtonsOn[j].setVisibility(View.VISIBLE);
                        imageButtonsOff[j].setVisibility(View.INVISIBLE);
                    }

                    Toast.makeText(sub_light.this, "전등이 모두 켜졌습니다.", Toast.LENGTH_LONG).show();
                    sub_mypage.value += "전등을 모두 켰습니다.";

                    LedUpdater ledUpdater = new LedUpdater();

                    for(int j=1; j<=8; j++) {
                        LedDb ledDb = new LedDb();
                        ledDb.execute(j, 1);
                    }
                    ledUpdater.execute();
                });
                imageButtonsOn[i].setOnClickListener(v -> {
                    for(int j=0; j<9; j++) {
                        imageButtonsOff[j].setVisibility(View.VISIBLE);
                        imageButtonsOn[j].setVisibility(View.INVISIBLE);
                    }

                    Toast.makeText(sub_light.this, "전등이 모두 꺼졌습니다.", Toast.LENGTH_LONG).show();
                    sub_mypage.value += "전등을 모두 껐습니다.";

                    LedUpdater ledUpdater = new LedUpdater();

                    for(int j=1; j<=8; j++) {
                        LedDb ledDb = new LedDb();
                        ledDb.execute(j, 0);
                    }
                    ledUpdater.execute();
                });
                break;
            }

            imageButtonsOff[i].setOnClickListener(v -> {
                LedDb ledDb = new LedDb();
                LedUpdater ledUpdater = new LedUpdater();
                imageButtonsOn[finalI].setVisibility(View.VISIBLE);
                imageButtonsOff[finalI].setVisibility(View.INVISIBLE);
                ledDb.execute(finalI+1, 1);
                ledUpdater.execute();
                onAllOn();
            });
            imageButtonsOn[i].setOnClickListener(v -> {
                LedDb ledDb = new LedDb();
                LedUpdater ledUpdater = new LedUpdater();
                imageButtonsOff[finalI].setVisibility(View.VISIBLE);
                imageButtonsOn[finalI].setVisibility(View.INVISIBLE);
                ledDb.execute(finalI+1, 0);
                ledUpdater.execute();
                onAllOn();
            });
        }
    }

    // link components with variables
    private void linkComponents() {
        imageButtonsOff[0] = findViewById(R.id.light_off_picture1);
        imageButtonsOff[1] = findViewById(R.id.light_off_picture2);
        imageButtonsOff[2] = findViewById(R.id.light_off_picture3);
        imageButtonsOff[3] = findViewById(R.id.light_off_picture4);
        imageButtonsOff[4] = findViewById(R.id.light_off_picture5);
        imageButtonsOff[5] = findViewById(R.id.light_off_picture6);
        imageButtonsOff[6] = findViewById(R.id.light_off_picture7);
        imageButtonsOff[7] = findViewById(R.id.light_off_picture8);
        imageButtonsOff[8] = findViewById(R.id.light_off_picture9);
        imageButtonsOn[0] = findViewById(R.id.light_on_picture1);
        imageButtonsOn[1] = findViewById(R.id.light_on_picture2);
        imageButtonsOn[2] = findViewById(R.id.light_on_picture3);
        imageButtonsOn[3] = findViewById(R.id.light_on_picture4);
        imageButtonsOn[4] = findViewById(R.id.light_on_picture5);
        imageButtonsOn[5] = findViewById(R.id.light_on_picture6);
        imageButtonsOn[6] = findViewById(R.id.light_on_picture7);
        imageButtonsOn[7] = findViewById(R.id.light_on_picture8);
        imageButtonsOn[8] = findViewById(R.id.light_on_picture9);
    }

    // check if all leds are on and enable/disables 9th lightbutton
    private void onAllOn() {
        for(int i=0; i<8; i++) {
            if(imageButtonsOn[i].getVisibility() == View.INVISIBLE) {
                imageButtonsOff[8].setVisibility(View.VISIBLE);
                imageButtonsOn[8].setVisibility(View.INVISIBLE);
                return;
            }
            imageButtonsOff[8].setVisibility(View.INVISIBLE);
            imageButtonsOn[8].setVisibility(View.VISIBLE);
        }
    }

    // get leds that are already on
    public class GetCurrentLed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DB db = new DB();
                // get full database and parse led data
                String originalData = db.sendGet();
                JSONArray jsonArray = db.jsonArrayParser(originalData, "led");

                for(int i=0; i<jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    Object stmt = jsonObject.get("stmt");
                    if(stmt.equals("1")) {
                        // append led number to StringBuffer 'currentlyOn'
                        currentlyOn.append(i);
                    }
                }
                loaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // update led
    // usage: ledDb.execute(ledNum, stmt);
    public class LedDb extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... ints) {
            try {
                DB db = new DB();
                db.updateLED(ints[0], ints[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // request raspberry to update led from db
    public class LedUpdater extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSch jSch = new JSch();
                Session session = jSch.getSession("pi", "192.168.0.34", 22);
                session.setPassword("raspberry");

                Properties prop = new Properties();
                prop.put("StrictHostKeyChecking", "no");
                session.setConfig(prop);

                session.connect();

                Channel channel = session.openChannel("exec");
                ChannelExec channelExec = (ChannelExec) channel;
                channelExec.setPty(true);

                // this is the file path
                channelExec.setCommand("sudo python3 test1/jjh/ledUpdater.py");

                StringBuilder outputBuffer = new StringBuilder();
                InputStream in = channel.getInputStream();
                ((ChannelExec) channel).setErrStream(System.err);

                channel.connect();

                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        outputBuffer.append(new String(tmp, 0, i));
                        if (i < 0) break;
                    }
                    if (channel.isClosed()) {
                        System.out.println(outputBuffer.toString());
                        channel.disconnect();
                        break;
                    }
                }

                channel.disconnect();
                session.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}