package com.sheena.playground.logic.stubs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sheena.playground.logic.UserAlreadyExistsException;
import com.sheena.playground.logic.UserDoesNotExistException;
import com.sheena.playground.logic.UserEntity;
import com.sheena.playground.logic.UsersService;
import com.sheena.playground.logic.VerificationCodeMismatchException;

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
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException {
		String email = userEntity.getEmail();
		
		if(this.users.containsKey(email)) {
			throw new UserAlreadyExistsException("There is already a registered user with email: " + email);
		}
		
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
	public void updateUserDetails(String email, UserEntity entityUpdates) throws UserDoesNotExistException {
		synchronized (this.users) {
			UserEntity existing = this.getUser(email);
			
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
		return userEntity.hashCode() + "";
	}
	
	public UserEntity getUser(String email) throws UserDoesNotExistException {
		UserEntity existing = this.users.get(email);
		if (existing == null) {
			throw new UserDoesNotExistException("No user with email: " + email);
		}
		return existing;
	}
}
