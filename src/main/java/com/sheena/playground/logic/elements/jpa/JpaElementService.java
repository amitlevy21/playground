package com.sheena.playground.logic.elements.jpa;

import java.util.List;

import com.sheena.playground.dal.ElementDao;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.logic.jpa.IdGeneratorDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elementDao;
	private IdGeneratorDao idGenerator;
	
	@Value("${playground.name:defaultPlayground}")
	private String playgroundName;

	@Autowired
	public JpaElementService(ElementDao elementDao, IdGeneratorDao idGenerator) {
		this.elementDao = elementDao;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	public ElementEntity addNewElement(ElementEntity element) {
		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
		Long id = tmp.getId();
		this.idGenerator.delete(tmp);
		
		element.setId("" + id);
		element.setPlayground(playgroundName);
		
		return this.elementDao.save(element);
	}

	@Override
	@Transactional
	public void updateElement(String id, ElementEntity entityUpdates) throws ElementNotExistException {
		ElementEntity existing = this.getElementById(id);
		
		if(entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().equals(existing.getAttributes()))
			existing.setAttributes(entityUpdates.getAttributes());
		if(entityUpdates.getCreationDate() != null & !entityUpdates.getCreationDate().equals(existing.getCreationDate()))
			existing.setCreationDate(entityUpdates.getCreationDate());
		if(entityUpdates.getCreatorEmail() != null && !entityUpdates.getCreatorEmail().equals(existing.getCreatorEmail()))
			existing.setCreatorEmail(entityUpdates.getCreatorEmail());
		if(entityUpdates.getCreatorPlayground() != null && !entityUpdates.getCreatorPlayground().equals(existing.getCreatorPlayground()))
			existing.setCreatorPlayground(entityUpdates.getCreatorPlayground());
		if(entityUpdates.getExpirationDate() != null && !entityUpdates.getExpirationDate().equals(existing.getExpirationDate()))
			existing.setExpirationDate(entityUpdates.getExpirationDate());
		if(entityUpdates.getName() != null && entityUpdates.getName().equals(existing.getName()))
			existing.setName(entityUpdates.getName());
		if(entityUpdates.getType() != null && !entityUpdates.getType().equals(existing.getType()))
			existing.setType(entityUpdates.getType());
		if(entityUpdates.getX() != null && !entityUpdates.getX().equals(existing.getX()))
			existing.setX(entityUpdates.getX());
		if(entityUpdates.getY() != null && !entityUpdates.getY().equals(existing.getY()))
			existing.setY(entityUpdates.getY());
		
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
		Page<ElementEntity> elementsPage = elementDao.findAll(PageRequest.of(page, size));
		return elementsPage.getContent();
	}

	/**
	 * Return 
	 */
	@Override
	public List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance, int size, int page) {
//		int xOffest = (int) Math.abs(x - distance);
//		int yOffest = (int) Math.abs(y - distance);
	
		return elementDao.findByXBetweenAndYBetween(
				x - distance, x + distance, 
				y - distance, y + distance,
				PageRequest.of(page, size));
	}

	@Override
	public List<ElementEntity> getElementsAttribute(String attributeName, Object value, int size, int page)
			throws ElementNotExistException, NoSuceElementAttributeException {
		switch (attributeName) {
		case "name":
			return elementDao.findByNameEquals(value.toString(), PageRequest.of(page, size));
		case "type":
			return elementDao.findByTypeEquals(value.toString(), PageRequest.of(page, size));
		default:
			throw new NoSuceElementAttributeException("attribute: " + attributeName + " is not supported");
		}
	}

	@Override
	public void cleanup() {
		this.elementDao.deleteAll();
		this.idGenerator.deleteAll();
	}
}