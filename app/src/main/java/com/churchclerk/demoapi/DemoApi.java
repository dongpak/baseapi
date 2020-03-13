/*

 */
package com.churchclerk.demoapi;

import com.churchclerk.baseapi.BaseApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import java.util.Date;

/**
 *
 */
@Component
@Path("/test")
public class DemoApi extends BaseApi<Demo> {

    private static Logger logger = LoggerFactory.getLogger(DemoApi.class);


    @Autowired
    private DemoService service;

    /**
     *
     */
    public DemoApi() {
        super(logger, Demo.class);
        setReadRoles(Role.ADMIN, Role.CLERK, Role.OFFICIAL, Role.MEMBER, Role.NONMEMbER);
        setUpdateRoles(Role.ADMIN);
        setDeleteRoles(Role.ADMIN);
    }

    /**
     *
     * @return
     */
    protected Demo createCriteria() {
        Demo criteria	= new Demo();

        addBaseCriteria(criteria);

        return criteria;
    }

    @Override
    protected Page<? extends Demo> doGet(Pageable pageable) {

        return service.getResources(pageable, createCriteria());
    }

    @Override
    protected Demo doGet(String id) {

            Demo resource = service.getResource(id);

            return resource;
    }

    @Override
    protected Demo doCreate(Demo resource) {

        resource.setCreatedBy(requesterId);
        resource.setCreatedDate(new Date());
        resource.setUpdatedBy(requesterId);
        resource.setUpdatedDate(new Date());
        return resource;
    }

    @Override
    protected Demo doUpdate(Demo resource) {
        Demo found = service.getResource(id);

        resource.setUpdatedBy(requesterId);
        resource.setUpdatedDate(new Date());

        return service.updateResource(resource);
    }

    @Override
    protected Demo doDelete(String id) {
        return service.deleteResource(id);
    }
}
