/**
 * 
 */
package com.churchclerk.baseapi;

import com.churchclerk.baseapi.model.ApiCaller;
import com.churchclerk.baseapi.model.BaseModel;
import com.churchclerk.securityapi.SecurityToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;


/**
 * 
 * @author dongp
 *
 */
public abstract class BaseApi<R extends BaseModel> {

	private Logger logger;

	@Context
	protected HttpServletRequest httpRequest;

	@Value("${jwt.secret}")
	private String	secret;

	@PathParam("id")
	protected String id;

	@DefaultValue("0")
	@QueryParam("page")
	protected int page = 0;

	@DefaultValue("20")
	@QueryParam("size")
	protected int size = 20;

	@QueryParam("active")
	protected Boolean active;

	@QueryParam("sortBy")
	protected String sortBy;

	protected ApiCaller	apiCaller	= null;

	private ApiCaller.Role[]	readRoles		= new ApiCaller.Role[0];
	private ApiCaller.Role[]	createRoles		= new ApiCaller.Role[0];
	private ApiCaller.Role[]	updateRoles		= new ApiCaller.Role[0];
	private ApiCaller.Role[]	deleteRoles		= new ApiCaller.Role[0];

	private Class<R>	resourceClass		= null;

	/**
	 * 
	 * @param logger
	 */
	public BaseApi(Logger logger, Class<R> resourceClass) {

		this.logger 		= logger;
		this.resourceClass	= resourceClass;
	}

	/**
	 *
	 * @param roles
	 */
	protected void setReadRoles(ApiCaller.Role... roles) {
		this.readRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setCreateRoles(ApiCaller.Role... roles) {
		this.createRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setUpdateRoles(ApiCaller.Role... roles) {
		this.updateRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setDeleteRoles(ApiCaller.Role... roles) {
		this.deleteRoles = roles;
	}

	/**
	 *
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResources() {
		try {
			parseApiCallerInfo();
			Pageable pageable = PageRequest.of(page, size, createSort());

			return Response.ok(doGet(pageable)).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @return
	 */
	protected Sort createSort() {
		List<Sort.Order> list = new ArrayList<Sort.Order>();

		if (sortBy != null) {
			for (String item : sortBy.split(",")) {
				Sort.Direction 	dir 	= Sort.Direction.ASC;
				String			field	= item;

				if (item.startsWith("-")) {
					dir		= Sort.Direction.DESC;
					field 	= item.substring(1);
				}
				else if (item.startsWith("+")) {
					field 	= item.substring(1);
				}

				list.add(new Sort.Order(dir, field));
			}
		}

		return Sort.by(list);
	}

	/**
	 *
	 * @param pageable
	 * @return
	 */
	protected Page<? extends R>  doGet(Pageable pageable) {
		return null;
	}

	/**
	 *
	 * @return
	 */
	protected void addBaseCriteria(R criteria) {

		criteria.setActive(true);
		if (active != null) {
			criteria.setActive(active.booleanValue());
		}
	}


	/**
	 *
	 * @return
	 */
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResource() {
		try {
			parseApiCallerInfo();
			return Response.ok(doGet()).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @return
	 */
	protected R doGet() {
		return null;
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createResource(R resource) {

		try {
			parseApiCallerInfo();
			return Response.ok(doCreate(resource)).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	protected R doCreate(R resource) {
		return resource;
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	@PUT
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateResource(R resource) {
		try {
			parseApiCallerInfo();
			return Response.ok(
					doUpdate(resource)
			).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	protected R doUpdate(R resource) {
		return resource;
	}

	/**
	 *
	 * @return
	 */
	@DELETE
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteResource() {

		try {
			parseApiCallerInfo();
			return Response.ok(doDelete()).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @return
	 */
	protected R doDelete() {
		return null;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public void parseApiCallerInfo() throws Exception {
		SecurityToken	token = getSecurityToken();

		if (token.getLocation().equals(getRemoteAddr()) == false) {
			logger.info("Invalid location: " + getRemoteAddr());

			throw new ForbiddenException("Invalid location");
		}

		apiCaller = new ApiCaller(token.getId(), token.getRoles());
	}

	private SecurityToken getSecurityToken() {
		Principal principal = httpRequest.getUserPrincipal();

		return (SecurityToken) ((UsernamePasswordAuthenticationToken) principal).getCredentials();
	}

	/**
	 *
	 * @return
	 */
	public String getRemoteAddr() {

		String addr = httpRequest.getHeader("x-forwarded-for");
		if ((addr != null) && (addr.trim().isEmpty() == false)) {
			return addr;
		}

		return httpRequest.getRemoteAddr();
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasSuperRole() {
		return apiCaller.hasSuperRole();
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasAdminRole() {
		return apiCaller.hasAdminRole();
	}

    /**
	 *
	 * @return
	 */
	protected boolean hasClerkRole() {
		return apiCaller.hasClerkRole();
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasOfficialRole() {
		return apiCaller.hasOfficialRole();
	}

    /**
	 *
	 * @return
	 */
	protected boolean hasMemberRole() {
		return apiCaller.hasMemberRole();
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	protected boolean readAllowed(String id) {
		return apiCaller.readAllowed(id, readRoles, null);
	}

	/**
	 *
	 * @param id
	 * @param nullAllowed
	 * @return
	 */
	protected boolean readAllowed(String id, BooleanSupplier nullAllowed) {
		return apiCaller.readAllowed(id, readRoles, nullAllowed);
	}


	/**
	 *
	 * @param id
	 * @return
	 */
	protected boolean createAllowed(String id) {
		return apiCaller.createAllowed(id, createRoles, null);
	}

	/**
	 *
	 * @param id
	 * @param nullAllowed
	 * @return
	 */
	protected boolean createAllowed(String id, BooleanSupplier nullAllowed) {
		return apiCaller.createAllowed(id, createRoles, nullAllowed);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	protected boolean updateAllowed(String id) {
		return apiCaller.updateAllowed(id, updateRoles, null);
	}

	/**
	 *
	 * @param id
	 * @param nullAllowed
	 * @return
	 */
	protected boolean updateAllowed(String id, BooleanSupplier nullAllowed) {
		return apiCaller.updateAllowed(id, updateRoles, nullAllowed);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	protected boolean deleteAllowed(String id) {
		return apiCaller.deleteAllowed(id, deleteRoles, null);
	}

	/**
	 *
	 * @param id
	 * @param nullAllowed
	 * @return
	 */
	protected boolean deleteAllowed(String id, BooleanSupplier nullAllowed) {
		return apiCaller.deleteAllowed(id, deleteRoles, nullAllowed);
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	protected Response generateErrorResponse(Throwable t) {
		Response r = null;
		
		if (t instanceof NotAuthorizedException) {
			r = Response.status(Status.UNAUTHORIZED).build();
		}
		else if (t instanceof NotFoundException) {
			r = Response.status(Status.NOT_FOUND).build();
		}
		else {
			r = Response.serverError().build();
			logger.error("Generating " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase() + " for "+ t, t);
		}

		return r;
	}


}
