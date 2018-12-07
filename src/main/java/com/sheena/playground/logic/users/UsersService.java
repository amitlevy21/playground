package com.sheena.playground.logic.users;

import com.sheena.playground.logic.elements.AttributeUpdateException;

public interface UsersService {
	
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException, RoleDoesNotExistException, RoleDoesNotExistException;
	public String generateUserVerificationCode(UserEntity userEntity);
	public void verifyUserRegistration(String email, String verificationCode) throws UserDoesNotExistException, VerificationCodeMismatchException;
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException;
	public void updateUserDetails(String email, UserEntity entityUpdates) throws UserDoesNotExistException, AttributeUpdateException;
	public UserEntity getUser(String email) throws UserDoesNotExistException;
	public void cleanup();
}
