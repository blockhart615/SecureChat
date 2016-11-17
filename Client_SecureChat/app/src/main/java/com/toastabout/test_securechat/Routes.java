package com.toastabout.test_securechat;

/**
 * Created by Brett on 11/16/2016.
 */

public class Routes {
	// Server user login url
	private final String LOGIN = "https://toastabout.com/SecureChat/login.php";
	// Server user register url
	private final String REGISTER = "https://toastabout.com/SecureChat/register.php";
	// Get messages
	private final String GET_MESSAGES = "https://toastabout.com/SecureChat/GetMessage.php";
	// Post messages to server
	private final String POST_MESSAGE = "https://toastabout.com/SecureChat/SendMessage.php";

	public String getLoginURL() {
		return LOGIN;
	}
	public String getRegisterURL() {
		return REGISTER;
	}
	public String getGetMessagesURL() {
		return GET_MESSAGES;
	}
	public String getPostMessagesURL() {
		return POST_MESSAGE;
	}
}
