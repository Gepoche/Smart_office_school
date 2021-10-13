package kr.icehs.intec.nocovice_01;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DB {

    // entire user session
    public static class session {
        public static int uNo;
    }

    // make / delete reservation
    public enum resvUpdateMode {
        INSERT, DELETE
    }

    // create connection and returns ( parameters could exist )
    private static HttpURLConnection createConnection(String requestMethod, @Nullable String params) throws Exception {
        // null handling
        // no adding parameters if theres no
        URL url;
        if(params != null) {
            url = new URL(ServerInfo.serverHttp + params);
        } else {
            url = new URL(ServerInfo.serverHttp);
        }

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(requestMethod);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(1000);
        con.setReadTimeout(5000);

        return con;
    }

    // return http response
    private static StringBuffer getHttpResponse(HttpURLConnection con) throws Exception {
        Charset charset = StandardCharsets.UTF_8;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response;
    }

    // return entire json data as String
    public static String sendGet() throws Exception {
        return getHttpResponse(createConnection("GET", null)).toString();
    }

    // update user using uNo, return 0 if success
    public static int updateUser(String uNo, String uId, String uPw, String lastCheck, int isAvailable) throws Exception {
        String params = "?target=user" +
                "&uNo=" + uNo +
                "&uId=" + uId +
                "&uPw=" + uPw +
                "&lastCheck=" + lastCheck +
                "&isAvailable=" + isAvailable;

        String response = getHttpResponse(createConnection("POST", params)).toString();

        if(response.contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // update user using current session data, return 0 if success
    public static int updateUser(String uId, String uPw, String lastCheck, int isAvailable) throws Exception {
        String params = "?target=user" +
                "&uNo=" + session.uNo +
                "&uId=" + uId +
                "&uPw=" + uPw +
                "&lastCheck=" + lastCheck +
                "&isAvailable=" + isAvailable;

        String response = getHttpResponse(createConnection("POST", params)).toString();

        if(response.contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // update meeting room reservation using uNo, return 0 if success
    public static int updateResv(resvUpdateMode mode, @Nullable String uNo, String startTime, @Nullable String endTime, @Nullable String keyValue) throws Exception {
        String params = "?target=room";
        if(mode == resvUpdateMode.INSERT) {
            params +=
                    "&uNo=" + uNo +
                            "&startTime=" + startTime +
                            "&endTime=" + endTime +
                            "&mode=ins" +
                            "&keyValue=" + keyValue;
        } else if(mode == resvUpdateMode.DELETE) {
            params +=
                    "&startTime=" + startTime +
                            "&mode=del";
        } else {
            return 1;
        }

        String response = getHttpResponse(createConnection("POST", params)).toString();

        if(response.contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // update meeting room reservation using current session data, return 0 if success
    public static int updateResv(resvUpdateMode mode, String startTime, @Nullable String endTime, @Nullable String keyValue) throws Exception {
        String params = "?target=room";
        if(mode == resvUpdateMode.INSERT) {
            params +=
                    "&uNo=" + session.uNo +
                            "&startTime=" + startTime +
                            "&endTime=" + endTime +
                            "&mode=ins" +
                            "&keyValue=" + keyValue;
        } else if(mode == resvUpdateMode.DELETE) {
            params +=
                    "&startTime=" + startTime +
                            "&mode=del";
        } else {
            return 1;
        }

        System.out.println(params);

        String response = getHttpResponse(createConnection("POST", params)).toString();

        if(response.contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // update statement of lights of the building, doesn't actually makes changes to the hardware
    // need updates in addition from the server
    public static int updateLED(int ledNum, int stmt) throws Exception {
        String params = "?target=led" +
                "&led_num=" + ledNum +
                "&stmt=" + stmt;

        String response = getHttpResponse(createConnection("POST", params)).toString();

        if(response.contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // return StringBuffer of leds that are currently on
    public static StringBuffer getLedCurrentlyOn() throws Exception {
        StringBuffer currentlyOn = new StringBuffer();
        DbTop dbTop = new Gson().fromJson(sendGet(), DbTop.class);

        for(int i = 0; i < 8; i++) {
            if(dbTop.led.get(i).stmt == 1) {
                currentlyOn.append(i + 1);
            }
        }

        return currentlyOn;
    }

    // find particular user on login and returns uNo
    // -1 if no user, -2 if wrong password
    public static int findOnLogin(DbTop data, String id, String password) {
        DbUsers[] userArr = data.users.toArray(new DbUsers[data.usernum]);

        for(DbUsers user : userArr) {
            if(user.uId.equals(id)) {
                if(user.uPw.equals(password)) {
                    return user.uNo;
                }
                return -2;
            }
        }
        return -1;
    }
}
