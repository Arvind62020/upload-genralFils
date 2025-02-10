 


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogReader_2_2_ {

    public static void main(String[] args) {
        String logFilePath = "server.log"; 
        String targetDate = "2024-01-30"; 

        try {
            Map<String, Long> hourlyTraffic = parseLogFile(logFilePath, targetDate);
            List<Map.Entry<String, Long>> sortedHours = sortHoursByTraffic(hourlyTraffic);
            List<String> topHours = getTopHours(sortedHours, 0.7);

            System.out.println("Hours contributing to 70% of traffic on " + targetDate + ":");
            for (String hour : topHours) {
                System.out.println(hour);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Long> parseLogFile(String logFilePath, String targetDate) throws IOException, ParseException {
        Map<String, Long> hourlyTraffic = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                
                if (!isValidLogLine(line)) {
                    continue;
                }

                String[] parts = line.split(" ");
                String timestamp = parts[0] + " " + parts[1];
                Date logDate;

                try {
                    logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                } catch (ParseException e) {
                    
                    System.err.println("Skipping line with invalid timestamp: " + line);
                    continue;
                }

                String hourKey = dateFormat.format(logDate);

                if (hourKey.startsWith(targetDate)) {
                    try {
                        long traffic = Long.parseLong(parts[parts.length - 1]); 
                        hourlyTraffic.put(hourKey, hourlyTraffic.getOrDefault(hourKey, 0L) + traffic);
                    } catch (NumberFormatException e) {
                       
                        System.err.println("Skipping line with invalid traffic data: " + line);
                    }
                }
            }
        }

        return hourlyTraffic;
    }

    private static boolean isValidLogLine(String line) {
        String[] parts = line.split(" ");
        return parts.length >= 3;
    }

    private static List<Map.Entry<String, Long>> sortHoursByTraffic(Map<String, Long> hourlyTraffic) {
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(hourlyTraffic.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); 
        return sortedEntries;
    }

    private static List<String> getTopHours(List<Map.Entry<String, Long>> sortedHours, double threshold) {
        long totalTraffic = sortedHours.stream().mapToLong(Map.Entry::getValue).sum();
        long cumulativeTraffic = 0;
        List<String> topHours = new ArrayList<>();

        for (Map.Entry<String, Long> entry : sortedHours) {
            cumulativeTraffic += entry.getValue();
            topHours.add(entry.getKey());
            if ((double) cumulativeTraffic / totalTraffic >= threshold) {
                break;
            }
        }

        return topHours;
    }
}