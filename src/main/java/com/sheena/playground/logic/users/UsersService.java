package com.sheena.playground.logic.users;

import com.sheena.playground.logic.elements.exceptions.AttributeUpdateException;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UnverifiedUserActionException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;

public interface UsersService {
	
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException, RoleDoesNotExistException, RoleDoesNotExistException;
	
	public String generateUserVerificationCode(UserEntity userEntity);
	
	public UserEntity verifyUserRegistration(String playground, String email, String verificationCode) throws UserDoesNotExistException, VerificationCodeMismatchException, CodeDoesNotExistException, UserAlreadyVerifiedException;
	
	public UserEntity login(String userEmail) throws UserDoesNotExistException, UnverifiedUserActionException;
	
	public void updateUserDetails(String playground, String email, UserEntity entityUpdates) throws UserDoesNotExistException, AttributeUpdateException, RoleDoesNotExistException, UnverifiedUserActionException;
	
	public UserEntity getUserByEmail(String email) throws UserDoesNotExistException;
	
	public void cleanup();
}
