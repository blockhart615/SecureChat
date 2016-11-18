package com.toastabout.test_securechat;

/**
 * Created by Brett on 11/16/2016.
 */

public class Routes {
	// Server user login url
	private final String LOGIN = "https://toastabout.com/SecureChat/login.php";
	private final String REGISTER = "https://toastabout.com/SecureChat/register.php";
	private final String GET_MESSAGES = "https://toastabout.com/SecureChat/GetMessage.php";
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
