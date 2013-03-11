/*******************************************************************************
 * Copyright (c) 2005, 2011 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   wharley - initial API and implementation
 *******************************************************************************/

package org.aspectj.org.eclipse.jdt.apt.core.internal.util;

import java.util.Map;

import org.aspectj.org.eclipse.jdt.apt.core.internal.AptPlugin;
import org.aspectj.org.eclipse.jdt.apt.core.util.AptPreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * This class is used to extend org.eclipse.core.runtime.preferences.
 */
public class AptCorePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
	 	IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE.getNode(AptPlugin.PLUGIN_ID);
	 	for (Map.Entry<String,String> entry : AptPreferenceConstants.DEFAULT_OPTIONS_MAP.entrySet()) {
	 		defaultPreferences.put(entry.getKey(), entry.getValue());
	 	}
	}

}
