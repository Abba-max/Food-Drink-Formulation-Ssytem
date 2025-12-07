package MyClasses.Utilities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


public class AuditTrail implements Serializable {
    private static final long serialVersionUID = 1L;

    public LinkedList<String> records;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AuditTrail() {
        records = new LinkedList<>();
        logAction("SYSTEM", "Audit trail initialized");
    }


    public void logAction(String user, String action) {
        try {
            String timestamp = dateFormat.format(new Date());
            String logEntry = String.format("[%s] %s: %s", timestamp, user, action);
            records.add(logEntry);

        } catch (Exception e) {
            System.err.println("Error logging audit action: " + e.getMessage());
        }
    }

    public void logSecurityAction(String user, String action, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        logAction("SECURITY:" + user, action + " - " + status);
    }


    public void logDataChange(String user, String entity, int entityId, String action) {
        logAction(user, String.format("%s %s with ID %d", action, entity, entityId));
    }

    public void logError(String user, String error) {
        logAction("ERROR:" + user, error);
    }


    public LinkedList<String> getAllRecords() {
        return new LinkedList<>(records);
    }

    public LinkedList<String> getRecordsByUser(String user) {
        LinkedList<String> userRecords = new LinkedList<>();

        for (String record : records) {
            if (record.contains(user + ":")) {
                userRecords.add(record);
            }
        }

        return userRecords;
    }

    public LinkedList<String> getRecordsByDateRange(String startDate, String endDate) {
        LinkedList<String> dateRecords = new LinkedList<>();

        for (String record : records) {
            // Extract date from record (format: [yyyy-MM-dd HH:mm:ss])
            if (record.length() > 20) {
                String recordDate = record.substring(1, 11);

                if (recordDate.compareTo(startDate) >= 0 &&
                        recordDate.compareTo(endDate) <= 0) {
                    dateRecords.add(record);
                }
            }
        }

        return dateRecords;
    }

    public LinkedList<String> getRecentRecords(int count) {
        LinkedList<String> recentRecords = new LinkedList<>();

        int startIndex = Math.max(0, records.size() - count);

        for (int i = startIndex; i < records.size(); i++) {
            recentRecords.add(records.get(i));
        }

        return recentRecords;
    }

    public LinkedList<String> searchRecords(String keyword) {
        LinkedList<String> matchingRecords = new LinkedList<>();

        String lowerKeyword = keyword.toLowerCase();

        for (String record : records) {
            if (record.toLowerCase().contains(lowerKeyword)) {
                matchingRecords.add(record);
            }
        }

        return matchingRecords;
    }

    public String getStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("=== AUDIT TRAIL STATISTICS ===\n");
        stats.append("Total records: ").append(records.size()).append("\n");

        // Count by action type
        int systemActions = 0;
        int adminActions = 0;
        int authorActions = 0;
        int customerActions = 0;
        int errorCount = 0;
        int securityActions = 0;

        for (String record : records) {
            if (record.contains("SYSTEM:")) systemActions++;
            else if (record.contains("ADMIN:")) adminActions++;
            else if (record.contains("AUTHOR:")) authorActions++;
            else if (record.contains("CUSTOMER:")) customerActions++;

            if (record.contains("ERROR:")) errorCount++;
            if (record.contains("SECURITY:")) securityActions++;
        }

        stats.append("\nActions by type:\n");
        stats.append("  System: ").append(systemActions).append("\n");
        stats.append("  Admin: ").append(adminActions).append("\n");
        stats.append("  Author: ").append(authorActions).append("\n");
        stats.append("  Customer: ").append(customerActions).append("\n");
        stats.append("  Security: ").append(securityActions).append("\n");
        stats.append("  Errors: ").append(errorCount).append("\n");

        // Get date range
        if (!records.isEmpty()) {
            String firstRecord = records.getFirst();
            String lastRecord = records.getLast();

            if (firstRecord.length() > 20 && lastRecord.length() > 20) {
                String firstDate = firstRecord.substring(1, 20);
                String lastDate = lastRecord.substring(1, 20);

                stats.append("\nDate range:\n");
                stats.append("  First: ").append(firstDate).append("\n");
                stats.append("  Last: ").append(lastDate).append("\n");
            }
        }

        return stats.toString();
    }

    public String exportToText() {
        StringBuilder export = new StringBuilder();

        export.append("=".repeat(80)).append("\n");
        export.append("AUDIT TRAIL EXPORT\n");
        export.append("Generated: ").append(dateFormat.format(new Date())).append("\n");
        export.append("=".repeat(80)).append("\n\n");

        for (int i = 0; i < records.size(); i++) {
            export.append(String.format("%5d. %s\n", i + 1, records.get(i)));
        }

        export.append("\n").append("=".repeat(80)).append("\n");
        export.append("Total entries: ").append(records.size()).append("\n");
        export.append("=".repeat(80)).append("\n");

        return export.toString();
    }


    public void clearAll() {
        records.clear();
        logAction("SYSTEM", "Audit trail cleared");
    }

    public int getRecordCount() {
        return records.size();
    }

    public void displayRecentActivity(int count) {
        System.out.println("\n=== RECENT ACTIVITY ===");

        LinkedList<String> recent = getRecentRecords(count);

        if (recent.isEmpty()) {
            System.out.println("No activity recorded yet.");
            return;
        }

        int startNum = Math.max(1, records.size() - count + 1);

        for (int i = 0; i < recent.size(); i++) {
            System.out.println((startNum + i) + ". " + recent.get(i));
        }
    }

    @Override
    public String toString() {
        return "AuditTrail{records=" + records.size() + " entries}";
    }
}