package org.syracus.stripes.bsf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.util.Log;

public class CachingResourceScriptResolver extends ResourceScriptResolver {

	private static final String CACHING_ENABLED = "CachingResourceScriptResolver.CachingEnabled";
	private static final String RELOAD_ENABLED = "CachingResourceScriptResolver.ReloadEnabled";
	
	private static final Log log = Log.getInstance( CachingResourceScriptResolver.class );
	
	private Map<String,Script> cache = new HashMap<String,Script>();
	private boolean cachingEnabled = true;
	private boolean reloadEnabled = true;
	
	
	@Override
	public void init(Configuration configuration) throws Exception {
		super.init(configuration);
		
		BootstrapPropertyResolver bootstrap = configuration.getBootstrapPropertyResolver();
		String property = bootstrap.getProperty( CACHING_ENABLED );
		if ( null != property && false == property.equalsIgnoreCase( "true" ) ) {
			this.cachingEnabled = false;
		}
		property = bootstrap.getProperty( RELOAD_ENABLED );
		if ( null != property && false == property.equalsIgnoreCase( "true" ) ) {
			this.reloadEnabled = false;
		}
	}


	@Override
	public Script getScript(String resource) {
		Script script = null;
		if ( true == this.cachingEnabled ) {
			if ( null == ( script = this.cache.get( resource ) ) ) {
				script = super.getScript( resource );
				if ( null != script ) {
					log.debug( "Caching script data for resource '", resource, "'." );
					this.cache.put( resource, script );
				}
			} else if ( true == this.reloadEnabled ) {
				long lm = script.getSource().lastModified();
				if ( lm > script.getModified() ) {
					log.debug( "Script data for resource '", resource, "' outdated. Reloading script code." );
					try {
						script.setCode( super.readFile( script.getSource() ) );
						script.setModified( lm );
					} catch( IOException e ) {
						throw new ScriptException( "Failed to reload script data for resource '" + resource + "'.", e );
					}
				}
			}
		} else {
			script = super.getScript( resource );
		}
		return( script );
	}
	

	
}
