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
package org.eclipse.riena.internal.security.server;

import java.util.Hashtable;

import org.eclipse.riena.communication.core.hooks.IServiceHook;
import org.eclipse.riena.core.RienaPlugin;
import org.eclipse.riena.core.cache.GenericObjectCache;
import org.eclipse.riena.core.cache.IGenericObjectCache;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends RienaPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.compeople.scp.security.server"; //$NON-NLS-1$

	private ServiceRegistration principalCacheRegistration;
	private ServiceRegistration securityServiceHook;

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.plugin = this;
		GenericObjectCache principalCache = new GenericObjectCache();
		principalCache.setName("principalCache"); //$NON-NLS-1$
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("cache.type", "PrincipalCache"); //$NON-NLS-1$ //$NON-NLS-2$
		principalCacheRegistration = context
				.registerService(IGenericObjectCache.class.getName(), principalCache, props);
		securityServiceHook = context.registerService(IServiceHook.class.getName(), new SecurityServiceHook(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		principalCacheRegistration.unregister();
		securityServiceHook.unregister();
		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Get the plugin instance.
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
