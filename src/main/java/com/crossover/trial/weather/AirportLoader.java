package com.crossover.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice.
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

    /**
     * Read data from the input stream line by line and send them to the server.
     * @param airportDataStream data stream to read
     */
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
            printErrorMessage(makeMessageLineDoesNotContain(line, "IATA code"));
            return;
        }

        if (latitudePos >= length) {
            printErrorMessage(makeMessageLineDoesNotContain(line, "latitude"));
            return;
        }

        if (longitudePos >= length) {
            printErrorMessage(makeMessageLineDoesNotContain(line, "longitude"));
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

    private String makeMessageLineDoesNotContain(String line, String what) {
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
}
