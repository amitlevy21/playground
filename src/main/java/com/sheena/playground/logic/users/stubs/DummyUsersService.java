package com.sheena.playground.logic.users.stubs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sheena.playground.logic.elements.AttributeUpdateException;
import com.sheena.playground.logic.users.RoleDoesNotExistException;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UserAlreadyExistsException;
import com.sheena.playground.logic.users.UserDoesNotExistException;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.VerificationCodeMismatchException;

@Service
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
	public void verifyUserRegistration(String email, String verificationCode) throws UserDoesNotExistException, VerificationCodeMismatchException {
		synchronized (verificationCode) {
			UserEntity existing = this.getUser(email);
			if (!verificationCode.equals(this.userVerificationCodes.get(email))) {
				throw new VerificationCodeMismatchException(
						"Code: " + verificationCode + " does not match the code provided to email: " + email);
			}
			existing.setVerifiedUser(true);
			this.users.put(email, existing);
		}
	}

	@Override
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException {
		UserEntity existing;
		
		synchronized (this.users) {
			existing = this.getUser(userEntity.getEmail());
			existing.setLastLogin(new Date());
			this.users.put(existing.getEmail(), existing);
		}
		return existing;
	}

	@Override
	public void updateUserDetails(String email, UserEntity entityUpdates) throws UserDoesNotExistException, AttributeUpdateException {
		synchronized (this.users) {
			UserEntity existing = this.getUser(email);
			
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
	
	public UserEntity getUser(String email) throws UserDoesNotExistException {
		UserEntity existing = this.users.get(email);
		if (existing == null) {
			throw new UserDoesNotExistException("No user with email: " + email);
		}
		return existing;
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
}
