package com.sheena.playground.logic.elements.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sheena.playground.dal.ElementDao;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.elements.exceptions.ElementAlreadyExistsException;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.logic.jpa.IdGeneratorDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JpaElementService
 */
@Service
public class JpaElementService implements ElementService {

    private ElementDao elementDao;
    private IdGeneratorDao idGenerator;

    @Autowired
    public JpaElementService(ElementDao elementDao, IdGeneratorDao idGenerator) {
        this.elementDao = elementDao;
        this.idGenerator = idGenerator;
    }

    @Override
    @Transactional
    public void addNewElement(ElementEntity element) throws ElementAlreadyExistsException {
        if (!this.elementDao.existsById(element.getName())) {
            IdGenerator tmp = this.idGenerator.save(new IdGenerator());
            Long dummyId = tmp.getId();
            this.idGenerator.delete(tmp);

            element.setDummyId("" + dummyId);
            this.elementDao.save(element);
        } else {
            throw new ElementAlreadyExistsException("element with name " + element.getName() + " exists");
        }
    }

    @Override
    @Transactional
    public void updateElement(String id, ElementEntity entityUpdates) throws ElementNotExistException {
        ElementEntity existing = this.getElementById(id);

        if (entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
            existing.setAttributes(entityUpdates.getAttributes());
        }
        if (entityUpdates.getX() != null && entityUpdates.getX() != existing.getX()) {
            existing.setX(entityUpdates.getX());
        }
        if (entityUpdates.getY() != null && entityUpdates.getY() != existing.getY()) {
            existing.setY(entityUpdates.getY());
        }

        this.elementDao.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ElementEntity getElementById(String id) throws ElementNotExistException {
        return this.elementDao.findById(id)
                .orElseThrow(() -> new ElementNotExistException("no element with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementEntity> getAllElements(int size, int page) {
        List<ElementEntity> allList = new ArrayList<>();
        this.elementDao.findAll().forEach(o -> allList.add(o));

        return allList.stream().skip(size * page).limit(size).collect(Collectors.toList()); // List
    }

    @Override
    public List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
            throws ElementNotExistException {
        List<ElementEntity> elements = getAllElements(10, 0);
        List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : elements) {
            Double distanceX = Math.abs(e.getX() - x);
            Double distanceY = Math.abs(e.getY() - y);
            if (distance.compareTo(distanceX) >= 0 && distance.compareTo(distanceY) >= 0) {
                rv.add(e);
            }
        }
        if (rv.isEmpty())
            throw new ElementNotExistException();
        return rv;

    }

    @Override
    public List<ElementEntity> getElementsAttribute(String attributeName, Object value)
            throws ElementNotExistException {
        List<ElementEntity> elements = getAllElements(10, 0);
        List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : elements) {
            if (e.getAttributes().get(attributeName).toString().equals(value.toString())) {
                rv.add(e);
            }
        }
        if (rv.isEmpty())
            throw new ElementNotExistException();
        return rv;
    }

    @Override
    public void cleanup() {
        this.elementDao.deleteAll();
        this.idGenerator.deleteAll();
    }

}