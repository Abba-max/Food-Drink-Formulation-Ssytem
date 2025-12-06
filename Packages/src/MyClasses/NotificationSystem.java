package MyClasses;

import MyClasses.Persons.Admin;
import MyClasses.Persons.Author;
import MyClasses.Consumables.Item;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Notification System for Formulation Issues
 * Admins notify Authors about problems
 * Authors can view and respond to notifications
 */
public class NotificationSystem {

    private static final String NOTIFICATIONS_FILE = "data/notifications.dat";
    private LinkedList<Notification> notifications;

    public NotificationSystem() {
        this.notifications = new LinkedList<>();
        loadNotifications();
    }

    /**
     * Admin sends notification to Author about formulation issue
     */
    public Notification sendNotification(Admin admin, Author author, Item item, String issueDescription, String severity) {
        Notification notification = new Notification(
                generateNotificationId(),
                admin.getAdminID(),
                admin.getName(),
                author.getAuthorID(),
                author.getName(),
                item.getItemID(),
                item.getName(),
                issueDescription,
                severity,
                new Date(),
                "PENDING"
        );

        notifications.add(notification);
        saveNotifications();

        System.out.println("✓ Notification sent to " + author.getName());
        return notification;
    }

    /**
     * Author views their notifications
     */
    public LinkedList<Notification> getNotificationsForAuthor(int authorID) {
        LinkedList<Notification> authorNotifications = new LinkedList<>();

        for (Notification notif : notifications) {
            if (notif.recipientAuthorID == authorID) {
                authorNotifications.add(notif);
            }
        }

        return authorNotifications;
    }

    /**
     * Author marks notification as resolved
     */
    public void markAsResolved(int notificationId, String resolutionNotes) {
        for (Notification notif : notifications) {
            if (notif.notificationId == notificationId) {
                notif.status = "RESOLVED";
                notif.resolutionDate = new Date();
                notif.resolutionNotes = resolutionNotes;
                saveNotifications();
                System.out.println("✓ Notification marked as resolved");
                return;
            }
        }
        System.out.println("⚠ Notification not found");
    }

    /**
     * Get pending notifications count for author
     */
    public int getPendingCount(int authorID) {
        int count = 0;
        for (Notification notif : notifications) {
            if (notif.recipientAuthorID == authorID && "PENDING".equals(notif.status)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Display notifications for author
     */
    public void displayNotificationsForAuthor(int authorID) {
        LinkedList<Notification> authorNotifs = getNotificationsForAuthor(authorID);

        if (authorNotifs.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }

        System.out.println("\n=== YOUR NOTIFICATIONS ===");
        for (Notification notif : authorNotifs) {
            System.out.println(notif.toString());
            System.out.println("-".repeat(60));
        }
    }

    /**
     * Save notifications to file
     */
    private void saveNotifications() {
        try {
            // Create directory if it doesn't exist
            File file = new File(NOTIFICATIONS_FILE);
            file.getParentFile().mkdirs();

            // Write to file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NOTIFICATIONS_FILE))) {
                oos.writeObject(notifications);
            }
        } catch (IOException e) {
            System.err.println("Error saving notifications: " + e.getMessage());
        }
    }

    /**
     * Load notifications from file
     */
    @SuppressWarnings("unchecked")
    private void loadNotifications() {
        try {
            File file = new File(NOTIFICATIONS_FILE);
            if (!file.exists()) {
                return; // No file yet, start fresh
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NOTIFICATIONS_FILE))) {
                notifications = (LinkedList<Notification>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading notifications: " + e.getMessage());
            notifications = new LinkedList<>();
        }
    }

    /**
     * Generate unique notification ID
     */
    private int generateNotificationId() {
        return notifications.size() + 1;
    }

    // ============ INNER CLASS: NOTIFICATION ============

    public static class Notification implements Serializable {
        private static final long serialVersionUID = 1L;

        int notificationId;
        int senderAdminID;
        String senderAdminName;
        int recipientAuthorID;
        String recipientAuthorName;
        int itemID;
        String itemName;
        String issueDescription;
        String severity; // LOW, MEDIUM, HIGH, CRITICAL
        Date issueDate;
        String status; // PENDING, RESOLVED, IGNORED
        Date resolutionDate;
        String resolutionNotes;

        public Notification(int notificationId, int senderAdminID, String senderAdminName,
                            int recipientAuthorID, String recipientAuthorName,
                            int itemID, String itemName, String issueDescription,
                            String severity, Date issueDate, String status) {
            this.notificationId = notificationId;
            this.senderAdminID = senderAdminID;
            this.senderAdminName = senderAdminName;
            this.recipientAuthorID = recipientAuthorID;
            this.recipientAuthorName = recipientAuthorName;
            this.itemID = itemID;
            this.itemName = itemName;
            this.issueDescription = issueDescription;
            this.severity = severity;
            this.issueDate = issueDate;
            this.status = status;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();

            sb.append("\n[").append(severity).append("] Notification #").append(notificationId);
            sb.append(" - Status: ").append(status).append("\n");
            sb.append("From: ").append(senderAdminName).append(" (Admin ID: ").append(senderAdminID).append(")\n");
            sb.append("Date: ").append(sdf.format(issueDate)).append("\n");
            sb.append("Item: ").append(itemName).append(" (ID: ").append(itemID).append(")\n");
            sb.append("Issue: ").append(issueDescription).append("\n");

            if (resolutionDate != null) {
                sb.append("Resolved: ").append(sdf.format(resolutionDate)).append("\n");
                sb.append("Notes: ").append(resolutionNotes).append("\n");
            }

            return sb.toString();
        }

        // Getters
        public int getNotificationId() { return notificationId; }

        public String getIssueDescription() {
            return issueDescription;
        }

        public int getItemID() { return itemID; }
        public String getStatus() { return status; }
        public String getSeverity() { return severity; }
    }
}