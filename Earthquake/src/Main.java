import model.Feature;
import model.Root;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Get the start and end dates from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Start time (yyyy/MM/dd): ");
        String startTimeStr = scanner.next();

        // Get the location of the earthquake from the user
        System.out.print("Enter location:  ");
        String keyword = scanner.next();

        LocalDate currentDate = LocalDate.now();
        String endTimeStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));


        // Convert the dates to the correct format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss:S");
        String[] arrOft1 = startTimeStr.split("/");
        LocalDate t1 = LocalDate.of(Integer.parseInt(arrOft1[0]), Integer.parseInt(arrOft1[1]), Integer.parseInt(arrOft1[2]));
        String[] arrOft2 = endTimeStr.split("/");
        LocalDate t2 = LocalDate.of(Integer.parseInt(arrOft2[0]), Integer.parseInt(arrOft2[1]), Integer.parseInt(arrOft2[2]));
        Period diff = Period.between(t1, t2);

        // Create an HttpClient for the API call and send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=%s&endtime=%s", t1, t2)))
                .header("access", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Process the API response
        Root objec = new Gson().fromJson(response.body(), Root.class);
        int count = 0;
        if (objec.features.size() == 0) {
            System.out.printf("No Earthquakes were recorded past %d days.%n", diff.getDays());
        } else {
            System.out.printf("%n%d Earthquakes were recorded past %d days.%n %n", objec.features.size(), diff.getDays());
            // Print the earthquakes
            for (Feature feature : objec.features) {
                Timestamp timestamp = new Timestamp(feature.properties.time);
                if (feature.properties.place.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.printf("%s, %.2f, %s %n", feature.properties.place, feature.properties.magnitude, simpleDateFormat.format(timestamp.getTime()));
                    count++;
                }
            }
            if (count == 0) {
                System.out.printf("No earthquakes found for the keyword '%s'.%n", keyword);
            } else {
                System.out.printf("%n%d Earthquakes found for the keyword '%s'.%n", count, keyword);
            }
        }
    }

}
