/**
 * 
 */
package com.churchclerk.demoapi;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


/**
 * 
 * @author dongp
 *
 */
public interface DemoStorage extends CrudRepository<DemoEntity, UUID>, JpaSpecificationExecutor<DemoEntity> {

}
