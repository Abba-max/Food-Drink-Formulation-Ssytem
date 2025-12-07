package MyClasses;

import java.util.Date;
import java.io.Serializable;

/**
 * Feedback class for consumer comments on formulations
 */
public class Feedback implements Serializable{
    private String comment;
    private boolean like;
    private String consumerName;
    private Date timestamp;
    private static final long serialVersionUID = 1L;

    public Feedback() {
        this.timestamp = new Date();
    }

    public Feedback(String comment, boolean like, String consumerName) {
        this.comment = comment;
        this.like = like;
        this.consumerName = consumerName;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "consumerName='" + consumerName + '\'' +
                ", like=" + (like ? "👍" : "👎") +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
