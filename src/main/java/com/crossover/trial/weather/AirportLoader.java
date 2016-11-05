package com.crossover.trial.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.crossover.trial.weather.api.AirportData;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice
 *
 * TODO: Implement the Airport Loader
 * 
 * @author code test administrator
 */
public class AirportLoader {
    private static final String DEFAULT_SEPARATOR = ",";

    private static final int DEFAULT_IATA_POS = 4;
    private static final int DEFAULT_LAT_POS = 6;
    private static final int DEFAULT_LONG_POS = 7;

    private String splitBy = DEFAULT_SEPARATOR;

    private int iataPos = DEFAULT_IATA_POS;
    private int latitudePos = DEFAULT_LAT_POS;
    private int longitudePos = DEFAULT_LONG_POS;

    private boolean removeQuotes = true;

    private WeatherClient weatherClient;

    private String baseUrl = WeatherClient.BASE_URI;

    public AirportLoader() {
        weatherClient = new WeatherClient(baseUrl);
    }

    public void upload(InputStream airportDataStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }
    }

    private void processLine(String line) {
        String[] columns = line.split(splitBy);
        int length = columns.length;
        if (iataPos >= length) {
            printErrorMessage(makeLineDoesNotContain(line, "IATA code"));
            return;
        }

        if (latitudePos >= length) {
            printErrorMessage(makeLineDoesNotContain(line, "latitude"));
            return;
        }

        if (longitudePos >= length) {
            printErrorMessage(makeLineDoesNotContain(line, "longitude"));
            return;
        }

        String iata = getIata(columns);
        String latitude = columns[latitudePos];
        String longitude = columns[longitudePos];

        AirportPostResult result = postAirport(iata, latitude, longitude);
        if (result.ok) {
            printMessage("Line [" + line + "] was processed sucessfully");
        } else {
            printErrorMessage("Error while processing [" + line + "]. Error code " + result.errorCode
                    + ". Error message " + result.errorMessage);
        }

    }

    private AirportPostResult postAirport(String iata, String latitude, String longitude) {
        Response response = weatherClient.addAirport(iata, latitude, longitude);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return new AirportPostResult();
        }

        Object message = response.getEntity();

        return new AirportPostResult(message != null ? message.toString() : "", response.getStatus());
    }

    private String makeLineDoesNotContain(String line, String what) {
        return "Line [" + line + "] does not contain " + what;
    }

    private String getIata(String[] columns) {
        String res = columns[iataPos];
        if (removeQuotes) {
            res = StringUtils.strip(res, "\"");
        }
        return res;
    }

    public static void main(String args[]) throws IOException {
        int k = 0;
        System.out.println(++k == 1);
        if (args.length != 1) {
            System.out.println("usage: " + AirportLoader.class.getName() + " input_file");
            return;
        }
        String filePath = args[0];
        File airportDataFile = new File(filePath);
        if (!airportDataFile.exists()) {
            System.err.println("File " + filePath + " does not exist");
            return;
        }
        long start = System.currentTimeMillis();
//        AirportLoader al = new AirportLoader();
//        al.generateAirports(10000, filePath + "_");
        AirportLoader al = new AirportLoader();
        try (FileInputStream fis = new FileInputStream(airportDataFile + "_")) {
            al.upload(fis);
        }
        long end = System.currentTimeMillis();
        
        System.out.println("Process took " + (end - start) / 1000 + " seconds");
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

    private void printErrorMessage(String message) {
        System.err.println(message);
    }

    private static class AirportPostResult {
        boolean ok = true;
        String errorMessage = "";
        int errorCode = 0;

        public AirportPostResult() {

        }

        public AirportPostResult(String errorMessage, int errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
            ok = false;
        }
    }

    private void generateAirports(int count, String filePath) throws FileNotFoundException, IOException {
        IataGenerator iataGenerator = new IataGenerator();
        try (OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(filePath))) {
            while (count-- > 0) {
                fos.write(String.format(TEMPLATE, iataGenerator.next()));
            }
        }
    }

    private String TEMPLATE = "1,\"General Edward Lawrence Logan Intl\",\"Boston\",\"United States\",\"%s\",\"KBOS\",42.364347,-71.005181,19,-5,\"A\"\r\n";

    private static class IataGenerator {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int i, j, k;

        public String next() {
            String res = "" + ALPHABET.charAt(i) + ALPHABET.charAt(j) + ALPHABET.charAt(k);
            incrementK();
            return res;
        }

        private void incrementK() {
            if (++k == ALPHABET.length()) {
                k = 0;
                incrementJ();
            }
        }

        private void incrementJ() {
            if (++j == ALPHABET.length()) {
                j = 0;
                incrementI();
            }
        }

        private void incrementI() {
            if (++i == ALPHABET.length()) {
                throw new RuntimeException("IataGenerator reached the end");
            }
        }
    }
}
