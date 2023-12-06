import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataAdapter {

    private static final String BASE_URL = "http://localhost:3000";
    public static List<String> sessions = new ArrayList<String>();

    public static void main(String[] args) {
        // Replace "your_user_id" with the actual user ID
        String userID = "1";

        // Example: Fetch sessions
        getSessions(userID);

        // Example: Send a user message
//        sendMessage(userID, "Hello, how are you?");

        // Example: Fetch messages for a specific session
        getMessagesForSession("1701406791895");
    }

    public static int getSessions(String userID) {
        try {
            URL url = new URL(BASE_URL + "/sessions/" + userID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject obj = new JSONObject(response.toString());
                JSONArray jArray = obj.getJSONArray("data");
                
                for (int i = 0 ; i < jArray.length() ; i++) {
                	sessions.add((String.valueOf(jArray.getLong(i))));
                }
                System.out.println("SESSIONLENGTH"+ jArray.length());
//                System.out.println(sessions);
//                System.out.println(obj.getJSONArray("data"));
//                sessions.addAll(obj.getJSONArray(userID));
                
                System.out.println("Sessions: " + response.toString());
                return jArray.length();
            } else {
                System.out.println("Error: " + responseCode);
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void sendMessage(String sessionID, String userID, String message) {
        try {
            URL url = new URL(BASE_URL + "/conversations/" + sessionID +"/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"message\": \"" + message + "\", \"sessionID\": \"" + sessionID + "\", \"userID\": \"" + userID + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Handle the success response
                System.out.println("Message sent successfully");
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getMessagesForSession(String sessionID) {
        try {
            URL url = new URL(BASE_URL + "/conversations/" + sessionID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Handle the response (parse JSON, etc.)
                System.out.println("Messages for session " + sessionID + ": " + response.toString());
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static User getUserDetails(String userID) {
        try {
            URL url = new URL(BASE_URL + "/users/" + userID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response manually into User object
                JSONObject jsonObject = new JSONObject(response.toString());

                User user = new User();
                user.firstName = jsonObject.getString("firstName");
                user.lastName = jsonObject.getString("lastName");
                user.username = jsonObject.getString("username");
                user.password = jsonObject.getString("password");

                return user;
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
   
}
