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
	public final String FUTURE_DATE = "FUTURE";
	public final String PAST_DATE = "PAST";
	public final String PRESENT_DATE = "PRESENT";
	
	// Data attributes for users
	private final String userName = "user";
	private final String emailDomain = "@afeka.edu";
	private final String avatar = "lion";
	public final String playerRole = "player";
	public final String managerRole = "manager";
	public final String playground = "Sheena.2019A";
	public final String verificationCodeSuffix = "code";
	
	// Data attributes for elements
	public final String checkInOutElement = "AttendanceClock";
	public final String RgisterCancelShiftElement = "WorkingDay";

	// Data attributes for check - IN&OUT plugin
	public final String SUCCESS_CHECK_IN_MESSAGE = "Welcome, have a nice day!";
	public final String CHECK_IN_TYPE = "CheckIn";
	public final String SUCCESS_CHECK_OUT_MESSAGE = "Thank You, Goodbye!";
	public final String CHECK_OUT_TYPE = "CheckOut";
	public final String CHECK_IN_OUT_TYPE = "CheckInOut";

	//  Data attributes for Register shift
	public final String REGISTER_CANCEL_SHIFT_TYPE = "Shift";
	public final String REGISTER_SHIFT_TYPE = "RegisterShift";
	public final String CANCEL_SHIFT_TYPE = "CancelShift";
	public final String SHIFT_TYPE = "Shift";
	public final boolean shiftIsExists = true;
	
	//  Data attributes for Post & View Messages
	public final String MESSAGE_BOARD_ELEMENT_TYPE = "messageBoard";
	public final String POST_MESSAGE_ACTIVITY_TYPE = "PostMessage";
	public final String VIEW_MESSAGE_ACTIVITY_TYPE = "ViewMessages";
	public final String MESSAGE_TO_POST = "Hello everyone it's the first message!";
	

	// Data attributes for Forms
	private String checkInOutForm = "currentDate"; //serverCurrentDate
	
	public ActivityRestControllerTestsHelper() {
		this.sdf = new SimpleDateFormat(this.format);
	}
	
	
	public NewUserForm generateSpecificNewUserForms(String role, int testCaseNum) {
		return new NewUserForm(this.userName + "_" + testCaseNum + "_" + this.emailDomain,
				this.userName + "_" + testCaseNum + "_", this.avatar, role);
	}


	public ElementTO generateSpecificCheckInOutElement(
			String playground,
			String name,
			String type,
			String username,
			String email,
			int testCaseNum) {
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
		attributes.put("serverCurrentDate", new Date());
		
		return new ElementTO(playground, dummyLocation, name, new Date(), expireationDate, type, attributes, username, email);
}


	public ActivityTO generateSpecificCheckInOutActivity(String playground, String elementPlayground, String elementId, String type,
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


	public ElementTO generateSpecificShiftElement(
			String playground,
			String name,
			String type,
			String username,
			String email,
			int testCaseNum) {
		Date expireationDate;
		Map<String, Object> attributes = new HashMap<>();
		try {
			String date1 = "05/14/2019"; // May 14, 2019
			String time1 = "08:58 PM";
			expireationDate = this.sdf.parse(date1 + " " + time1);
			attributes.put("shiftDate", expireationDate);
			attributes.put("shiftHours", 8);
			attributes.put("maxWorkersInShift", 1);
			Map<String, Object> workersMap = new HashMap<>();
			attributes.put("workers", workersMap);
			
			Location dummyLocation = new Location();
			
			return new ElementTO(
					playground, dummyLocation, name,
					new Date(), expireationDate,
					type, attributes, username, email);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}
	

	public ActivityTO generateSpecificregisterCancelShiftActivity(
			String playground, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, boolean DateWithExistsShift) {
		Map<String, Object> attributes = new HashMap<>();
		Date theDate;
		String date = "", time = "";
		try {
			if (DateWithExistsShift) {
				date = "05/14/2019"; // May 14, 2019
				time = "08:58 PM";   // Not really important
				theDate = sdf.parse(date + " " + time);
			} else {
				theDate = new Date(); // No shift is plans from now to now
			}
			System.err.println("Seleted Date is: " + theDate + "\nand DateWithExistsShift: " + DateWithExistsShift);
			attributes.put("wantedShiftDate", theDate);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

		return new ActivityTO(playground, elementPlayground, elementId, type,
				playerPlayground, playerEmail, attributes);
	}


	public ElementTO generateSpecificMessageBoardElement(
			String playground,
			String name,
			String type,
			String username,
			String email,
			int testCaseNum) {
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


	public ActivityTO generateSpecificPostViewMessageActivity(String playground, String elementPlayground, String elementId,
			String type, String playerPlayground, String playerEmail) {
		Map<String, Object> attributes = new HashMap<>();
		if (type.equalsIgnoreCase(POST_MESSAGE_ACTIVITY_TYPE)) {
			attributes.put("text", MESSAGE_TO_POST);
			attributes.put("publisherEmail", playerEmail);
			attributes.put("publisherPlayground", playerPlayground);
		}
		else {
			attributes.put("size", 10);
			attributes.put("page", 0);
		}
		return new ActivityTO(playground, elementPlayground, elementId, type, playerPlayground, playerEmail,
				attributes);
	}

}
