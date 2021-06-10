import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class DB {
    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public int updateUser(String url, String uNo, String uId, String uPw, String lastCheck) throws Exception {
        String params = "?target=user&uNo=" + uNo + "&uId=" + uId + "&uPw=" + uPw + "&lastCheck=" + lastCheck;

        URL obj = new URL(url + params);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    public int insertBook(String url, String uNo, String startTime, String endTime) throws Exception {
        String params = "?target=room&uNo=" + uNo + "&startTime=" + startTime + "&endTime=" + endTime + "&mode=ins";

        URL obj = new URL(url + params);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteBook(String url, String startTime) throws Exception {
        String params = "?target=room&startTime=" + startTime + "&mode=del";

        URL obj = new URL(url + params);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    public int updateLED(String url, int ledNum, int stmt) throws Exception {
        String params = "?target=led&led_num=" + ledNum + "&stmt=" + stmt;

        URL obj = new URL(url + params);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if(response.toString().contains("successful")) {
            return 0;
        } else {
            return 1;
        }
    }

    public JSONArray jsonArrayParser(String stringData, String key) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(stringData);
        JSONArray jsonArray = (JSONArray) jsonObject.get(key);

        return jsonArray;
    }

    public boolean isInDB(JSONArray jsonArray, String key, String value) {
        for(int i=0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = (JSONObject)jsonArray.get(i);

            Object keyVal = jsonObj.get(key);
            if(keyVal == null) {
                return false;
            }

            if(keyVal.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }

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

    public String getByNum(JSONArray jsonArray, String key, int index) {
        JSONObject jsonObj = (JSONObject)jsonArray.get(index);

        Object value = jsonObj.get(key);
        if(value == null) {
            return null;
        }

        return value.toString();
    }

    public String getDate(String url) {
        try {
            String serverData = sendGet(url);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(serverData);
            return jsonObject.get("today").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTime(String url) {
        try {
            String serverData = sendGet(url);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(serverData);
            return jsonObject.get("now").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
