 

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class LogReader_server_print_hourly {
    
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[(\\d{2}/[A-Za-z]{3}/\\d{4}):(\\d{2}):\\d{2}:\\d{2} \\+\\d{4}\\]"
    );
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);

    public static void main(String[] args) {
        String logFilePath = "server.log"; 
        LocalDate targetDate = LocalDate.of(2024, 1, 30); 

       
        Map<Integer, Integer> hourlyTraffic = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String ip = matcher.group(1); 
                    String dateStr = matcher.group(2); 
                    int hour = Integer.parseInt(matcher.group(3)); 

                   
                    LocalDate logDate = LocalDate.parse(dateStr, DATE_FORMAT);

                   
                    if (logDate.equals(targetDate)) {
                       
                        hourlyTraffic.merge(hour, 1, Integer::sum);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the log file: " + e.getMessage());
        }

       
        System.out.println(" Hour  | Visitors");
        System.out.println("-------------------");
        for (int hour = 0; hour < 24; hour++) {
            int visitors = hourlyTraffic.getOrDefault(hour, 0);
            System.out.printf(" %02d    | %d%n", hour, visitors);
        }
    }
}
 