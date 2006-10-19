package org.syracus.stripes.resolution;

import java.util.Locale;

import net.sourceforge.stripes.config.ConfigurableComponent;

/**
 * The ResolutionResolver is used to map key names to concrete
 * URLs used by different Resolution implementations.
 * This class is very basic at the moment. It should be
 * extended to return complete Resolution instances sometimes.
 * @author snwiem
 *
 */
public interface ResolutionResolver extends ConfigurableComponent {

	public String resolvePath( String key );
	public String resolvePath( String key, Locale locale );
	
}
