package com.batch.web;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
	    maxFileSize = 1024 * 1024 * 10,      // 10MB
	    maxRequestSize = 1024 * 1024 * 50    // 50MB
	)
public class home extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the uploaded file
        Part filePart = req.getPart("file");
        InputStream fileContent = filePart.getInputStream();

        // Read CSV content
        List<String[]> csvData = readCsv(fileContent);

        // Process CSV data
        processCsvData(csvData);

        // Respond to the client
        resp.getWriter().write("CSV data processed successfully!");
    }

    private List<String[]> readCsv(InputStream fileContent) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new	 InputStreamReader(fileContent));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            csvData.add(fields);
        }
        return csvData;
    }

    @SuppressWarnings("unchecked")
	private void processCsvData(List<String[]> csvData) throws IOException {
        // Create a JSON array to hold the data
        JSONArray jsonArray = new JSONArray();

        for (String[] row : csvData) {
            String phoneNumber = row[0];
            String textMessage = row[1];

            // Create a JSON object for each row
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_number", phoneNumber);
            jsonObject.put("text_message", textMessage);
            System.out.println(phoneNumber+":"+textMessage);
            // Add the JSON object to the array
            jsonArray.add(jsonObject); // Use add() method to add JSONObject to JSONArray
        }

        // Convert the JSON array to a string
        String jsonPayload = jsonArray.toString();

        // Send HTTP POST request to the API endpoint
        URL url = new URL("https://www.bitmomegle.xyz/api/insert.php");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Handle the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Request successful
            System.out.println("API call successful.");
        } else {
            // Request failed
            System.out.println("API call failed with response code: " + responseCode);
        }

        connection.disconnect();
    }

}
