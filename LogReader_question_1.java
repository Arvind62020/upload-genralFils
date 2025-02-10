 
    import java.io.*;
    import java.nio.file.*;
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.*;
    import java.util.regex.*;
    
    public class LogReader_1 {
        
        private static final Pattern LOG_PATTERN = Pattern.compile(
            "(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[(\\d{2}/[A-Za-z]{3}/\\d{4}):.*\\]"
        );
        
        private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
    
        public static void main(String[] args) {
            String logFilePath = "server.log"; 
            Map<LocalDate, Map<String, Integer>> hitsPerIPPerDay = new HashMap<>();
    
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = LOG_PATTERN.matcher(line);
                    if (matcher.find()) {
                        String ip = matcher.group(1); 
                        String dateStr = matcher.group(2); 
                        LocalDate date = LocalDate.parse(dateStr, INPUT_FORMAT); 
                        hitsPerIPPerDay.computeIfAbsent(date, k -> new HashMap<>())
                                .merge(ip, 1, Integer::sum);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading the log file: " + e.getMessage());
            }
    
            
            System.out.println("IP Address            Occurrences");
            System.out.println("-----------------------------------");
            for (var dateEntry : hitsPerIPPerDay.entrySet()) {
                System.out.println("Date: " + dateEntry.getKey());
                for (var ipEntry : dateEntry.getValue().entrySet()) {
                    System.out.printf("%-20s |  %d%n", ipEntry.getKey(), ipEntry.getValue());
                }
                System.out.println(); 
            }
        }
    }