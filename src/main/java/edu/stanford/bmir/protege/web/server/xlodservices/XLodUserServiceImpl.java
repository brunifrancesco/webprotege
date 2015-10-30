package edu.stanford.bmir.protege.web.server.xlodservices;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Provider;

import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.server.metaproject.AuthenticationManager;
import edu.stanford.bmir.protege.web.server.metaproject.AuthenticationManagerImpl;
import edu.stanford.bmir.protege.web.server.metaproject.MetaProjectStore;
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

	@Override
	public void checkForAuthoritiesUserXLod(Request request) {
		// TODO Auto-generated method stub

	}
}
