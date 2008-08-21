/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.security.services.itest.module;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.eclipse.riena.security.common.authentication.ClientLogin;

/**
 * Test module that implements the JAAS LoginModule interface
 * 
 */
public class ClientRemoteLoginModule implements LoginModule {

	private CallbackHandler callbackHandler;

	String username;
	String password;
	// AuthenticationTicket ticket;
	ClientLogin clientLogin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {
		return clientLogin.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject
	 * , javax.security.auth.callback.CallbackHandler, java.util.Map,
	 * java.util.Map)
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.callbackHandler = callbackHandler;
		this.clientLogin = new ClientLogin("Test", subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("username: ");
		callbacks[1] = new PasswordCallback("password: ", false);
		if (callbackHandler == null) {
			System.out.println("callbackhandler cant be null");
			return false;
		}
		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			password = new String(((PasswordCallback) callbacks[1]).getPassword());
			return clientLogin.login(callbacks);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

}
