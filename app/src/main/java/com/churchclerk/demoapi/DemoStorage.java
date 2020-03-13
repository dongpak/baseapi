/**
 * 
 */
package com.churchclerk.demoapi;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


/**
 * 
 * @author dongp
 *
 */
public interface DemoStorage extends CrudRepository<DemoEntity, String>, JpaSpecificationExecutor<DemoEntity> {

}
