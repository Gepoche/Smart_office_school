import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DB {
    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);


        Charset charset = StandardCharsets.UTF_8;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public int sendPost(String url, String uNo, String uId, String uPw, String lastCheck) throws Exception {
        String params = "?uNo=" + uNo + "&uId=" + uId + "&uPw=" + uPw + "&lastCheck=" + lastCheck;

        URL obj = new URL(url + params);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);


        Charset charset = StandardCharsets.UTF_8;
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

    public JSONArray jsonParser(String stringData) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(stringData);
        JSONArray jsonArray = (JSONArray) jsonObject.get("result");

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

    public String getDate() {
        try {
            String serverData = sendGet("http://192.168.0.34");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(serverData);
            return jsonObject.get("today").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
