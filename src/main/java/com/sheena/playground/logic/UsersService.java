package com.sheena.playground.logic;

public interface UsersService {
	
	public UserEntity createNewUser(UserEntity userEntity) throws UserAlreadyExistsException;
	public String generateUserVerificationCode(UserEntity userEntity);
	public void verifyUserRegistration(String email, String verificationCode) throws UserDoesNotExistException, VerificationCodeMismatchException;
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException;
	public void updateUserDetails(String email, UserEntity entityUpdates) throws UserDoesNotExistException;
	public UserEntity getUser(String email) throws UserDoesNotExistException;
}
