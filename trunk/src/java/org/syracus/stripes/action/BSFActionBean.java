package org.syracus.stripes.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.util.Log;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.syracus.stripes.bsf.Script;
import org.syracus.stripes.bsf.ScriptException;
import org.syracus.stripes.bsf.ScriptResolver;
import org.syracus.stripes.config.RuntimeConfiguration;

public abstract class BSFActionBean implements ActionBean {

	private static final Log log = Log.getInstance( BSFActionBean.class );

	@DefaultHandler
	public Resolution exec() {
		String scriptResource = getContext().getRequest().getParameter( "exec" );
		if ( null == scriptResource ) {
			throw new ScriptException( "Missing script argument to exec event." );
		}
		try {
			Script script = loadScript( scriptResource /*getScript()*/ );
			BSFManager manager = new BSFManager();
			prepare( manager );
			manager.exec(
					script.getLanguage(),
					script.getSource().getAbsolutePath(),
					0, 0,
					script.getCode()
			);
			return( getContext().getSourcePageResolution() );
		} catch( BSFException e ) {
			throw new ScriptException( e );
		}
	}
	
	public Resolution eval() {
		String scriptResource = getContext().getRequest().getParameter( "eval" );
		if ( null == scriptResource ) {
			throw new ScriptException( "Missing script argument to eval event." );
		}
		try {
			Script script = loadScript( scriptResource /*getScript()*/ );
			BSFManager manager = new BSFManager();
			prepare( manager );
			Object result = manager.eval(
					script.getLanguage(),
					script.getSource().getAbsolutePath(),
					0, 0,
					script.getCode()
			);
			if ( null != result && (result instanceof Resolution) ) {
				return( (Resolution)result );
			}
			return( getContext().getSourcePageResolution() );
		} catch( BSFException e ) {
			throw new ScriptException( e );
		}
	}
	
	protected Script loadScript( String name ) {
		log.trace( "Trying to load script with name '", name, "'" );
		RuntimeConfiguration configuration = (RuntimeConfiguration)StripesFilter.getConfiguration();
		ScriptResolver scriptResolver = configuration.getScriptResolver();
		Script script = scriptResolver.getScript( name );
		log.trace( "Returning script instance '", script.toString(), "' for name '", name, "'" );
		return( script );
	}
	
	protected void prepare( BSFManager manager )
		throws BSFException
	{
		manager.declareBean( "request", getContext().getRequest(), HttpServletRequest.class );
		manager.declareBean( "response", getContext().getResponse(), HttpServletResponse.class );
		manager.declareBean( "application", getContext().getRequest().getSession().getServletContext(), ServletContext.class );
		manager.declareBean( "context", getContext(), getContext().getClass() );
		manager.declareBean( "action", this, this.getClass() );
	}

}
