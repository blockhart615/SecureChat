package Classes;

import java.util.ArrayList;

/**
 * Implements Conversations
 */
public class Conversation {

    private ArrayList<Message> conversation;
    private String recipient;

    /**
     * Constructor
     * @param recipient
     */
    public Conversation(String recipient) {
        this.recipient = recipient;
    }

    /**
     * get latest messages from the server
     */
    public void pullConversations() {

    }

    /**
     * @return the conversation (might not be necessary. could handle it in loadConversation())
     */
    public ArrayList<Message> getConversation() {
        return conversation;
    }

    /**
     * this class will find the correct conversation file and load it into memory
     */
    public void loadConversation(String recipient) {

    }

}
