/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     mkaufman@bea.com - initial API and implementation
 *******************************************************************************/
package org.aspectj.org.eclipse.jdt.apt.core.internal.util;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.org.eclipse.jdt.apt.core.internal.env.BaseProcessorEnv;
import org.aspectj.org.eclipse.jdt.core.IPackageFragment;
import org.aspectj.org.eclipse.jdt.core.IPackageFragmentRoot;
import org.aspectj.org.eclipse.jdt.core.JavaModelException;

/**
 * Utility class for dealing with packages, using
 * Eclipse's underlying SearchEngine
 */
public class PackageUtil {
	
	private PackageUtil() {}
	
	public static IPackageFragment[] getPackageFragments(
			final String packageName, 
			final BaseProcessorEnv env) {
		
		List<IPackageFragment> packages = new ArrayList<IPackageFragment>();
		try {
			// The environment caches our package fragment roots
			IPackageFragmentRoot[] roots = env.getAllPackageFragmentRoots();
			for (IPackageFragmentRoot root : roots) {
				IPackageFragment fragment = root.getPackageFragment(packageName);
				if (fragment != null && fragment.exists())
					packages.add(fragment);
			}
		}
		catch (JavaModelException e) {
			return new IPackageFragment[0];
		}
		
		return packages.toArray(new IPackageFragment[packages.size()]);
	}

}