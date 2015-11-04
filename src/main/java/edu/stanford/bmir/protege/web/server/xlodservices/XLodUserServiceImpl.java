package edu.stanford.bmir.protege.web.server.xlodservices;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.io.BaseEncoding;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;

import edu.stanford.bmir.protege.web.server.metaproject.MetaProjectStore;
import edu.stanford.bmir.protege.web.server.xlodexceptions.UserLoginWPException;
import edu.stanford.bmir.protege.web.server.xlodrestmodels.Request;
import edu.stanford.bmir.protege.web.shared.auth.PasswordDigestAlgorithm;
import edu.stanford.bmir.protege.web.shared.auth.Salt;
import edu.stanford.bmir.protege.web.shared.auth.SaltProvider;
import edu.stanford.bmir.protege.web.shared.auth.SaltedPasswordDigest;
import edu.stanford.bmir.protege.web.shared.user.UserEmailAlreadyExistsException;
import edu.stanford.bmir.protege.web.shared.user.UserNameAlreadyExistsException;
import edu.stanford.bmir.protege.web.shared.user.UserRegistrationException;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

/***
 * Useful methods to interface WP with XLod APi
 * @author Francesco Bruni <brunifrancesco02@gmail.com>
 *
 */
public class XLodUserServiceImpl implements XLodUserService {
	
	private MetaProject metaProject;
	private MetaProjectStore metaProjectStore;
	private static XLodUserServiceImpl instance;
	private PasswordDigestAlgorithm passwordDigestAlgorithm;
	private Salt salt;
    
    @Inject
    public XLodUserServiceImpl(MetaProject metaProject, MetaProjectStore metaProjectStore, PasswordDigestAlgorithm passwordDigestAlgorithm) {
        this.metaProject = metaProject;
        this.metaProjectStore = metaProjectStore;
        this.passwordDigestAlgorithm = passwordDigestAlgorithm;
        this.salt = new SaltProvider().get();
        instance = this;
    }
    
    public static XLodUserServiceImpl getInstance(){
		return instance;
    	
    }
    
    /**
     * Save user whose data came from XLod Server.
     * @param  request a {@link edu.stanford.bmir.protege.web.server.xlodrestmodels.Request} which contains data coming the Http request
     * @throws UserRegistrationException if user is already signed up
     */
	@Override
	public void registerUserByXLod(Request request)throws UserRegistrationException{
			SaltedPasswordDigest saltedPasswordDigest = passwordDigestAlgorithm.getDigestOfSaltedPassword(request.getPassword(), salt);
			
	        checkNotNull(request.getUsername());
	        checkNotNull(request.getEmail());
	        User existingUser = metaProject.getUser(request.getUsername());
	        if (existingUser != null) {
	            throw new UserNameAlreadyExistsException(request.getUsername());
	        }
	        for (User user : metaProject.getUsers()) {
	            if (request.getEmail().equals(user.getEmail())) {
	                throw new UserEmailAlreadyExistsException(request.getEmail());
	            }
	        }
	        
	        User newUser = metaProject.createUser(request.getUsername(), "");
	        newUser.setName(request.getUsername());
	        String encodedPassword = BaseEncoding.base16().lowerCase().encode(saltedPasswordDigest.getBytes());
	        String encodedSalt = BaseEncoding.base16().lowerCase().encode(salt.getBytes());
	        newUser.setDigestedPassword(encodedPassword, encodedSalt);
	        newUser.setEmail(request.getUsername());
	        metaProjectStore.saveMetaProject(metaProject);
	        return;
	    }

	/***
	 * Check for user authorities, querying XLod API
	 * @param request the data coming from incoming http request
	 * @throws {@link UserLoginWPException if user does not exist/not authorized/unexpected error}
	 */
	@Override
	public void checkForAuthoritiesUserXLod(Request request_) throws RequestException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		StringBuffer buffer = new StringBuffer("http://localhost:8080/linkedopendatamatera/api/wp/user?email=");
		buffer.append(request_.getEmail());
		ResponseEntity<edu.stanford.bmir.protege.web.server.xlodrestmodels.Response> data = null;
		try{
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			data = restTemplate.getForEntity(buffer.toString(), edu.stanford.bmir.protege.web.server.xlodrestmodels.Response.class);
		}catch(HttpServerErrorException htpe){
			htpe.printStackTrace();
		}catch (HttpClientErrorException e) {
			if (e.getStatusCode() == org.springframework.http.HttpStatus.FORBIDDEN){
				throw new UserLoginWPException("User seems not authorized to access to WP Editor");
			}
			if (e.getStatusCode() == org.springframework.http.HttpStatus.NOT_FOUND){
				throw new UserLoginWPException("User seems not signed up to XLod");
			}
			return;
		}catch (Exception e) {
			e.printStackTrace();
			throw new UserLoginWPException("Unexpected error in doing request to check user authorities");
		}
	}

	@Override
	public void updateUserXLod(Request request) throws UserPrincipalNotFoundException{
		SaltedPasswordDigest saltedPasswordDigest = passwordDigestAlgorithm.getDigestOfSaltedPassword(request.getPassword(), salt);
		User existingUser = metaProject.getUser(request.getUsername());
        if (existingUser != null) {
        	existingUser.setEmail(request.getEmail());
        	String encodedPassword = BaseEncoding.base16().lowerCase().encode(saltedPasswordDigest.getBytes());
            String encodedSalt = BaseEncoding.base16().lowerCase().encode(salt.getBytes());
            existingUser.setDigestedPassword(encodedPassword, encodedSalt);
            existingUser.setEmail(request.getUsername());
            metaProjectStore.saveMetaProject(metaProject);
        }else{
        	throw new UserPrincipalNotFoundException(request.getUsername());
        }
        
		
	}
}
