package com.sheena.playground.logic.users.stubs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

//import org.springframework.stereotype.Service;

import com.sheena.playground.logic.elements.exceptions.AttributeUpdateException;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;

//@Service
public class DummyUsersService implements UsersService {
	
	private Map<String, UserEntity> users;
	private Map<String, String> userVerificationCodes;
	
	@PostConstruct
	public void init() {
		this.users = new HashMap<>();
		this.userVerificationCodes = new HashMap<>();
	}
	
	@Override
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException, RoleDoesNotExistException {
		String email = userEntity.getEmail();
		
		if(email != null && this.users.containsKey(email)) {
			throw new UserAlreadyExistsException("There is already a registered user with email: " + email);
		}
		
		boolean existsRole = isRoleExists(userEntity.getRole());
		if(!existsRole)
			throw new RoleDoesNotExistException("Requested role: " + userEntity.getRole() + " does not exist");
		
		String verificationCode = generateUserVerificationCode(userEntity);
		
		this.userVerificationCodes.put(userEntity.getEmail(), verificationCode);
		this.users.put(email, userEntity);
		
		return userEntity;
	}

	@Override
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException {
		UserEntity existing;
		
		synchronized (this.users) {
			existing = this.getUserByEmail(userEntity.getEmail());
			existing.setLastLogin(new Date());
			this.users.put(existing.getEmail(), existing);
		}
		return existing;
	}

	@Override
	public void updateUserDetails(String playground, String email, UserEntity entityUpdates) throws UserDoesNotExistException, AttributeUpdateException {
		synchronized (this.users) {
			UserEntity existing = this.getUserByEmail(email);
			
			if(entityUpdates.getPoints() != null && 
					!entityUpdates.getPoints().equals(existing.getPoints())) {
				throw new AttributeUpdateException("Attribute: points cannot be updated");
			}
			
			boolean dirty = false;
			if(entityUpdates.getUsername() != null) {
				existing.setUsername(entityUpdates.getUsername());
				dirty = true;
			}
			if(entityUpdates.getAvatar() != null) {
				existing.setAvatar(entityUpdates.getAvatar());
				dirty = true;
			}
			if(entityUpdates.getRole() != null) {
				existing.setRole(entityUpdates.getRole());
				dirty = true;
			}
			if(dirty) {
				this.users.put(existing.getEmail(), existing);
			}
		}
	}

	@Override
	public String generateUserVerificationCode(UserEntity userEntity) {
		//TODO: maybe use a more sophisticated implementation like:
		//return userEntity.hashCode() + "";
		return userEntity.getEmail().toString() + "007";
	}
	
	private boolean isRoleExists(String givenRole) {
		Roles roles[] = Roles.values();
		
		boolean existsRole = false;
		for(Roles role: roles) {
			if(givenRole.equalsIgnoreCase(role.name()))
				existsRole = true;
		}
		return existsRole; 
	}

	@Override
	public void cleanup() {
		this.users.clear();
		this.userVerificationCodes.clear();
	}

	@Override
	public UserEntity verifyUserRegistration(String playground, String email, String verificationCode)
			throws UserDoesNotExistException, VerificationCodeMismatchException, CodeDoesNotExistException {
		synchronized (verificationCode) {
			UserEntity existing = this.getUserByEmail(email);
			if (!verificationCode.equals(this.userVerificationCodes.get(email))) {
				throw new VerificationCodeMismatchException(
						"Code: " + verificationCode + " does not match the code provided to email: " + email);
			}
			existing.setVerifiedUser(true);
			this.users.put(email, existing);
			return getUserByEmail(email);
		}
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}
}
