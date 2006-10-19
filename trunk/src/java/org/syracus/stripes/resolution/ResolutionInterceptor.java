package org.syracus.stripes.resolution;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

import net.sourceforge.stripes.action.OnwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.Log;

@Intercepts(LifecycleStage.ResolutionExecution)
public class ResolutionInterceptor implements Interceptor {

	private static final Log log = Log.getInstance( ResolutionInterceptor.class );
	
	private static final String RESOLUTION_RESOLVER = "ResolutionResolver.Class";
	private static final Class DEFAULT_RESOLUTION_RESOLVER = ResourceBundleResolutionResolver.class;
	
	public Resolution intercept(ExecutionContext context) throws Exception {
		Resolution resolution = context.getResolution();
		
		Class resolutionClass = resolution.getClass();
		Type type = resolutionClass.getGenericSuperclass();
		if ( type instanceof ParameterizedType ) {
			ParameterizedType pType = (ParameterizedType)type;
		
			Type rawType = pType.getRawType();
			
			if ( rawType.equals( OnwardResolution.class ) ) {
				String path = ((OnwardResolution)resolution).getPath();
								
				String newPath = resolvePath( path, context );
				
				log.debug( "[intercept] oldPath '", path, "' mapped to '", newPath, "'" );
				
				if ( null != newPath ) {
					((OnwardResolution)resolution).setPath( newPath );
				}
			}
		}

		return( context.proceed() );
	}
	
	protected String resolvePath( String path, ExecutionContext context ) {
		ResolutionResolver resolver = getResolutionResolver();
		Locale locale = context.getActionBeanContext().getRequest().getLocale();
		
		String action = context.getActionBean().getClass().getName();
		String binding = StripesFilter.getConfiguration().getActionResolver().getUrlBinding( context.getActionBean().getClass() );
		String event = context.getHandler().getName();
		
		log.trace( "Action = '", action, "'" );
		log.trace( "Binding = '", binding, "'" );
		log.trace( "Event = '", event, "'" );
		
		String mapping = null;
		
		// phase 1: action.event.path
		if ( null != ( mapping = resolver.resolvePath( action + "." + event + "." + path, locale ) ) ) {
			return( mapping );
		}
		// phase 2: action.path
		if ( null != ( mapping = resolver.resolvePath( action + "." + "." + path, locale ) ) ) {
			return( mapping );
		}
		// phase 3: binding.event.path
		if ( null != ( mapping = resolver.resolvePath( binding + "." + event + "." + path, locale ) ) ) {
			return( mapping );
		}
		// phase 4: binding.path
		if ( null != ( mapping = resolver.resolvePath( binding + "." + path, locale ) ) ) {
			return( mapping );
		}
		// phase 5: event.path
		if ( null != ( mapping = resolver.resolvePath( event + "." + path, locale ) ) ) {
			return( mapping );
		}
		// phase 5: path
		if ( null != ( mapping = resolver.resolvePath( path, locale ) ) ) {
			return( mapping );
		}
		return( null );
	}
	
	protected ResolutionResolver getResolutionResolver() {
		ResolutionResolver resolverInstance = null;
		Configuration configuration = StripesFilter.getConfiguration(); 
		String resolverName = configuration.getBootstrapPropertyResolver().getProperty( RESOLUTION_RESOLVER );
		Class resolverClass = null;
		
		try {
			if ( null == resolverName ) {
				resolverClass = DEFAULT_RESOLUTION_RESOLVER;
			} else {
				resolverClass = Class.forName( resolverName );
			}
			if ( ResolutionResolver.class.isAssignableFrom( resolverClass ) ) {
				resolverInstance = (ResolutionResolver)resolverClass.newInstance(); 
			} else {
				throw new StripesRuntimeException( "Invalid implementation for ResolutionResolver." );
			}
		} catch( ClassNotFoundException e ) {
			throw new StripesRuntimeException( e );
		} catch( IllegalAccessException e ) {
			throw new StripesRuntimeException( e );
		} catch( InstantiationException e ) {
			throw new StripesRuntimeException( e );
		}
		
		try {
			resolverInstance.init( configuration );
		} catch( Exception e ) {
			throw new StripesRuntimeException( e );
		}
		
		return( resolverInstance );
	}

}
