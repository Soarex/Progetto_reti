import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    Utils
    Classe contenente funzioni di utilita`
*/

public class Utils {
    public static String httpRequest(String urlString, String parameters) {
        HttpURLConnection connection = null;
        
        try {
            URL url = new URL(urlString + "/" + parameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            reader.close();
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            if (connection != null)
                connection.disconnect();

            e.printStackTrace();
            return null;
        }
    }
}