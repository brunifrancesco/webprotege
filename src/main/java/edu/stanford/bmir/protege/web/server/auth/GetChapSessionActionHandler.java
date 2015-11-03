package edu.stanford.bmir.protege.web.server.auth;

import com.google.common.base.Optional;
import com.google.gwt.http.client.RequestException;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.metaproject.AuthenticationManager;
import edu.stanford.bmir.protege.web.server.xlodexceptions.UserLoginWPException;
import edu.stanford.bmir.protege.web.server.xlodrestmodels.Request;
import edu.stanford.bmir.protege.web.server.xlodservices.XLodUserService;
import edu.stanford.bmir.protege.web.shared.auth.*;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14/02/15
 */
public class GetChapSessionActionHandler implements ActionHandler<GetChapSessionAction, GetChapSessionResult> {

    private WebProtegeLogger logger;

    private ChapSessionManager chapSessionManager;

    private AuthenticationManager authenticationManager;
    
    private XLodUserService xloduserservice;

    @Inject
    public GetChapSessionActionHandler(ChapSessionManager chapSessionManager,
                                       AuthenticationManager authenticationManager,
                                       WebProtegeLogger logger,
                                       XLodUserService xloduserservice) {
        this.chapSessionManager = checkNotNull(chapSessionManager);
        this.authenticationManager = checkNotNull(authenticationManager);
        this.logger = logger;
        this.xloduserservice = xloduserservice;
    }

    @Override
    public Class<GetChapSessionAction> getActionClass() {
        return GetChapSessionAction.class;
    }

    @Override
    public RequestValidator<GetChapSessionAction> getRequestValidator(GetChapSessionAction action, RequestContext requestContext) {
        return NullValidator.get();
    }

    @Override
    public GetChapSessionResult execute(GetChapSessionAction action, ExecutionContext executionContext) {
        UserId userId = action.getUserId();
        if(userId.isGuest()) {
            logger.info("Attempt at authenticating guest user");
            return new GetChapSessionResult(Optional.<ChapSession>absent());
        }
        Optional<Salt> salt = authenticationManager.getSalt(userId);
        if(!salt.isPresent()) {
            logger.info("Attempt to authenticate non-existing user: %s", userId);
            return new GetChapSessionResult(Optional.<ChapSession>absent());
        }
        
        ChapSession challengeMessage = chapSessionManager.getSession(salt.get());
        //check for authorities querying XLOD
        try{
        	xloduserservice.checkForAuthoritiesUserXLod(new Request(null, action.getUserId().getUserName(), null));
        }catch(UserLoginWPException ulwp){
        	logger.info("Attempt to authenticate user via XLOD API: %s; reason is: %s", userId, ulwp.getReason());
        	ulwp.printStackTrace();
        	return new GetChapSessionResult(Optional.<ChapSession>absent());
        } catch (RequestException e) {
        	logger.info("Attempt to authenticate user via XLOD API: error in doing request!");
			e.printStackTrace();
		}
        return new GetChapSessionResult(Optional.of(challengeMessage));
    }
}
