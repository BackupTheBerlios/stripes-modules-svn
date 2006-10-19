package org.syracus.stripes.bsf;

import net.sourceforge.stripes.exception.StripesRuntimeException;

public class ScriptException extends StripesRuntimeException {

	private static final long serialVersionUID = 1L;

	public ScriptException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ScriptException(String arg0) {
		super(arg0);
	}

	public ScriptException(Throwable arg0) {
		super(arg0);
	}

	
	
}
