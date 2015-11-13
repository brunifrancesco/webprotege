package edu.stanford.bmir.protege.web.server.inject;

import com.google.inject.AbstractModule;

import edu.stanford.bmir.protege.web.server.xlodrestcontrollers.XLodAPIController;
import edu.stanford.bmir.protege.web.server.xlodrestcontrollers.XLodAPIControllerImpl;
import edu.stanford.bmir.protege.web.server.xlodservices.XLodUserService;
import edu.stanford.bmir.protege.web.server.xlodservices.XLodUserServiceImpl;

public class XLODModule extends AbstractModule{

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		bind(XLodUserService.class).to(XLodUserServiceImpl.class).asEagerSingleton();
		bind(XLodAPIController.class).to(XLodAPIControllerImpl.class).asEagerSingleton();
	}

}
