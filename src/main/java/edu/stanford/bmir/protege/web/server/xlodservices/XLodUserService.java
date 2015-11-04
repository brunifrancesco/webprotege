package edu.stanford.bmir.protege.web.server.xlodservices;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import com.google.gwt.http.client.RequestException;

import edu.stanford.bmir.protege.web.server.xlodrestmodels.Request;
import edu.stanford.bmir.protege.web.shared.user.UserRegistrationException;

public interface XLodUserService {
	
	/**
     * Save user whose data came from XLod Server.
     * @param  request a {@link edu.stanford.bmir.protege.web.server.xlodrestmodels.Request} which contains data coming the Http request
     * @throws UserRegistrationException if user is already signed up
     */
	public void registerUserByXLod(Request request);
	
	/***
	 * Check for user authorities, querying XLod API
	 * @param request the data coming from incoming http request
	 * @throws {@link UserLoginWPException if user does not exist/not authorized/unexpected error}
	 */
	public void checkForAuthoritiesUserXLod(Request request) throws RequestException;
	
	/***
	 * Update WP user with data coming from XLod
	 * @param request the data coming from incoming http request
	 * @throws UserPrincipalNotFoundException 
	 * @throws {@link UserLoginWPException if user does not exist/not authorized/unexpected error}
	 */
	public void updateUserXLod(Request request) throws RequestException, UserPrincipalNotFoundException;
	
	
}
