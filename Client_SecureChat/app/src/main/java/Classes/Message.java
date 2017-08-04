package Classes;

/**
 * Class that represents the basic structure of a message
 */
public class Message {

    private String sender;
    private String timeStamp;
    private String message;

    public Message(String sender, String timeStamp, String message){
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    /**
     * saving conversations:
     * each conversation will be held in a file
     * filename = recipeient's name
     */
}
