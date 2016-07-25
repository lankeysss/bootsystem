package com.casit.suwen.upload;

import java.util.Enumeration;
import java.util.Hashtable;

@SuppressWarnings("unchecked")
public class Request { 
	private Hashtable<Object, Object> m_parameters = new Hashtable<Object, Object>();
	//private int m_counter = 0;

	
	protected void putParameter(String paramString1, String paramString2) {
		if (paramString1 == null)
			throw new IllegalArgumentException(
					"The name of an element cannot be null.");
		Hashtable<Object, Object> localHashtable;
		if (this.m_parameters.containsKey(paramString1)) {
			localHashtable = (Hashtable<Object, Object>) this.m_parameters.get(paramString1);
			localHashtable
					.put(new Integer(localHashtable.size()), paramString2);
		} else {
			localHashtable = new Hashtable<Object, Object>();
			localHashtable.put(new Integer(0), paramString2);
			this.m_parameters.put(paramString1, localHashtable);
			//this.m_counter += 1;
		}
	}

	public String getParameter(String paramString) {
		if (paramString == null) {
			throw new IllegalArgumentException(
					"Form's name is invalid or does not exist (1305).");
		}

		Hashtable<Object, Object> localHashtable = (Hashtable<Object, Object>) this.m_parameters
				.get(paramString);
		if (localHashtable == null)
			return null;
		return (String) localHashtable.get(new Integer(0));
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return this.m_parameters.keys();
	}

	public String[] getParameterValues(String paramString) {
		if (paramString == null) {
			throw new IllegalArgumentException(
					"Form's name is invalid or does not exist (1305).");
		}

		Hashtable<Object, Object> localHashtable = (Hashtable<Object, Object>) this.m_parameters
				.get(paramString);
		if (localHashtable == null)
			return null;
		String[] arrayOfString = new String[localHashtable.size()];
		for (int i = 0; i < localHashtable.size(); i++)
			arrayOfString[i] = ((String) localHashtable.get(new Integer(i)));
		return arrayOfString;
	}
}