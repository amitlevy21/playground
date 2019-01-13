package com.sheena.playground.logic.users.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.aop.IsExistUser;
import com.sheena.playground.aop.IsUserVerified;
import com.sheena.playground.aop.MyLog;
import com.sheena.playground.dal.UserDao;
import com.sheena.playground.dal.VerificationCodeDao;
import com.sheena.playground.logic.elements.exceptions.AttributeUpdateException;
import com.sheena.playground.logic.users.Mail;
import com.sheena.playground.logic.users.MailService;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.VerificationCodeEntity;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UnverifiedUserActionException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;

@Service
public class JpaUserService implements UsersService{
	//Dependency injections
	private UserDao userDao;
	private VerificationCodeDao VerificationCodeDao;
	private MailService mailService;
	private String verificationUrlHost;
	private String playgroundName;
	
	//Constants
	private final String VERIFICATION_URL = "/playground/users/confirm/%s/%s/%s";

	@Autowired
	public JpaUserService(
			UserDao userDao, 
			VerificationCodeDao verificationCodeDao, 
			MailService mailService) {
		this.userDao = userDao;
		this.VerificationCodeDao = verificationCodeDao;
		this.mailService = mailService;
	}

	@Value("${user.verification.host}")
	public void setVerificationUrlHost(String verificationUrlHost) {
		this.verificationUrlHost = verificationUrlHost;
	}

	@Value("${playground.name:defaultPlayground}")
	public void setPlaygroundName(String playgroundName) {
		this.playgroundName = playgroundName;
	}

	@Override
	@Transactional
	@MyLog
	public UserEntity createNewUser(UserEntity userEntity)
			throws UserAlreadyExistsException, RoleDoesNotExistException {
		//First  check if a user with such email exists in the playground
		if(this.userDao.findUserByEmailAndPlayground(
				userEntity.getEmail(), this.playgroundName)
				.size() == 0) {
			
			if(isRoleExists(userEntity.getRole())) {
				
				//Generating a verification code and persisting it
				String verificationCode = generateUserVerificationCode(userEntity);
				this.VerificationCodeDao.save(new VerificationCodeEntity(
						verificationCode, 
						userEntity.getEmail()));
				
				//Send an email to the guest with verification link
				Mail verificationEmail = new Mail();
				verificationEmail.setSubject(MailService.VERIFICATION_SUBJECT);
				verificationEmail.setContent(String.format(
						verificationUrlHost + VERIFICATION_URL, 
						userEntity.getPlayground(), 
						userEntity.getEmail(), 
						verificationCode));
				verificationEmail.setTo(userEntity.getEmail());
				
//				mailService.sendMessage(verificationEmail);
        
				//Set PK for this user to be persisted
				userEntity.setId(userEntity.getEmail() + userEntity.getPlayground());
				userEntity.setPlayground(this.playgroundName);
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

	@Override
	@MyLog
	public String generateUserVerificationCode(UserEntity userEntity) {
		return userEntity.getEmail() + VerificationCodeDao.SUFFIX;
	}

	@MyLog
	@Override
	@IsExistUser
	public UserEntity verifyUserRegistration(String playground, String email, String verificationCode)
			throws UserDoesNotExistException, VerificationCodeMismatchException, CodeDoesNotExistException, UserAlreadyVerifiedException {
		UserEntity user = getUserByEmail(email);
		
		if(isUserVerified(user)) {
			throw new UserAlreadyVerifiedException("User with email: " + user.getEmail() + " is already verified");
		}
		
		VerificationCodeEntity codeEntity = (VerificationCodeEntity) this.VerificationCodeDao.findByCode(verificationCode).toArray()[0];
		if(codeEntity == null)	 
			throw new CodeDoesNotExistException("Code: " + verificationCode + " does not exist");
		
		if(!codeEntity.getUserEmail().equals(email)) {
			throw new VerificationCodeMismatchException(
					"Code: " + verificationCode 
					+ " does not match the code provided to email: " + email);
		}
		
		user.setVerifiedUser(true);
		
		return this.userDao.save(user);
	}

	@MyLog
	@Override
	@IsUserVerified
	public UserEntity login(String userEmail) throws UserDoesNotExistException, UnverifiedUserActionException {
		UserEntity user = getUserByEmail(userEmail);
		
		user.setLastLogin(new Date());
		
		return this.userDao.save(user);
	}

	@MyLog
	@Override
	@IsUserVerified
	public void updateUserDetails(String playground, String email, UserEntity entityUpdates)
			throws UserDoesNotExistException, AttributeUpdateException, RoleDoesNotExistException, UnverifiedUserActionException {
		UserEntity user = getUserByEmail(email);
		
		if(entityUpdates.getEmail() != null && !entityUpdates.getEmail().equals(email)) {
			throw new AttributeUpdateException("Attribute: email cannot be updated");
		}
		else if(entityUpdates.getPlayground()!= null && !entityUpdates.getPlayground().equals(playground)) {
			throw new AttributeUpdateException("Attribute: playground cannot be updated");
		}
		else if(!this.isRoleExists(entityUpdates.getRole())) {
			throw new RoleDoesNotExistException(
					"Requested role: " + entityUpdates.getRole() + " does not exist");
		}
		else if(user.getPoints() != null && !user.getPoints().equals(entityUpdates.getPoints())) {
			throw new AttributeUpdateException("Attribute: points cannot be updated");
		}
		else {
			this.userDao.save(entityUpdates);
		}
	}
	
	@Override
	public void cleanup() {
		userDao.deleteAll();
		VerificationCodeDao.deleteAll();
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserEntity getUserByEmail(String email) throws UserDoesNotExistException {
		List<UserEntity> users = this.userDao.findUserByEmail(email);
		if(users.size() == 0)
			throw new UserDoesNotExistException("no user with email: " + email + " exists");
		return (UserEntity) users.toArray()[0];
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
