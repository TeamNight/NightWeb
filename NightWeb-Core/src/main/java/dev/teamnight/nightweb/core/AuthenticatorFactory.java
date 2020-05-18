/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.servlet.http.HttpSession;

/**
 * @author Jonas
 *
 */
public class AuthenticatorFactory {

	private Constructor<? extends Authenticator> constructor;
	
	public AuthenticatorFactory(Class<? extends Authenticator> authenticatorType) {
		if(Modifier.isAbstract(authenticatorType.getModifiers())) {
			throw new IllegalArgumentException("Authenticator implementation \"" + authenticatorType.getCanonicalName() + "\" is abstract");
		}
		try {
			this.constructor = authenticatorType.getConstructor(Context.class, HttpSession.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Authenticator implementation \""  + authenticatorType.getCanonicalName() + "\" does not have public " + authenticatorType.getSimpleName() + "(Context, HttpSession) constructor");
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Authenticator implementation does not provide public constructor", e);
		}
	}
	
	public Authenticator getAuthenticator(Context context, HttpSession session) {
		Authenticator auth = null;
		try {
			auth = this.constructor.newInstance(context, session);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return auth;
	}
}
