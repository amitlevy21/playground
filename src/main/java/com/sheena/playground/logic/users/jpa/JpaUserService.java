package com.sheena.playground.logic.users.jpa;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.aop.MyLog;
import com.sheena.playground.dal.UserDao;
import com.sheena.playground.dal.VerificationCodeDao;
import com.sheena.playground.logic.elements.AttributeUpdateException;
import com.sheena.playground.logic.users.CodeDoesNotExistException;
import com.sheena.playground.logic.users.RoleDoesNotExistException;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UnverifiedUserActionException;
import com.sheena.playground.logic.users.UserAlreadyExistsException;
import com.sheena.playground.logic.users.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.UserDoesNotExistException;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.VerificationCodeEntity;
import com.sheena.playground.logic.users.VerificationCodeMismatchException;

@Service
public class JpaUserService implements UsersService{
	private UserDao userDao;
	private VerificationCodeDao VerificationCodeDao;
	
	@Autowired
	public JpaUserService(UserDao userDao, VerificationCodeDao verificationCodeDao) {
		this.userDao = userDao;
		this.VerificationCodeDao = verificationCodeDao;
	}

	@Override
	@Transactional
	@MyLog
	public UserEntity createNewUser(UserEntity userEntity)
			throws UserAlreadyExistsException, RoleDoesNotExistException {
		if(!this.userDao.existsById(userEntity.getEmail())) {
			if(isRoleExists(userEntity.getRole())) {
				
				//Generating a verification code and persisting it
				String verificationCode = generateUserVerificationCode(userEntity);
				this.VerificationCodeDao.save(new VerificationCodeEntity(
						verificationCode, 
						userEntity.getEmail()));
				
				//Set PK for this tuple to be persisted
				userEntity.setCombinedId(userEntity.getEmail() + userEntity.getPlayground());
				return this.userDao.save(userEntity);
			}
			else {
				throw new RoleDoesNotExistException(
						"Requested role: " + userEntity.getRole() + " does not exist");
			}
		}
		else {
			throw new UserAlreadyExistsException(
					"User with email: " + userEntity.getEmail() + " already exists");
		}
	}

	@SuppressWarnings("static-access")
	@Override
	@MyLog
	public String generateUserVerificationCode(UserEntity userEntity) {
		return userEntity.getEmail() + VerificationCodeDao.SUFFIX;
	}

	@Override
	@MyLog
	public UserEntity verifyUserRegistration(String playground, String email, String verificationCode)
			throws UserDoesNotExistException, VerificationCodeMismatchException, CodeDoesNotExistException, UserAlreadyVerifiedException {
		UserEntity user = getUserByEmail(email);
		
		if(isUserVerified(user)) {
			throw new UserAlreadyVerifiedException(
					"User with email: " + user.getEmail() 
					+ " is already verified");
		}
		
		VerificationCodeEntity codeEntity = this.VerificationCodeDao.findById(verificationCode)
				.orElseThrow(() ->
				new CodeDoesNotExistException(
						"Code: " + verificationCode + " does not exist"));
		
		if(!codeEntity.getUserEmail().equals(email)) {
			throw new VerificationCodeMismatchException(
					"Code: " + verificationCode 
					+ " does not match the code provided to email: " + email);
		}
		
		user.setVerifiedUser(true);
		
		return this.userDao.save(user);
	}

	@Override
	@MyLog
	public UserEntity login(UserEntity userEntity) throws UserDoesNotExistException, UnverifiedUserActionException {
		UserEntity user = getUserByEmail(userEntity.getEmail());
		
		if(!isUserVerified(user)) {
			throw new UnverifiedUserActionException(
					"User with email: " + user.getEmail() 
					+ " cannot perform this action since the account is "
					+ "not verified");
		}
		
		user.setLastLogin(new Date());
		return this.userDao.save(user);
	}

	@Override
	@MyLog
	public void updateUserDetails(String playground, String email, UserEntity entityUpdates)
			throws UserDoesNotExistException, AttributeUpdateException, RoleDoesNotExistException, UnverifiedUserActionException {
		UserEntity user = getUserByEmail(email);
		
		if(!isUserVerified(user)) {
			throw new UnverifiedUserActionException(
					"User with email: " + user.getEmail() 
					+ " cannot perform this action since the account is "
					+ "not verified");
		}
		
		if(!entityUpdates.getEmail().equals(email)) {
			throw new AttributeUpdateException("Attribute: email cannot be updated");
		}
		else if(!entityUpdates.getPlayground().equals(playground)) {
			throw new AttributeUpdateException("Attribute: playground cannot be updated");
		}
		else if(!this.isRoleExists(entityUpdates.getRole())) {
			throw new RoleDoesNotExistException(
					"Requested role: " + entityUpdates.getRole() + " does not exist");
		}
		else if(!user.getPoints().equals(entityUpdates.getPoints())) {
			throw new AttributeUpdateException("Attribute: points cannot be updated");
		}
		else {
			this.userDao.save(entityUpdates);
		}
	}
	
	@Override
	public void cleanup() {
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserEntity getUserByEmail(String email) {
		return (UserEntity) this.userDao.findUserByEmail(email).toArray()[0];
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
	
	private boolean isUserVerified(UserEntity userEntity) {
		return userEntity.isVerifiedUser();
	}
}
