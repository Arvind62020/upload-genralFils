
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class LogReader_2_1_  {
    
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[(\\d{2}/[A-Za-z]{3}/\\d{4}):.*\\]"
    );
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);

    public static void main(String[] args) {
        String logFilePath = "server.log";
        LocalDate targetDate = LocalDate.of(2024, 1, 30); 

        
        Map<String, Integer> ipTraffic = new HashMap<>();
        int totalTraffic = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String ip = matcher.group(1); 
                    String dateStr = matcher.group(2); 
                    LocalDate logDate = LocalDate.parse(dateStr, DATE_FORMAT);

                   
                    if (logDate.equals(targetDate)) {
                        
                        ipTraffic.merge(ip, 1, Integer::sum);
                        totalTraffic++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the log file: " + e.getMessage());
        }

        
        double threshold = totalTraffic * 0.85;

        
        List<Map.Entry<String, Integer>> sortedIPs = new ArrayList<>(ipTraffic.entrySet());
        sortedIPs.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        
        int cumulativeTraffic = 0;
        List<String> topIPs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedIPs) {
            cumulativeTraffic += entry.getValue();
            topIPs.add(entry.getKey());
            if (cumulativeTraffic >= threshold) {
                break;
            }
        }

        
        System.out.println("IP addresses contributing to 85% of traffic on " + targetDate + ":");
        for (String ip : topIPs) {
            System.out.println(ip);
        }
    }
}
    