package com.sheena.playground.aop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sheena.playground.api.ElementTO;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RolePrivilageException;
import com.sheena.playground.logic.users.exceptions.UnverifiedUserActionException;

@Component
@Aspect
public class UserAspect {
	private Log log = LogFactory.getLog(UserAspect.class);
	private UsersService usersService;

	@Autowired
	public UserAspect(UsersService usersService) {
		this.usersService = usersService;
	}

	/**
	 * Checks if the email parameter provided to the annotated method is one that
	 * belongs to a guest that requested to register.
	 * <p>
	 * NOTE: The guest's verification status is not cared for
	 *
	 * @param joinPoint a point in around the execution of the wrapped method
	 * @return Object continue to execute the method wrapped by the aspect
	 * @throws Throwable
	 */
	@Before("@annotation(com.sheena.playground.aop.IsExistUser)")
	public void isExistUser(JoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check if user with email=" + emailArg + " exists");

		try {
			this.usersService.getUserByEmail(emailArg);
		} catch (Throwable e) {
			log.error(className + "." + methodName + " throwed exception of type :" + e.getClass().getSimpleName());
			throw e;
		}
	}

	/**
	 * Checks if the email parameter provided to the annotated method is one that
	 * belongs to a registered user that verified it's registration
	 * 
	 * @param joinPoint a point in around the execution of the wrapped method
	 * @return Object continue to execute the method wrapped by the aspect
	 * @throws Throwable
	 */
	@Before("@annotation(com.sheena.playground.aop.IsUserVerified)")
	public void isExistVerifiedUser(JoinPoint joinPoint) throws Throwable {
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
		} catch (Throwable e) {
			log.error(className + "." + methodName + " throwed exception of type :" + e.getClass().getSimpleName());
			throw e;
		}
	}

	/**
	 * Checks if the email parameter provided to the annotated method is one that
	 * belongs to a registered user whos'e role is of a player
	 * 
	 * @param joinPoint a point in around the execution of the wrapped method
	 * @return Object continue to execute the method wrapped by the aspect
	 * @throws Throwable
	 */
	@Before("@annotation(com.sheena.playground.aop.IsUserPlayer)")
	public void isUserPlayer(JoinPoint joinPoint) throws Throwable {
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

			if (!userEntity.isVerifiedUser())
				throw new UnverifiedUserActionException("user with email: " + emailArg + " is not verified");
			
			if (!userEntity.getRole().equalsIgnoreCase(Roles.PLAYER.toString()))
				throw new RolePrivilageException("user with email: " + emailArg + " role is not a player");
			
		} catch (Throwable e) {
			log.error(className + "." + methodName + " throwed exception of type :" + e.getClass().getSimpleName());
			throw e;
		}
	}

	/**
	 * Checks if the email parameter provided to the annotated method is one that
	 * belongs to a registered user whos'e role is of a manager
	 * 
	 * @param joinPoint a point in around the execution of the wrapped method
	 * @return Object continue to execute the method wrapped by the aspect
	 * @throws Throwable
	 */
	@Before("@annotation(com.sheena.playground.aop.IsUserManager)")
	public void isUserManager(JoinPoint joinPoint) throws Throwable {
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

			if (!userEntity.isVerifiedUser())
				throw new UnverifiedUserActionException("user with email: " + emailArg + " is not verified");

			if (!userEntity.getRole().equalsIgnoreCase(Roles.MANAGER.toString()))
				throw new RolePrivilageException("user with email: " + emailArg + " is not a manager");

		} catch (Throwable e) {
			log.error(className + "." + methodName + " throwed exception of type :" + e.getClass().getSimpleName());
			throw e;
		}
	}

	@Around("@annotation(com.sheena.playground.aop.FilterElementsByRole)")
	public Object filterElementsByRole(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		String emailArg = getEmailArgForAdvice(joinPoint);
		if (emailArg == null) {
			throw new RuntimeException(className + "." + methodName + " is missing arg: email");
		}

		log.info("check role of user with email: " + emailArg);

		boolean filterFlag = false;

		try {
			UserEntity userEntity = this.usersService.getUserByEmail(emailArg);
			String role = userEntity.getRole();
			if (role.equalsIgnoreCase(Roles.PLAYER.toString()))
				filterFlag = true;

			Object rv = joinPoint.proceed();
			if (!filterFlag)
				return rv;
			else {
				ElementTO[] elements = (ElementTO[]) rv;
				List<ElementTO> elementsList = new ArrayList<>();
				for (int i = 0; i < elements.length; i++) {
					if (elements[i].getExpirationDate().after(new Date()))
						elementsList.add(elements[i]);
				}
				return elementsList.toArray(new ElementTO[0]);
			}
		} catch (Throwable e) {
			log.error(methodName + " - end with error" + e.getClass().getName());
			throw e;
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

	private String getEmailArgForAdvice(JoinPoint joinPoint) {
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
