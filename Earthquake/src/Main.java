

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
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {


        Scanner scanner = new Scanner(System.in);
        System.out.print("Start time (yyyy/MM/dd): ");
        String startTimeStr = scanner.next();
        System.out.print("End time (yyyy/MM/dd): ");
        String endTimeStr = scanner.next();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss:S");
        String[] arrOft1 = startTimeStr.split("/");
        LocalDate t1 = LocalDate.of(Integer.parseInt(arrOft1[0]), Integer.parseInt(arrOft1[1]), Integer.parseInt(arrOft1[2]));
        String[] arrOft2 = endTimeStr.split("/");
        LocalDate t2 = LocalDate.of(Integer.parseInt(arrOft2[0]), Integer.parseInt(arrOft2[1]), Integer.parseInt(arrOft2[2]));


        Period diff = Period.between(t1, t2);


        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(String.format("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=%s&endtime=%s", t1, t2))).header("access", "application/json").build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Root objec = new Gson().fromJson(response.body(), Root.class);

        if ((long) objec.features.size() == 0) {

            System.out.printf("No Earthquakes were recorded past %d days.%n", diff.getDays());
        } else {

            System.out.printf("%n%d Earthquakes were recorded past %d days.%n %n", objec.features.size(), diff.getDays());
            for (Feature feature : objec.features) {
                Timestamp timestamp = new Timestamp(feature.properties.time);


                System.out.printf("%s, %.2f, %s %n", feature.properties.place, feature.properties.mag, simpleDateFormat.format(timestamp.getTime()));
            }
        }
    }
}