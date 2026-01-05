package gui.handlers;

import MyClasses.Consumables.Item;
import MyClasses.Persons.Customer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Common event handlers for the application
 */
public class EventHandlers {

    /**
     * Handler for purchase events
     */
    public static class PurchaseHandler implements EventHandler<ActionEvent> {
        private Customer customer;
        private Item item;

        public PurchaseHandler(Customer customer, Item item) {
            this.customer = customer;
            this.item = item;
        }

        @Override
        public void handle(ActionEvent event) {
            // Handle purchase logic
            customer.makePayment(item, "Credit Card");
        }
    }

    /**
     * Handler for feedback submission
     */
    public static class FeedbackHandler implements EventHandler<ActionEvent> {
        private Customer customer;
        private Item item;
        private String comment;
        private boolean liked;

        public FeedbackHandler(Customer customer, Item item, String comment, boolean liked) {
            this.customer = customer;
            this.item = item;
            this.comment = comment;
            this.liked = liked;
        }

        @Override
        public void handle(ActionEvent event) {
            // Handle feedback submission
            customer.provideFeedback(item, comment, liked);
        }
    }
}