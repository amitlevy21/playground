package com.sheena.playground.api;

import javax.validation.constraints.Email;

import com.sheena.playground.logic.users.UserEntity;

public class UserTO {
	
	@Email
	private String email;
	
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private Long points;
	
	public UserTO() {
		this.points = 0L;
	}
	
	public UserTO(String email, String username, String avatar, String role) {
		super();
		setAvatar(avatar);
		setEmail(email);
		setPlayground(playground);
		setRole(role);
		setUsername(username);
	}
	
	public UserTO(NewUserForm newUserForm) {
		setEmail(newUserForm.getEmail());
		setUsername(newUserForm.getUsername());
		setAvatar(newUserForm.getAvatar());
		setRole(newUserForm.getRole());
		this.points = 0L;
	}
	
	public UserTO(UserEntity userEntity) {
		super();
		setAvatar(userEntity.getAvatar());
		setEmail(userEntity.getEmail());
		setPlayground(userEntity.getPlayground());
		setPoints(userEntity.getPoints());
		setRole(userEntity.getRole());
		setUsername(userEntity.getUsername());
	}
	
	public UserTO(UserTO other) {
		super();
		setAvatar(other.getAvatar());
		setEmail(other.getEmail());
		setPlayground(other.getEmail());
		setRole(other.getRole());
		setUsername(other.getUsername());
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

	@Override
	public String toString() {
		return "UserTO [email=" + email + ", playground=" + playground + ", username=" + username + ", avatar=" + avatar
				+ ", role=" + role + ", points=" + points + "]";
	}
	
	public UserEntity toEntity() {
		UserEntity ue = new UserEntity();
		ue.setAvatar(this.avatar);
		ue.setEmail(this.email);
		ue.setPlayground(this.playground);
		ue.setPoints(this.points);
		ue.setRole(this.role);
		ue.setUsername(this.username);
		ue.setId(this.email + this.playground);
		
		return ue;
	}
}
