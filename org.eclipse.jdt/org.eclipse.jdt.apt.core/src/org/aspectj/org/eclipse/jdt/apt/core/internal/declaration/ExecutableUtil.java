/*******************************************************************************
 * Copyright (c) 2005, 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *******************************************************************************/
package org.aspectj.org.eclipse.jdt.apt.core.internal.declaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.aspectj.com.sun.mirror.declaration.ParameterDeclaration;
import org.aspectj.com.sun.mirror.declaration.TypeDeclaration;
import org.aspectj.com.sun.mirror.declaration.TypeParameterDeclaration;
import org.aspectj.com.sun.mirror.type.ReferenceType;
import org.aspectj.org.eclipse.jdt.apt.core.internal.declaration.EclipseMirrorObject.MirrorKind;
import org.aspectj.org.eclipse.jdt.apt.core.internal.env.BaseProcessorEnv;
import org.aspectj.org.eclipse.jdt.apt.core.internal.util.Factory;
import org.aspectj.org.eclipse.jdt.core.dom.IMethodBinding;
import org.aspectj.org.eclipse.jdt.core.dom.ITypeBinding;
import org.aspectj.org.eclipse.jdt.core.dom.Name;
import org.aspectj.org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.aspectj.org.eclipse.jdt.core.dom.TypeParameter;


class ExecutableUtil {

	/**
	 * @param executable must be a constructor, method or annotation element.
	 * @return the formal type parameters of the executable. 
	 */
	static Collection<TypeParameterDeclaration> getFormalTypeParameters(
			EclipseDeclarationImpl executable,
			BaseProcessorEnv env)
	{			
		// the dom ast does not provide type parameter list for annotation element
		// that incorrectly includes them in the text
		if(executable == null || executable.kind() == MirrorKind.ANNOTATION_ELEMENT)
			return Collections.emptyList();
		if( executable.kind() != MirrorKind.METHOD && executable.kind() != MirrorKind.CONSTRUCTOR)
			throw new IllegalArgumentException("Executable is not a method " +  //$NON-NLS-1$
					executable.getClass().getName());
		
		if( executable.isFromSource() ){
			final org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration methodAstNode = 
				(org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration)executable.getAstNode();
			
			// Synthetic methods will have no ast node
			if (methodAstNode == null)
				return Collections.emptyList();
	    	@SuppressWarnings("unchecked")
	    	final List<TypeParameter> typeParams = methodAstNode.typeParameters();
	    	final List<TypeParameterDeclaration> result = new ArrayList<TypeParameterDeclaration>();
	    	for(TypeParameter typeParam : typeParams){
	    		final ITypeBinding typeBinding = typeParam.resolveBinding();
	    		if( typeBinding == null ){
	    			throw new UnsupportedOperationException("cannot create a type parameter declaration without a binding"); //$NON-NLS-1$
	    		}
	    		else{
	    			final TypeParameterDeclaration typeParamDecl = 
	    				(TypeParameterDeclaration)Factory.createDeclaration(typeBinding, env);
	                if( typeParamDecl != null )
	                    result.add(typeParamDecl);
	    		}
	    	}
	    	return result;
		}
		else{ // binary
			if( !executable.isBindingBased() )
				throw new IllegalStateException("binary executable without binding."); //$NON-NLS-1$
			 final IMethodBinding methodBinding = ((ExecutableDeclarationImpl)executable).getDeclarationBinding();
				final ITypeBinding[] typeParams = methodBinding.getTypeParameters();        
		        if( typeParams == null || typeParams.length == 0 )
		            return Collections.emptyList();
		        final List<TypeParameterDeclaration> result = new ArrayList<TypeParameterDeclaration>();
		        for( ITypeBinding typeVar : typeParams ){
		            final TypeParameterDeclaration typeParamDecl = 
		            	(TypeParameterDeclaration)Factory.createDeclaration(typeVar, env);
		            if( typeParamDecl != null )
		                result.add(typeParamDecl);
		        }
		        return result;
			
		}
	}
	
