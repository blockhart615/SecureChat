package Classes;

/**
 * Class that represents the basic structure of a message
 */
public class Message {

    private String sender;
    private String receiver;
    private String timeStamp;
    private String message;

    public Message(String sender, String receiver, String timeStamp, String message){
        this.sender = sender;
        this.receiver = receiver;
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

    public String getReceiver() { return receiver; }


    /**
     * saving conversations:
     * each conversation will be held in a file
     * filename = recipeient's name
     */
}
