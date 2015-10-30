package edu.stanford.bmir.protege.web.server.xlodservices;

import com.google.inject.ImplementedBy;

import edu.stanford.bmir.protege.web.server.xlodrestmodels.Request;

public interface XLodUserService {
	
	public void registerUserByXLod(Request request);
	
	public void checkForAuthoritiesUserXLod(Request request);
}
