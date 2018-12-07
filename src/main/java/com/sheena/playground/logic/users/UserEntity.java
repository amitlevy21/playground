package com.sheena.playground.logic.users;

import java.util.Date;

public class UserEntity {
	
	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private Long points;
	private boolean verifiedUser;
	private Date lastLogin;
	
	public UserEntity() {
		super();
		this.points = 0L;
		this.setVerifiedUser(false);
		this.setLastLogin(null);
	}

	public UserEntity(String email, String playground, String username, String avatar, String role) {
		super();
		this.email = email;
		this.playground = playground;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = 0L;
		this.setVerifiedUser(false);
		this.setLastLogin(null);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public boolean isVerifiedUser() {
		return verifiedUser;
	}

	public void setVerifiedUser(boolean verifiedUser) {
		this.verifiedUser = verifiedUser;
	}
	
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
	public String toString() {
		return "UserEntity [email=" + email + ", playground=" + playground + ", username=" + username + ", avatar="
				+ avatar + ", role=" + role + ", points=" + points + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		UserEntity other = (UserEntity) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
