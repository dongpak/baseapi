/**
 * 
 */
package com.churchclerk.demoapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;


/**
 * 
 * @author dongp
 *
 */
@Service
@Slf4j
public class DemoService {

	@Autowired
	private DemoStorage storage;

	@Value("${jwt.secret}")
	private String		secret;

	/**
	 *
	 * @return
	 */
	public Page<? extends Demo> getResources(Pageable pageable, Demo criteria) {

		var page = storage.findAll(new DemoResourceSpec(criteria), pageable);

		return page;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Demo getResource(String id) {

		var entity = storage.findById(id);
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
		var entity = new DemoEntity();

		entity.copy(resource);

		return storage.save(entity);
	}


	/**
	 *
	 * @param resource
	 * @return
	 */
	public Demo updateResource(Demo resource) {
		var optional = storage.findById(resource.getId().toString());

		if (optional.isPresent()) {
			DemoEntity entity = optional.get();

			entity.copyNonNulls(resource);
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
		var optional = storage.findById(id);

		if (optional.isPresent() == false) {
			throw new NotFoundException("No such resource with id: " + id);
		}

		storage.deleteById(id);
		return optional.get();
	}


}
