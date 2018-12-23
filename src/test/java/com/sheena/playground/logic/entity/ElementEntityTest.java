package com.sheena.playground.logic.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.Location;
import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * ElementEntityTest
 */
public class ElementEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ElementTO et;

    @Before
    public void setup() {
        Map<String, Object> att = new HashMap<>();
        att.put("attribute1", new HashMap<>());
        this.et = new ElementTO(
            "sheena",
            new Location(13.0, 24.9),
            "Pen",
            new Date("20/11/18"),
            new Date("19/11/19"),
            "tool",
            att,
            "sheena",
            "123@gmail.com");
    }

    @Test
    public void addNewElementWhenElementCreationDateOlderThanExpirationDateShouldThrowException() throws InvalidExpirationDateException{
        this.thrown.expect(InvalidExpirationDateException.class);
        this.et.toEntity().setExpirationDate(new Date("12/1/11"));

    }

    @Test
    public void compareElementEntityShouldEqual() throws InvalidExpirationDateException {
        Map<String, Object> att = new HashMap<>();
        att.put("attribute1", new HashMap<>());
        ElementTO et2 = new ElementTO(
            "sheena",
            new Location(13.0, 24.9),
            "Pen",
            new Date("20/11/18"),
            new Date("19/11/19"),
            "tool",
            att,
            "sheena",
            "123@gmail.com");

        assertThat(et2.toEntity()).isEqualTo(et.toEntity());
    }

    @Test
    public void compareElementEntityShouldNotEqual() throws InvalidExpirationDateException {
        Map<String, Object> att = new HashMap<>();
        att.put("attribute1", new HashMap<>());
        ElementTO et2 = new ElementTO(
            "sheena",
            new Location(12.0, 24.9),
            "Pen",
            new Date("20/11/18"),
            new Date("19/11/19"),
            "tool",
            att,
            "sheena",
            "123@gmail.com");
        assertThat(et2.toEntity()).isNotEqualTo(et.toEntity());
    }
}