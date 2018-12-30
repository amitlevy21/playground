package com.sheena.playground.api.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.Location;
import com.sheena.playground.api.NewUserForm;

public class ActivityRestControllerTestsHelper {
	private final String format = "MM/dd/yyyy hh:mm a";
	private SimpleDateFormat sdf;
	public static String FUTURE_DATE = "FUTURE";
	public static String PAST_DATE = "PAST";
	public static String PRESENT_DATE = "PAST";
	
	// Data attributes for users
	private final String userName = "user";
	private final String emailDomain = "@afeka.edu";
	private final String avatar = "lion";
	private final String userPlayground = "userPlayground";
	
	// Data attributes for Check - IN&OUT
	private String checkInOutForm = "currentDate";
	
	public ActivityRestControllerTestsHelper() {
		this.sdf = new SimpleDateFormat(this.format);
	}
	
	
	public NewUserForm generateSpecificNewUserForms(String role, int testCaseNum) {
		return new NewUserForm(this.userName + "_" + testCaseNum + "_" + this.emailDomain,
				this.userName + "_" + testCaseNum + "_", this.avatar, role);
	}


	public ElementTO generateSpecificElement(String playground, String name, String type, String username, String email, int testCaseNum) {
		Date expireationDate = null;
		try {
			String date1 = "12/26/2019";
			String time1 = "08:58 PM";
			expireationDate = this.sdf.parse(date1 + " " + time1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Location dummyLocation = new Location();
		Map<String, Object> attributes = new HashMap<>();
		
		return new ElementTO(playground, dummyLocation, name, new Date(), expireationDate, type, attributes, username, email);
}


	public ActivityTO generateSpecificActivity(String playground, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, String when) {
		Map<String, Object> attributes = new HashMap<>();
		Date theDate;
		String date = "", time = "";
		try {
			if (when.equalsIgnoreCase(this.PAST_DATE)) {
				date = "12/26/2009";
				time = "08:58 PM";
				theDate = sdf.parse(date + " " + time);
			} else if (when.equalsIgnoreCase(this.FUTURE_DATE)) {
				date = "02/20/2027";
				time = "11:28 PM";
				theDate = sdf.parse(date + " " + time);
			} else {
				theDate = new Date();
			}
			attributes.put(checkInOutForm, theDate);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

		return new ActivityTO(playground, elementPlayground, elementId, type,
				playerPlayground, playerEmail, attributes);
	}

}
