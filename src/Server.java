import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import com.sun.net.httpserver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.io.OutputStream;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Server {
	public static MongoClient mongoClient;
    public static MongoDatabase database;
    private final String BASE_URL = "https://mldl-chatbot-default-rtdb.firebaseio.com";
    
    public Server() {
    	mongoClient = MongoClients.create("mongodb+srv://akshatp24:password1234@chatbot.phhnt9t.mongodb.net/?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("Chatbot");
    }
    public static boolean authenticateUser(String username, String password) {
        return makeFirebaseRequest(username, password);
    }

    private static boolean makeFirebaseRequest(String username, String password) {
        String firebaseUrl = "https://mldl-chatbot-default-rtdb.firebaseio.com/UserInfo.json";

        try {
            URL url = new URL(firebaseUrl);
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

                return checkCredentials(response.toString(), username, password);
            } else {
                System.out.println("Failed to authenticate. HTTP response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkCredentials(String firebaseResponse, String username, String password) {
        if (firebaseResponse.contains("\"Username\":\"" + username + "\"")) {
            String storedPassword = extractPassword(firebaseResponse, username);
            return storedPassword.equals(password);
        }
        return false;
    }

    private static String extractPassword(String firebaseResponse, String username) {
        String[] entries = firebaseResponse.split("\\{");
        for (String entry : entries) {
            if (entry.contains("\"Username\":\"" + username + "\"")) {
                String[] parts = entry.split(",");
                for (String part : parts) {
                    if (part.contains("\"Password\":\"")) {
                        return part.split(":")[1].replace("\"", "").trim();
                    }
                }
            }
        }
        return "";
    }
    public static void main(String[] args) throws Exception {
        mongoClient = MongoClients.create("mongodb+srv://akshatp24:password1234@chatbot.phhnt9t.mongodb.net/?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("Chatbot");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/conversations", new ConversationsHandler(database));


        server.setExecutor(null);
        server.start();
        System.out.println("Server is ready for connections...");
    }
    static class ConversationsHandler implements HttpHandler {
        private final MongoDatabase database;

        public ConversationsHandler(MongoDatabase database) {
            this.database = database;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else {
                // Handle other HTTP methods if needed
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            try (InputStream requestBody = exchange.getRequestBody()) {
                // Read request body
                String body = new String(requestBody.readAllBytes());

                // Parse JSON body
                Document jsonBody = Document.parse(body);
                String sessionID = jsonBody.getString("sessionID");
                String message = jsonBody.getString("message");
                String userID = jsonBody.getString("userID");

                // Get the messages collection
                MongoCollection<Document> messageCollection = database.getCollection("messages");

                try {
                    // Insert the message into the collection
                    Document newMessage = new Document()
                            .append("sessionID", sessionID)
                            .append("message", message)
                            .append("userID", userID);
                    messageCollection.insertOne(newMessage);

                    // Retrieve all messages for the session and respond with JSON
                    List<String> messages = messageCollection.find(new Document("sessionID", sessionID))
                            .map(doc -> doc.getString("message"))
                            .into(new ArrayList<>());

                    // Respond with the messages as JSON
                    String jsonResponse = String.join(",", messages);
                    exchange.sendResponseHeaders(200, jsonResponse.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(jsonResponse.getBytes());
                    }

                } catch (Exception e) {
                    // Handle storage error
                    exchange.sendResponseHeaders(500, -1); // Internal Server Error
                }
            }
        }
    }

//    static class RequestHandler implements HttpHandler {
//        
//        private final MongoDatabase database;
//
//    	public RequestHandler(MongoDatabase database) {
//            this.database = database;
//        }

//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
////        	System.out.println(contentType);
//            if (exchange.getRequestMethod().equals("GET")) {
//                handleGetRequest(exchange);
//            } else if (exchange.getRequestMethod().equals("POST")) {
//                handleUpdateRequest(exchange);
//            } else {
//                sendResponse(exchange, "Invalid operation.", 400);
//            }
//        }

//        private void handleGetRequest(HttpExchange exchange) throws IOException {
//            String path = exchange.getRequestURI().getPath();
//            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
//            MongoCollection<Document> collection = database.getCollection("Products");
//
//            try {
//            	Document query = new Document("ProductID", productId);
//            	Document result = collection.find(query).first();
//
//                if (!result.next()) {
//                    sendResponse(exchange, "Invalid Product ID.", 404);
//                } else {
//                    String productName = result.getString("Name");
//                    double productPrice = result.getDouble("Price");
//                    double productQuantity = result.getDouble("Quantity");
//                    
//                	String reqFormatType = exchange.getRequestHeaders().getFirst("formatType");
//                	String response;
//                	if (reqFormatType.equals("application/json")) {
//                		response =  "{\"productID\": " + productId;
//                		response += ", \"Name\": \"" + productName;
//                		response += "\", \"Price\": " + productPrice;
//                		response += ", \"Quantity\": " + productQuantity + "}";
//                	} else {
//                		response = "<html><body>";
//                        response += "<h1>Product Information:</h1>";
//                        response += "<p>Product ID: " + productId + "</p>";
//                        response += "<p>Product Name: " + productName + "</p>";
//                        response += "<p>Price: " + productPrice + "</p>";
//                        response += "<p>Quantity:  " + productQuantity + "</p>";
//                        response += "</body></html>";
//                	}                    
//                    sendResponse(exchange, response, 200);
//                }
//            } catch (Exception e) {
//                sendResponse(exchange, "Database error.", 500);
//            }
//        }

//        private void handleUpdateRequest(HttpExchange exchange) throws IOException {
//            String path = exchange.getRequestURI().getPath();
////            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
//            
//            String reqFormatType = exchange.getRequestHeaders().getFirst("formatType");
//            System.out.println(reqFormatType);
//            String requestBody = new String(exchange.getRequestBody().readAllBytes());
//            System.out.println(requestBody);
//            
//            int productId;
//            String updatedName;
//            double updatedPrice;
//            double updatedQuantity;
//            
//            if (reqFormatType.equals("application/json")) {
//            	JSONObject obj = new JSONObject(requestBody);
//            	productId = obj.getInt("productID");
//            	updatedName = obj.getString("Name");
//            	updatedPrice = obj.getDouble("Price");
//            	updatedQuantity = obj.getDouble("Quantity");
//            } else {
//            	HashMap<String, String> hash_map = new HashMap<String, String>();
//            	String[] updateData = requestBody.split("&");
//            	for (int i = 0 ; i < updateData.length ; i++) {
//            		String[] keyValues = updateData[i].split("=");
//            		hash_map.put(keyValues[0], keyValues[1]);
//            	}
//            	
////            	productid=1&name=Apple&price=0.99&quantity=100.0
//                productId = Integer.parseInt(hash_map.get("productid"));
//                updatedName = hash_map.get("name");
//                updatedPrice = Double.parseDouble(hash_map.get("price"));
//                updatedQuantity = Double.parseDouble(hash_map.get("quantity"));
//            }
//
//            try {
//                PreparedStatement updateStmt = connection.prepareStatement("UPDATE Products SET Name = ?, Price = ?, Quantity = ? WHERE ProductID = ?");
//                updateStmt.setString(1, updatedName);
//                updateStmt.setDouble(2, updatedPrice);
//                updateStmt.setDouble(3, updatedQuantity);
//                updateStmt.setInt(4, productId);
//                int numRows = updateStmt.executeUpdate();
//
//                if (numRows > 0) {
//                    sendResponse(exchange, "Product updated successfully.", 200);
//                } else {
//                    sendResponse(exchange, "Failed to update product.", 404);
//                }
//            } catch (SQLException e) {
//                sendResponse(exchange, "Database error.", 500);
//            }
//        }
        

//        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
//            exchange.sendResponseHeaders(statusCode, response.length());
//            OutputStream out = exchange.getResponseBody();
//            out.write(response.getBytes());
//            out.close();
//        }
    }

