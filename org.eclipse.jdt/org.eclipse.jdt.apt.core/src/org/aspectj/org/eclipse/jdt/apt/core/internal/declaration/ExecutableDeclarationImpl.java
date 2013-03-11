/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *******************************************************************************/

package org.aspectj.org.eclipse.jdt.apt.core.internal.declaration;

import java.util.Collection;

import org.aspectj.com.sun.mirror.declaration.ExecutableDeclaration;
import org.aspectj.com.sun.mirror.declaration.ParameterDeclaration;
import org.aspectj.com.sun.mirror.declaration.TypeDeclaration;
import org.aspectj.com.sun.mirror.declaration.TypeParameterDeclaration;
import org.aspectj.com.sun.mirror.type.ReferenceType;
import org.aspectj.com.sun.mirror.util.DeclarationVisitor;
import org.aspectj.org.eclipse.jdt.apt.core.internal.env.BaseProcessorEnv;
import org.aspectj.org.eclipse.jdt.apt.core.internal.util.Factory;
import org.aspectj.org.eclipse.jdt.core.dom.IMethodBinding;
import org.aspectj.org.eclipse.jdt.core.dom.ITypeBinding;


public abstract class ExecutableDeclarationImpl 
	extends MemberDeclarationImpl implements ExecutableDeclaration
{
    public ExecutableDeclarationImpl(final IMethodBinding binding, final BaseProcessorEnv env)
    {     
        super(binding, env);
    }

    public void accept(DeclarationVisitor visitor)
    {
        visitor.visitExecutableDeclaration(this);
    }

    public TypeDeclaration getDeclaringType()
    {
        final IMethodBinding methodBinding = getDeclarationBinding();        
        return Factory.createReferenceType(methodBinding.getDeclaringClass(), _env);
    }

    public Collection<TypeParameterDeclaration> getFormalTypeParameters()
    {
    	return ExecutableUtil.getFormalTypeParameters(this, _env);
    }
    public Collection<ParameterDeclaration> getParameters()
    {
    	return ExecutableUtil.getParameters(this, _env);
    }

    public Collection<ReferenceType> getThrownTypes()
    {
    	return ExecutableUtil.getThrownTypes(this, _env);
    }

    public boolean isVarArgs()
    {
        return getDeclarationBinding().isVarargs();
    }

    public String getSimpleName()
    {
		return getDeclarationBinding().getName();
    }

    public IMethodBinding getDeclarationBinding()
    {
        return (IMethodBinding)_binding;
    }

    public boolean isFromSource()
    {
        final ITypeBinding type = getDeclarationBinding().getDeclaringClass();
        return ( type != null && type.isFromSource() );
    }
}
