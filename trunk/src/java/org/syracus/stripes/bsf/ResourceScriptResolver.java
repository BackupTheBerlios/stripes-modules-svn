package org.syracus.stripes.bsf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.util.Log;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public class ResourceScriptResolver implements ScriptResolver {

	private static final String RESOURCE_PREFIX = "ResourceScriptResolver.ResourcePrefix";
	
	private static final int DEFAULT_BUFFERSIZE = 1024;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final Log log = Log.getInstance( ResourceScriptResolver.class );
	
	private Configuration configuration;
	private List<String> resources;
	
	public void init(Configuration configuration) throws Exception {
		this.configuration = configuration;
		
		BootstrapPropertyResolver bootstrap = configuration.getBootstrapPropertyResolver();
		String property = bootstrap.getProperty( RESOURCE_PREFIX );
		if ( null != property ) {
			this.resources = Arrays.asList( property.split( "," ) );
		}
	}
	
	public Configuration getConfiguration() {
		return( this.configuration );
	}
	
	public Script getScript(String resource) {
		log.trace( "Trying to load script for resource '", resource, "'" );
		Script script = null;
		try {
			File file = resolveResource( resource );
			if ( null != file ) {
				script = loadScript( file );
				return( script );
			} else {
				throw new ScriptException( "Failed to load script data for resource '" + resource + "'." );
			}
		} catch( IOException e ) {
			throw new ScriptException( "Failed to load script data for resource '" + resource + "'.", e );
		} catch( BSFException e ) {
			throw new ScriptException( e );
		}
	}
	
	protected File resolveResource( String resource ) {
		log.trace( "[resolveResource] resource = '", resource, "'" );
		ServletContext context = getConfiguration().getServletContext();
		if ( null != this.resources ) {
			if ( !resource.startsWith( "/" ) ) {
				resource = "/" + resource;
			}
			for ( String r : this.resources ) {
				log.trace( "[resolveResource] examining path '", r, "'" );
				String path = context.getRealPath( r + resource );
				log.trace( "[resolveResource] realPath = '", path, "'" );
				File file = new File( path );
				if ( null != file && file.exists() && file.isFile() && file.canRead() ) {
					return( file );
				}
			}
		} else {
			String path = context.getRealPath( resource );
			File file = new File( path );
			if ( null != file && file.exists() && file.isFile() && file.canRead() ) {
				return( file );
			}
		}
		return( null );
	}
	
	protected Script loadScript( File file )
		throws IOException, BSFException
	{
		Script script = new Script();
		script.setCode( readFile( file ) );
		script.setSource( file );
		script.setLanguage( BSFManager.getLangFromFilename( file.getName() ) );
		script.setModified( file.lastModified() );
		return( script );
	}

	protected String readFile( File file )
		throws IOException
	{
		FileInputStream istream = new FileInputStream( file );
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		streamCopy( istream, ostream );
		return( ostream.toString( DEFAULT_ENCODING ) );
	}
	
	protected void streamCopy( InputStream istream, OutputStream ostream )
		throws IOException
	{
		streamCopy( istream, ostream, DEFAULT_BUFFERSIZE );
	}
	
	protected void streamCopy( InputStream istream, OutputStream ostream, int bufferSize )
		throws IOException
	{
		byte[] buffer = new byte[ bufferSize ];
		int rb = 0;
		while ( -1 != ( rb = istream.read( buffer, 0, bufferSize ) ) ) {
			ostream.write( buffer, 0, rb );
		}
	}
	

}
