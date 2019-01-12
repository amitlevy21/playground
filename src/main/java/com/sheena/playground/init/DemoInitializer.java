package com.sheena.playground.init;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.sheena.playground.aop.MyLog;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;
import com.sheena.playground.logic.activities.ActivityWithNoTypeException;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;
import com.sheena.playground.plugins.attendanceClock.AttendanceClock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class DemoInitializer {

    private UsersService userService;
    private ElementService elementService;
    private ActivityService activityService;

    private UserEntity player;
    private UserEntity manager;

    private ElementEntity messageBoard;
    private ElementEntity shiftManager;
    private ElementEntity attendanceClock;

    private ActivityEntity viewMessages;
    private ActivityEntity postMessage;
    private ActivityEntity registerShift;
    private ActivityEntity declareWorkDay;

    @Autowired
    public DemoInitializer(UsersService userService, ElementService elementService, ActivityService activityService) {
        super();
        this.userService = userService;
        this.elementService = elementService;
        this.activityService = activityService;
    }

    @PostConstruct
    @MyLog
    public void init()
            throws UserAlreadyExistsException, RoleDoesNotExistException, ParseException, UserDoesNotExistException,
            CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {

        createDemoUsers();
        createDemoElements();
        createDemoActivities();

    }

    @MyLog
    public void createDemoUsers()
            throws UserDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException,
            VerificationCodeMismatchException, UserAlreadyExistsException, RoleDoesNotExistException {

        this.player = new UserEntity("user1@gmail.com", "user1", "turtle", "player");
        this.manager = new UserEntity("user3@gmail.com", "user3", "sheep", "manager");

        this.userService.createNewUser(this.player);
        this.player = this.userService.verifyUserRegistration(this.player.getPlayground(), this.player.getEmail(),
                this.player.getEmail() + "code");
        this.userService.createNewUser(this.manager);
        this.manager = this.userService.verifyUserRegistration(this.manager.getPlayground(), this.manager.getEmail(),
                this.manager.getEmail() + "code");

    }

    @MyLog
    public void createDemoElements() throws ParseException {

        Date creationDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");
        Date expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse("2222-01-01");

        Map<String, Object> messageAttributes = new HashMap<>();

        Map<String, Object> shiftFormAttributes = new HashMap<>();
        shiftFormAttributes.put("shiftDate", new Date());
        shiftFormAttributes.put("maxWorkersInShift", 7);

        Map<String, Object> attendanceClockAttributes = new HashMap<>();
        attendanceClockAttributes.put("workDate", new Date());

        // only managers can create elements

        this.messageBoard = new ElementEntity("sheena", 10.0, 5.0, "messageBoard", creationDate, expirationDate,
                "messageBoard", messageAttributes, "sheena", this.manager.getEmail());

        this.shiftManager = new ElementEntity("sheena", 5.0, 3.0, "shiftManager", creationDate, expirationDate, "shift",
                shiftFormAttributes, "sheena", this.manager.getEmail());

        this.attendanceClock = new ElementEntity("sheena", 0.0, 12.0, "attendanceClock", creationDate, expirationDate,
                "attendanceClock", attendanceClockAttributes, "sheena", this.manager.getEmail());

        Stream.of(this.messageBoard, this.shiftManager, this.attendanceClock).forEach(t -> {
            this.elementService.addNewElement(t.getCreatorEmail(), t);
        });

    }

    @MyLog
    public void createDemoActivities() {

        Map<String, Object> postMessageAttributes = new HashMap<>();
        postMessageAttributes.put("text", "Hello World!");

        Map<String, Object> viewMessageAttributes = new HashMap<>();
        viewMessageAttributes.put("page", 0);
        viewMessageAttributes.put("size", 10);

        Map<String, Object> registerShiftFormAttributes = new HashMap<>();
        registerShiftFormAttributes.put("wantedShiftDate", new Date());

        Map<String, Object> attendanceClockAttributes = new HashMap<>();
        attendanceClockAttributes.put("clockingDate", new Date());

        // only players can create activities

        this.postMessage = new ActivityEntity("sheena", this.messageBoard.getId(), "PostMessage",
                this.player.getPlayground(), this.player.getEmail(), postMessageAttributes);

        this.viewMessages = new ActivityEntity("sheena", this.messageBoard.getId(), "ViewMessages",
                this.player.getPlayground(), this.player.getEmail(), viewMessageAttributes);

        this.registerShift = new ActivityEntity("sheena", this.shiftManager.getId(), "RegisterShift",
                this.player.getPlayground(), this.player.getEmail(), registerShiftFormAttributes);

        this.declareWorkDay = new ActivityEntity("sheena", this.attendanceClock.getId(), "Clock",
                this.player.getPlayground(), this.player.getEmail(), attendanceClockAttributes);

        Stream.of(this.postMessage, this.viewMessages, this.registerShift, this.declareWorkDay).forEach(t -> {
            try {
                this.activityService.addNewActivity(t, t.getPlayerPlayground(), t.getPlayerEmail());
            } catch (ActivityTypeNotAllowedException | ActivityWithNoTypeException e) {
                e.printStackTrace();
            }
        });

    }

}