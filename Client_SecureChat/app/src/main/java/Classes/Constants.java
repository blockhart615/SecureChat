package Classes;

/**
 * Holds all the constants for the app.
 */
public class Constants {

    //API URL's
    public static final String BASE_URL = "http://192.168.0.12/Server_SecureChat/";
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String REGISTER_URL = BASE_URL + "register.php";
    public static final String GET_MESSAGES_URL = BASE_URL + "GetMessage.php";
    public static final String POST_MESSAGE_URL = BASE_URL + "SendMessage.php";
    public static final String GET_URL = GET_MESSAGES_URL+"/?receiver="; //also need to append username

    //Local storage
    public static final String LOGIN_FILENAME = "login.txt";

    //Intents
//    public static final String KEY_EXCHANGE_ACTIVITY = "";
//    public static final String KEY_EXCHANGE_ACTIVITY = "";
//    public static final String KEY_EXCHANGE_ACTIVITY = "";
//    public static final String KEY_EXCHANGE_ACTIVITY = "";
//    public static final String KEY_EXCHANGE_ACTIVITY = "";
//    public static final String KEY_EXCHANGE_ACTIVITY = "";

}
