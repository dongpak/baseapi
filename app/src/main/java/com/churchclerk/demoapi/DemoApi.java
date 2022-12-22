/*

 */
package com.churchclerk.demoapi;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.baseapi.model.ApiCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
@Component
@Path("/demo")
@Slf4j
public class DemoApi extends BaseApi<Demo> {

    @QueryParam("testData")
    private String testDataLike;


    @Autowired
    private DemoService service;

    /**
     *
     */
    public DemoApi() {
        super(Demo.class);
        setReadRoles(ApiCaller.Role.ADMIN, ApiCaller.Role.CLERK, ApiCaller.Role.OFFICIAL, ApiCaller.Role.MEMBER);
        setUpdateRoles(ApiCaller.Role.ADMIN);
        setDeleteRoles(ApiCaller.Role.ADMIN);
    }

    @Override
    protected Page<? extends Demo> doGet(Pageable pageable) {
        return service.getResources(pageable, createCriteria());
    }

    /**
     *
     * @return
     */
    protected Demo createCriteria() {
        var criteria	= new Demo();

        addBaseCriteria(criteria);

        criteria.setTestData(testDataLike);
        if (readAllowed("testmember") == false) {
            criteria.setId(UUID.randomUUID().toString());
        }

        return criteria;
    }

    @Override
    protected Demo doGet() {

        if ((id == null) || (id.trim().isEmpty())) {
            throw new BadRequestException("Resource id cannot be empty");
        }

        if (readAllowed(id.trim()) == false) {
            throw new ForbiddenException();
        }

        var resource = service.getResource(id.trim());

        return resource;
    }

    @Override
    protected Demo doCreate(Demo resource) {

        if (resource.getId() != null) {
            throw new BadRequestException("Resource id should not be present");
        }

        if (createAllowed(null) == false) {
            throw new ForbiddenException();
        }

        resource.setId(UUID.randomUUID().toString());

//        resource.setCreatedBy(apiCaller.getUserid());
//        resource.setCreatedDate(new Date());
//        resource.setUpdatedBy(apiCaller.getUserid());
//        resource.setUpdatedDate(new Date());

        return service.createResource(resource);
    }

    @Override
    protected Demo doUpdate(Demo resource) {
        if ((id == null) || id.isEmpty() || resource.getId() == null || resource.getId().isEmpty()) {
            throw new BadRequestException("Resource id cannot be empty");
        }

        if (resource.getId().equals(id) == false) {
            throw new BadRequestException("Resource id does not match");
        }

        if (updateAllowed(id) == false) {
            throw new ForbiddenException();
        }

        resource.setUpdatedBy(apiCaller.getUserid());
        resource.setUpdatedDate(new Date());

        return service.updateResource(resource);
    }

    @Override
    protected Demo doDelete() {
        if ((id == null) || id.isEmpty()) {
            throw new BadRequestException("Resource id cannot be empty");
        }

        if (deleteAllowed(id) == false) {
            throw new ForbiddenException();
        }

        return service.deleteResource(id);
    }
}
