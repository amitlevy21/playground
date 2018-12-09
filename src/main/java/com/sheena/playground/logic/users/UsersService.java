package com.sheena.playground.logic.users;

import com.sheena.playground.logic.elements.AttributeUpdateException;

public interface UsersService {
	
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException, RoleDoesNotExistException, RoleDoesNotExistException;
	public String generateUserVerificationCode(UserEntity userEntity);
	public UserEntity verifyUserRegistration(String playground, String email, String verificationCode) throws UserDoesNotExistException, VerificationCodeMismatchException, CodeDoesNotExistException, UserAlreadyVerifiedException;
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException, UnverifiedUserActionException;
	public void updateUserDetails(String playground, String email, UserEntity entityUpdates) throws UserDoesNotExistException, AttributeUpdateException, RoleDoesNotExistException, UnverifiedUserActionException;
	public UserEntity getUserByEmail(String email);
	public void cleanup();
}
