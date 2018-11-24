package com.sheena.playground.logic.dao.stubs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.Location;
import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.dao.ElementDao;
import com.sheena.playground.logic.entity.ElementEntity;

/**
 * ElementFakeDataImp
 */
public class ElementFakeDataImp implements ElementDao {

    private final Location DUMMY_LOCATION = new Location();
    private final Date DUMMY_DATE = new Date();
    private final Map<String, Object> DUMMY_MAP = new HashMap<>();

    {
        DUMMY_MAP.put("1", "2");
    };
    private final ElementTO DUMMY_ELEMENT = new ElementTO("playground", "435", DUMMY_LOCATION, "name", DUMMY_DATE,
            DUMMY_DATE, "type", DUMMY_MAP, "", "");
    private final ElementTO[] DUMMY_ELEMENTS = new ElementTO[] { DUMMY_ELEMENT, DUMMY_ELEMENT, DUMMY_ELEMENT, };

    private Map<String, ElementEntity> idToElement;

    public ElementFakeDataImp() {
        idToElement = new HashMap<>();
        ElementTO dummyElement = new ElementTO("sheena", "123", new Location(13.0, 24.9), "Pen", new Date("20/11/18"),
                new Date("19/11/19"), "tool", new HashMap<String, Object>(), "sheena", "123@gmail.com");
        idToElement.put("123", dummyElement.toEntity());
    }

    @Override
    public Collection<ElementEntity> getAllElements(int size, int page) {
        return idToElement.values().stream().skip(size * page).limit(size).collect(Collectors.toList());
    }

    @Override
    public ElementEntity getElementById(String id) throws ElementNotExistException {
        ElementEntity et = idToElement.get(id);
        if (et == null)
            throw new ElementNotExistException();
        return et;
    }

    @Override
    public boolean removeElementById(String id) {
        return idToElement.remove(id) != null ? true : false;
    }

    @Override
    public void updateElement(ElementEntity element) {
        idToElement.put(element.getId(), element);
    }

    @Override
    public void addElement(ElementEntity element) {
        idToElement.put(element.getId(), element);
    }

    @Override
    public Collection<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance) {
        List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : idToElement.values()) {
            Double distanceX = Math.abs(e.getLocation().getX() - x);
            Double distanceY = Math.abs(e.getLocation().getY() - y);
            if (distance <= distanceX && distance <= distanceY)
                rv.add(e);
        }
        // if (rv.isEmpty())
        // throw new ElementNotExistException();
        return rv;
    }

}