package kr.icehs.intec.mdp_login;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class DB {
    // URL of database (http form)
    public final String dbUrl = "http://192.168.0.34";

    // create http connection between parameter url using parameter request method
    public HttpURLConnection createConnection(URL url, String requestMethod) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(requestMethod);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(1000);
        con.setReadTimeout(5000);

        return con;
    }

    // get response of http connection set
    public StringBuffer getHttpResponse(HttpURLConnection con) throws Exception {
        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response;
    }

    // returns elements from the database as json formatted String
    public String sendGet() throws Exception {
        URL obj = new URL(dbUrl);
        HttpURLConnection con = createConnection(obj, "GET");
        StringBuffer response = getHttpResponse(con);

        return response.toString();
    }

    // updates user of the database by uNo(user number)
    // returns 0 if success
    public int updateUser(String uNo, String uId, String uPw, String lastCheck) throws Exception {
        String params = "?target=user&uNo=" + uNo + "&uId=" + uId + "&uPw=" + uPw + "&lastCheck=" + lastCheck;

        URL obj = new URL(dbUrl + params);
        HttpURLConnection con = createConnection(obj, "POST");
        StringBuffer response = getHttpResponse(con);

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // creates room reservation
    // returns 0 if success
    public int insertResv(String uNo, String startTime, String endTime) throws Exception {
        String params = "?target=room&uNo=" + uNo + "&startTime=" + startTime + "&endTime=" + endTime + "&mode=ins";

        URL obj = new URL(dbUrl + params);
        HttpURLConnection con = createConnection(obj, "POST");
        StringBuffer response = getHttpResponse(con);

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // deletes reservation
    // returns 0 if success
    public int deleteResv(String startTime) throws Exception {
        String params = "?target=room&startTime=" + startTime + "&mode=del";

        URL obj = new URL(dbUrl + params);
        HttpURLConnection con = createConnection(obj, "POST");
        StringBuffer response = getHttpResponse(con);

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // updates LED/Light by parameter ledNum, range 1~8
    // returns 0 if success
    public int updateLED(int ledNum, int stmt) throws Exception {
        String params = "?target=led&led_num=" + ledNum + "&stmt=" + stmt;

        URL obj = new URL(dbUrl + params);
        HttpURLConnection con = createConnection(obj, "POST");
        StringBuffer response = getHttpResponse(con);

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    // parses String data ro JSON array and returns it
    public JSONArray jsonArrayParser(String stringData, String key) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(stringData);
        JSONArray jsonArray = (JSONArray) jsonObject.get(key);

        return jsonArray;
    }

    // returns row number of key(ex: "name"), value(ex: "정재희") from jsonArray
    // returns -1 if not existing
    public int getNum(JSONArray jsonArray, String key, String value) {
        for(int i=0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = (JSONObject)jsonArray.get(i);

            Object keyVal = jsonObj.get(key);
            if(keyVal == null) {
                return -1;
            }

            if(keyVal.toString().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    // returns value of key at the row of parameter index from jsonArray
    // returns null if value not existing
    public String getByNum(JSONArray jsonArray, String key, int index) {
        JSONObject jsonObj = (JSONObject)jsonArray.get(index);

        Object value = jsonObj.get(key);
        if(value == null) {
            return null;
        }

        return value.toString();
    }

    // returns date of server
    // returns null if server not available
    public String getDate() {
        try {
            String serverData = sendGet();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(serverData);
            return jsonObject.get("today").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // returns time of server
    // returns null if server not available
    public String getTime() {
        try {
            String serverData = sendGet();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(serverData);
            return jsonObject.get("now").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}