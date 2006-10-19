package org.syracus.stripes.resolution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.localization.DefaultLocalizationBundleFactory;
import net.sourceforge.stripes.util.Log;

/**
 * Concrete implementation of a ResolutionResolver.
 * Mappings are done using standard java resource bundle mechanism.
 * The resource bundles to use can be configured using the configuration
 * key 'ResourceBundleResolutionResolver.ResourceBundles' which may
 * contain a comma separated list of ResourceBundle names. By default
 * the 'StripesResources' from DefaultLocalozationBundleFactory is used.
 * 
 * @author snwiem
 *
 */
public class ResourceBundleResolutionResolver implements ResolutionResolver {

	private static final Log log = Log.getInstance( ResourceBundleResolutionResolver.class );
	
	private static final String RESOURCE_BUNDLES = "ResourceBundleResolutionResolver.ResourceBundles";
	private static final String DEFAULT_RESOURCE_BUNDLES = DefaultLocalizationBundleFactory.BUNDLE_NAME;
	
	private Configuration configuration;
	private Map<String,ResourceBundle> bundleCache = new HashMap<String,ResourceBundle>();
	
	public void init(Configuration configuration) throws Exception {
		this.configuration = configuration;
	}
	
	public Configuration getConfiguration() {
		return( this.configuration );
	}

	public String resolvePath(String key, Locale locale ) {
		log.trace( "[getPath] key = '", key, "', locale = '", locale, "'" );
		List<String> resources = getResourceBundleResources( getConfiguration() );
		if ( null != resources ) {
			String resource = null;
			try {
				for ( Iterator<String> i = resources.iterator(); i.hasNext(); ) {
					resource = i.next();
					log.trace( "[getPath] lookup up resource '", resource, "' for local '", locale, "'" );
					ResourceBundle bundle = getResourceBundle( resource, locale );
					if ( null != bundle ) {
						try {
							String value = bundle.getString( key );
							if ( null != value ) {
								return( value );
							}
						} catch( MissingResourceException mre ) {
							//log.info( "No value found for key '", key, "' in resource bundle '", resource, "' for locale '", locale, "'" );
						}
					}
				}
				
				log.debug( "No mapping found for key '", key, "' in all configured resource bundles. returning key." );
				return( null );
			} catch( MissingResourceException mre ) {
				log.warn( "Could not find resource bundle '", resource, "' configured for ResourceBundleResolutionResolver." );
			}
		}
		return( null );
	}
	
	public String resolvePath(String key) {
		return( resolvePath( key, null ) );
	}
	
	protected ResourceBundle getResourceBundle( String resource, Locale locale ) {
		ResourceBundle bundle = null;
		String bundleKey = (null == locale) ? resource : (resource + locale.toString()); 
		bundle = bundleCache.get( bundleKey );
		if ( null == bundle ) {
			log.trace( "No cached resource bundle found for resource '", resource, "' and locale '", locale.toString(), "'" );
			try {
				bundle = (null == locale) ? ResourceBundle.getBundle( resource ) : ResourceBundle.getBundle( resource, locale );
				if ( null != bundle ) {
					log.trace( "Caching resource bundle using key '", bundleKey, "'" );
					this.bundleCache.put( bundleKey, bundle );
				}
			} catch( MissingResourceException e ) {
				log.warn( "Could not find resource bundle '", resource, "' configured for ResourceBundleResolutionResolver." );
			}
		} else {
			log.trace( "Returning cached resource bundle for resource '", resource, "' and locale '", locale.toString(), "'" );
		}
		return( bundle );
	}
	
	protected List<String> getResourceBundleResources( String resources ) {
		if ( null != resources ) {
			return( Arrays.asList( resources.split( "," ) ) );
		}
		return( null );
	}
	protected List<String> getResourceBundleResources( Configuration configuration ) {
		BootstrapPropertyResolver bootstrap = configuration.getBootstrapPropertyResolver();
		String resources = bootstrap.getProperty( RESOURCE_BUNDLES );
		if ( null == resources ) {
			resources = DEFAULT_RESOURCE_BUNDLES;
		}
		return( getResourceBundleResources( resources ) );
	}

}
