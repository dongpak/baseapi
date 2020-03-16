/**
 * 
 */
package com.churchclerk.demoapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.Optional;


/**
 * 
 * @author dongp
 *
 */
@Service
public class DemoService {

	private static Logger logger	= LoggerFactory.getLogger(DemoService.class);

	@Autowired
	private DemoStorage storage;

	@Value("${jwt.secret}")
	private String		secret;

	/**
	 *
	 * @return
	 */
	public Page<? extends Demo> getResources(Pageable pageable, Demo criteria) {

		Page<DemoEntity> page = storage.findAll(new DemoResourceSpec(criteria), pageable);

		return page;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Demo getResource(String id) {

		Optional<DemoEntity> entity = storage.findById(id);
		if (entity.isPresent() == false) {
			return null;
		}

		return entity.get();
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	public Demo createResource(Demo resource) {
		DemoEntity entity = new DemoEntity();

		entity.copy(resource);

		return storage.save(entity);
	}


	/**
	 *
	 * @param resource
	 * @return
	 */
	public Demo updateResource(Demo resource) {
		Optional<DemoEntity> optional = storage.findById(resource.getId().toString());

		if (optional.isPresent()) {
			DemoEntity entity = optional.get();

			entity.copy(resource);
			return storage.save(entity);
		}

		return resource;
	}


	/**
	 *
	 * @param id
	 * @return
	 */
	public Demo deleteResource(String id) {
		Optional<DemoEntity> optional = storage.findById(id);

		if (optional.isPresent() == false) {
			throw new NotFoundException("No such resource with id: " + id);
		}

		storage.deleteById(id);
		return optional.get();
	}


}
