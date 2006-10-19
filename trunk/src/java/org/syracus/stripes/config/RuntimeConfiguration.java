package org.syracus.stripes.config;

import net.sourceforge.stripes.exception.StripesRuntimeException;

import org.syracus.stripes.bsf.ScriptResolver;

/**
 * Extension to the the default RuntimeConfiguration provided by stripes.
 * This extension allows to seamless integrate new ConfigurableComponents
 * into the stripes framework.
 * @author snwiem
 *
 */
public class RuntimeConfiguration extends net.sourceforge.stripes.config.RuntimeConfiguration {
	
	public static final String SCRIPT_RESOLVER = "ScriptResolver.Class";
	
	private ScriptResolver scriptResolver;
	
	/**
	 * Looks for a class name in config and uses that to create the component.
	 * @return
	 */
	protected ScriptResolver initScriptResolver() {
		return( initializeComponent( ScriptResolver.class, SCRIPT_RESOLVER ) );
	}
	
	
	public ScriptResolver getScriptResolver() {
		return( this.scriptResolver );
	}
	
	@Override
	public void init() {
		super.init();
		try {
			this.scriptResolver = initScriptResolver();
		} catch( Exception e ) {
			throw new StripesRuntimeException( "Problem initialiting configuration objects.", e );
		}
	}
}
