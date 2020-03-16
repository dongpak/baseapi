/*
 */
package com.churchclerk.demoapi;


import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.baseapi.model.ApiCaller;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-mock.properties")
public class DemoApiTest {

	private static final String HEADER_AUTHORIZATION	= "Authorization";
	private static final String TOKEN_PREFIX 			= "Bearer ";
	private static final String LOCAL_ADDRESS			= "127.0.0.1";

	@InjectMocks
	private DemoApi					testObject;

	@Mock
	protected HttpServletRequest	testHttpRequest;

	@Mock
	private DemoService testService;

	@Value("${jwt.secret}")
	private String					testSecret;


	private SecurityToken	testToken;
	private Date			testDate;
	private String			testId;
	private Demo			testResource;
	private DemoEntity 		testEntity;
	private Principal		testPrincipal;

	@BeforeEach
	public void setupMock() {

		Mockito.clearInvocations(testHttpRequest);
		Mockito.clearInvocations(testService);

		testDate		= new Date();
		testId			= UUID.randomUUID().toString();
		testResource	= createResource(testId);
		testEntity		= new DemoEntity();

		if (createToken(testId, LOCAL_ADDRESS) == false) {
			throw new RuntimeException("Error creating security token");
		};

		testPrincipal   = new UsernamePasswordAuthenticationToken(null, testToken, null);

		ReflectionTestUtils.setField(testObject, "secret", testSecret);

		Mockito.when(testHttpRequest.getHeader(HEADER_AUTHORIZATION)).thenReturn(TOKEN_PREFIX+testToken.getJwt());
		Mockito.when(testHttpRequest.getRemoteAddr()).thenReturn(LOCAL_ADDRESS);
		Mockito.when(testHttpRequest.getUserPrincipal()).thenReturn(testPrincipal);
	}

	private Demo createResource(String name) {
		Demo	resource = new Demo();

		resource.setId(testId);
		resource.setTestData(resource.getId());
		resource.setActive(true);
		return resource;
	}

	private boolean createToken(String id, String location) {
		testToken = new SecurityToken();

		testToken.setId(id + "|");
		testToken.setRoles(ApiCaller.Role.SUPER.name());
		testToken.setLocation(location);
		testToken.setSecret(testSecret);

		return SecurityApi.process(testToken);
	}

	@Test
	@Order(0)
	public void contexLoads() throws Exception {
		Assertions.assertThat(testObject).isNotNull();
	}

	@Test
	public void testGetResources() throws Exception {
		ReflectionTestUtils.setField(testObject, "sortBy", "street");

		Mockito.when(testService.getResources(null, null)).thenReturn(null);

		Response response = testObject.getResources();

		Assertions.assertThat(response.getEntity()).isNull();
	}


	@Test
	public void testGetResource() throws Exception {

		ReflectionTestUtils.setField(testObject, "id", testId);

		Mockito.when(testService.getResource(testId)).thenReturn(null);

		Response response = testObject.getResource();

		Assertions.assertThat(response.getEntity()).isNull();
	}

	@Test
	public void testCreateResource() throws Exception {

		testResource.setId(null);
		Mockito.when(testService.createResource(testResource)).thenReturn(testResource);

		Response response = testObject.createResource(testResource);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatus()).isBetween(200, 299);
		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isInstanceOf(Demo.class);

		Demo actual = (Demo) response.getEntity();

		Assertions.assertThat(actual.isActive()).isEqualTo(true);
		Assertions.assertThat(actual.getCreatedBy()).isEqualTo(testId);
		Assertions.assertThat(actual.getCreatedDate()).isAfterOrEqualTo(testDate);
		Assertions.assertThat(actual.getUpdatedBy()).isEqualTo(testId);
		Assertions.assertThat(actual.getUpdatedDate()).isAfterOrEqualTo(testDate);

	}

	@Test
	public void testUpdateResource() throws Exception {

		ReflectionTestUtils.setField(testObject, "id", testId);

		Mockito.when(testService.getResource(testId)).thenReturn(testResource);
		Mockito.when(testService.updateResource(testResource)).thenReturn(testResource);

		testResource.setActive(false);
		Response response = testObject.updateResource(testResource);

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isInstanceOf(Demo.class);

		Demo actual = (Demo) response.getEntity();

		Assertions.assertThat(actual.isActive()).isEqualTo(false);
		Assertions.assertThat(actual.getUpdatedBy()).isEqualTo(testId);
		Assertions.assertThat(actual.getUpdatedDate()).isAfterOrEqualTo(testDate);
	}

	@Test
	public void testUpdateResourceNotExist() throws Exception {
		ReflectionTestUtils.setField(testObject, "id", testId);

		Mockito.when(testService.updateResource(testResource)).thenReturn(null);

		Response response = testObject.updateResource(testResource);

		Assertions.assertThat(response.getEntity()).isNull();
	}

	@Test
	public void testDeleteResource() throws Exception {
		ReflectionTestUtils.setField(testObject, "id", testId);

		Mockito.when(testService.deleteResource(testId)).thenReturn(testResource);

		Response response = testObject.deleteResource();

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isEqualTo(testResource);
	}
}
