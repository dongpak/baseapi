/**
 * 
 */
package com.churchclerk.baseapi;

import com.churchclerk.basemodel.BaseModel;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author dongp
 *
 */
public abstract class BaseApi<R extends BaseModel> {

	public enum Role {
		SUPER, ADMIN, CLERK, OFFICIAL, MEMBER, NONMEMbER
	}

	private Logger logger;

	@Context
	protected HttpServletRequest httpRequest;

	@Value("${jwt.secret}")
	private String	secret;

	//@PathParam("id")
	protected String id;

	//@DefaultValue("0")
	//@QueryParam("page")
	protected int page = 0;

	//@DefaultValue("20")
	//@QueryParam("size")
	protected int size = 20;

	//@QueryParam("active")
	protected Boolean active;

	//@QueryParam("sortBy")
	protected String sortBy;

	protected SecurityToken	authToken			= null;
	protected String		requesterId			= null;
	protected String		requesterChurchId	= null;

	private Role[]		readRoles			= new Role[0];
	private Role[]		createRoles			= new Role[0];
	private Role[]		updateRoles			= new Role[0];
	private Role[]		deleteRoles			= new Role[0];

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
	protected void setReadRoles(Role... roles) {
		this.readRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setCreateRoles(Role... roles) {
		this.createRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setUpdateRoles(Role... roles) {
		this.updateRoles = roles;
	}

    /**
	 *
	 * @param roles
	 */
	protected void setDeleteRoles(Role... roles) {
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
			verifyToken();
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
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResource() {
		try {
			verifyToken();
			return Response.ok(doGet(id)).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	protected R doGet(String id) {
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
			verifyToken();
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
			verifyToken();
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
			verifyToken();
			return Response.ok(doDelete(id)).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	protected R doDelete(String id) {
		return null;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public SecurityToken verifyToken() throws Exception {

		SecurityToken	token	= new SecurityToken();
		String 			auth 	= httpRequest.getHeader("Authorization");

		if (auth == null) {
			logger.info("Authorization header required");
			throw new NotAuthorizedException("Authorization reuired");
		}

		token.setSecret(secret);
		token.setJwt(auth.substring(7));

		if (SecurityApi.process(token) == true) {
			if (token.expired()) {
				logger.info("Token expired");
				throw new NotAuthorizedException("Token expired");
			}

			if (token.getLocation().equals(getRemoteAddr()) == false) {
				logger.info("Invalid location: " + getRemoteAddr());

				throw new NotAuthorizedException("Invalid location");
			}

			authToken 	= token;

			parseRequesterId();
			return token;
		}

		throw new NotAuthorizedException("Bad token");
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

	private void parseRequesterId() {
		String[]	items = authToken.getId().split("\\|");

		requesterId			= items[0];

		if (items.length > 1) {
			requesterChurchId = items[1];
		}
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasSuperRole() {
		return authToken.getRoles().contains(Role.SUPER.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasAdminRole() {
		return authToken.getRoles().contains(Role.ADMIN.name());
	}

    /**
	 *
	 * @return
	 */
	protected boolean hasClerkRole() {
		return authToken.getRoles().contains(Role.CLERK.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasOfficialRole() {
		return authToken.getRoles().contains(Role.CLERK.name()) || authToken.getRoles().contains(Role.OFFICIAL.name());
	}

    /**
	 *
	 * @return
	 */
	protected boolean hasMemberRole() {
		return authToken.getRoles().contains(Role.MEMBER.name());
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	private boolean operationAllowed(String churchId, Role[] roles) {
		if (hasSuperRole()) {
			return true;
		}

		if ((requesterChurchId != null) && (requesterChurchId.equals(churchId))) {
			for (Role role : roles) {
				if (authToken.getRoles().contains(role.name())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean readAllowed(String churchId) {
		return operationAllowed(churchId, readRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean createAllowed(String churchId) {
		return operationAllowed(churchId, createRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean updateAllowed(String churchId) {
		return operationAllowed(churchId, updateRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean deleteAllowed(String churchId) {
		return operationAllowed(churchId, deleteRoles);
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