	/**
	 * @param executable must be a constructor, method or annotation element.
	 * @return the list formal parameters of the executable. 
	 */
	static Collection<ParameterDeclaration> getParameters(
			final EclipseDeclarationImpl executable,
			final BaseProcessorEnv env)
	{
		// the dom ast does not provide parameter list for annotation element
		// that incorrectly includes them in the text
		if(executable == null || executable.kind() == MirrorKind.ANNOTATION_ELEMENT)
			return Collections.emptyList();
		if( executable.kind() != MirrorKind.METHOD && executable.kind() != MirrorKind.CONSTRUCTOR)
			throw new IllegalArgumentException("Executable is not a method " +  //$NON-NLS-1$
					executable.getClass().getName());
		
		if( executable.isFromSource() ){
			// We always need to look into the ast to make sure the complete list of
			// parameters are returned since parameters with unresolved type will not 
			// show up in the method binding
			final org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration methodAstNode = 
				(org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration)executable.getAstNode();
			
			// Synthetic methods will have no ast node
			if (methodAstNode == null)
				return Collections.emptyList();
			
	    	@SuppressWarnings("unchecked") 
	    	final List<SingleVariableDeclaration> params = methodAstNode.parameters();
	    	if( params == null || params.size() == 0 )
	    		return Collections.emptyList();  
	    	final List<ParameterDeclaration> result = new ArrayList<ParameterDeclaration>(params.size());
	    	for( int i=0, size=params.size(); i<size; i++ ){   		
	    		final SingleVariableDeclaration varDecl = params.get(i);
	    		final ParameterDeclaration param = 
	    			Factory.createParameterDeclaration(varDecl, executable.getResource(), env);
	    		result.add(param);
	    	}
	        return result;
		}
		else{
			if( !executable.isBindingBased() )
				throw new IllegalStateException("binary executable without binding."); //$NON-NLS-1$
			// it is binary, since we don't support the class file format, will rely on the
			// binding and hope that it's complete.
			final ExecutableDeclarationImpl impl = (ExecutableDeclarationImpl)executable;
			final IMethodBinding methodBinding = impl.getDeclarationBinding();
	        final ITypeBinding[] paramTypes = methodBinding.getParameterTypes();
	        if( paramTypes == null || paramTypes.length == 0 )
	            return Collections.emptyList();        
	        final List<ParameterDeclaration> result = new ArrayList<ParameterDeclaration>(paramTypes.length);        
	        
	        for( int i=0; i<paramTypes.length; i++ ){
	            final ITypeBinding type = paramTypes[i];
	            final ParameterDeclaration param = Factory.createParameterDeclaration(impl, i, type, env);
	            result.add(param);
	        }

	        return result;
			
		}
	}  
	
	/**
	 * @param executable must be a constructor, method or annotation element.
	 * @return the list thrown types of the executable. 
	 */
	static Collection<ReferenceType> getThrownTypes(
			final EclipseDeclarationImpl executable,
			final BaseProcessorEnv env)
	{
		if(executable == null || executable.kind() == MirrorKind.ANNOTATION_ELEMENT)
			return Collections.emptyList();
		if( executable.kind() != MirrorKind.METHOD && executable.kind() != MirrorKind.CONSTRUCTOR)
			throw new IllegalArgumentException("Executable is not a method " +  //$NON-NLS-1$
					executable.getClass().getName());
		if( executable.isFromSource()){
			// We always need to look into the ast to make sure the complete list of
			// parameters are returned since parameters with unresolved type will not 
			// show up in the method binding
			final org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration methodAstNode = 
				(org.aspectj.org.eclipse.jdt.core.dom.MethodDeclaration)executable.getAstNode();
			
			// If this method is synthetic, there will be no AST node
			if (methodAstNode == null) 
				return Collections.emptyList();
			
	    	@SuppressWarnings("unchecked") 
	    	final List<Name> exceptions = methodAstNode.thrownExceptions();
	    	if(exceptions == null || exceptions.size() == 0 )
	    		return Collections.emptyList();
	    	final List<ReferenceType> results = new ArrayList<ReferenceType>(4);
	    	for(Name exception : exceptions ){
	    		final ITypeBinding eType = exception.resolveTypeBinding();
	    		final ReferenceType refType;
	    		if( eType == null || eType.isRecovered() )
	    			refType = Factory.createErrorClassType(exception.toString());
	    		else
	    			refType = Factory.createReferenceType(eType, env);
	    		results.add(refType);
	    	}
	    	
	    	return results;
		}
		else{
			if( !executable.isBindingBased() )
				throw new IllegalStateException("binary executable without binding."); //$NON-NLS-1$
			final ExecutableDeclarationImpl impl = (ExecutableDeclarationImpl)executable;
			final IMethodBinding methodBinding = impl.getDeclarationBinding();			
	        final ITypeBinding[] exceptions = methodBinding.getExceptionTypes();
	        final List<ReferenceType> results = new ArrayList<ReferenceType>(4);
	        for( ITypeBinding exception : exceptions ){
	            final TypeDeclaration mirrorDecl = Factory.createReferenceType(exception, env);
	            if( mirrorDecl != null)
	                results.add((ReferenceType)mirrorDecl);
	        }
	        return results;
		}
	}
}