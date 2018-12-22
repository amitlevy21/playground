package com.sheena.playground.aop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sheena.playground.logic.users.RolePrivilageException;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UnverifiedUserActionException;
import com.sheena.playground.logic.users.UserDoesNotExistException;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;

@Component
@Aspect
public class UserAspect {
	private Log log = LogFactory.getLog(UserAspect.class);
	private UsersService usersService;

	@Autowired
	public UserAspect(UsersService usersService) {
		this.usersService = usersService;
	}

	@Around("@annotation(com.sheena.playground.aop.IsExistUser)")
	public Object isExistUser(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check if user with email=" + emailArg + " exists");

		try {
			this.usersService.getUserByEmail(emailArg);
			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new UserDoesNotExistException("no user with email: " + emailArg + " exists");
		}
	}

	@Around("@annotation(com.sheena.playground.aop.IsExistVerifiedUser)")
	public Object isExistVerifiedUser(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check if user with email=" + emailArg + " exists");

		try {
			UserEntity userEntity = this.usersService.getUserByEmail(emailArg);

			log.info("check if user with email=" + emailArg + " is verified");
			log.debug("user=" + userEntity.toString());

			if (!userEntity.isVerifiedUser()) {
				throw new UnverifiedUserActionException("user with email: " + emailArg + " is not verified");
			}
			return joinPoint.proceed();
		} catch (UnverifiedUserActionException e) {
			throw e;
		} catch (Throwable e) {
			throw new UserDoesNotExistException("no user with email: " + emailArg + " exists");
		}
	}

	@Around("@annotation(com.sheena.playground.aop.IsUserPlayer)")
	public Object isUserPlayer(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check if user with email=" + emailArg + " exists");

		try {
			UserEntity userEntity = this.usersService.getUserByEmail(emailArg);

			log.info("check if user with email=" + emailArg + " is verified");
			log.debug("user=" + userEntity.toString());

			if (!userEntity.isVerifiedUser()) {
				throw new UnverifiedUserActionException("user with email: " + emailArg + " is not verified");
			}
			if (!userEntity.getRole().equals(Roles.PLAYER.toString())) {
				throw new RolePrivilageException("user with email: " + emailArg + " is not a player");
			}
			return joinPoint.proceed();
		} catch (UnverifiedUserActionException e) {
			throw e;
		} catch (RolePrivilageException e) {
			throw e;
		} catch (Throwable e) {
			throw new UserDoesNotExistException("no user with email: " + emailArg + " exists");
		}
	}

	@Around("@annotation(com.sheena.playground.aop.IsUserManager)")
	public Object isUserManager(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check if user with email=" + emailArg + " exists");

		try {
			UserEntity userEntity = this.usersService.getUserByEmail(emailArg);

			log.info("check if user with email=" + emailArg + " is verified");
			log.debug("user=" + userEntity.toString());

			if (!userEntity.isVerifiedUser()) {
				throw new UnverifiedUserActionException("user with email: " + emailArg + " is not verified");
			}
			if (!userEntity.getRole().equals(Roles.MANAGER.toString())) {
				throw new RolePrivilageException("user with email: " + emailArg + " is not a manager");
			}
			return joinPoint.proceed();
		} catch (UnverifiedUserActionException e) {
			throw e;
		} catch (RolePrivilageException e) {
			throw e;
		} catch (Throwable e) {
			throw new UserDoesNotExistException("no user with email: " + emailArg + " exists");
		}
	}

	/**
	 * Check if a given string matches the pattern of an email
	 *
	 * @param string a String of some sort
	 * @return Boolean true for strings that match an email pattern, false otherwise
	 * @see https://howtodoinjava.com/regex/java-regex-validate-email-address/
	 */
	private boolean isStringEmail(String string) {
		String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(string);

		return matcher.matches();
	}

	private String getEmailArgForAdvice(ProceedingJoinPoint joinPoint) {
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof String) {
				String s = (String) arg;
				if (isStringEmail(s)) {
					return s;
				}
			}
		}
		return null;
	}
}
