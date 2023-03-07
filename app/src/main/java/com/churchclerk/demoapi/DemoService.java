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

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.UUID;


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

		var entity = storage.findById(UUID.fromString(id));
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
	@Transactional
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
		var optional = storage.findById(resource.getId());

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
		var optional = storage.findById(UUID.fromString(id));

		if (optional.isPresent() == false) {
			throw new NotFoundException("No such resource with id: " + id);
		}

		storage.deleteById(UUID.fromString(id));
		return optional.get();
	}


}
