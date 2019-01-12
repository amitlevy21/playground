package com.sheena.playground.plugins.shiftRegistery;

import java.util.Date;

public class ShiftResponse {

	private Date shiftDate;
	private String playerEmail;
	private String playerPlaygorund;
	
	public ShiftResponse() {
	}

	public ShiftResponse(Date shiftDate, String playerEmail, String playerPlaygorund) {
		super();
		this.shiftDate = shiftDate;
		this.playerEmail = playerEmail;
		this.playerPlaygorund = playerPlaygorund;
	}

	public Date getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Date shiftDate) {
		this.shiftDate = shiftDate;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getPlayerPlaygorund() {
		return playerPlaygorund;
	}

	public void setPlayerPlaygorund(String playerPlaygorund) {
		this.playerPlaygorund = playerPlaygorund;
	}

	@Override
	public String toString() {
		return "ShiftResponse [shiftDate=" + shiftDate + ", playerEmail=" + playerEmail + ", playerPlaygorund="
				+ playerPlaygorund + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerEmail == null) ? 0 : playerEmail.hashCode());
		result = prime * result + ((playerPlaygorund == null) ? 0 : playerPlaygorund.hashCode());
		result = prime * result + ((shiftDate == null) ? 0 : shiftDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShiftResponse other = (ShiftResponse) obj;
		if (playerEmail == null) {
			if (other.playerEmail != null)
				return false;
		} else if (!playerEmail.equals(other.playerEmail))
			return false;
		if (playerPlaygorund == null) {
			if (other.playerPlaygorund != null)
				return false;
		} else if (!playerPlaygorund.equals(other.playerPlaygorund))
			return false;
		if (shiftDate == null) {
			if (other.shiftDate != null)
				return false;
		} else if (!shiftDate.equals(other.shiftDate))
			return false;
		return true;
	}
}
