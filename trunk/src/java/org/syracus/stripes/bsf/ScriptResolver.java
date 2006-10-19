package org.syracus.stripes.bsf;

import net.sourceforge.stripes.config.ConfigurableComponent;

public interface ScriptResolver extends ConfigurableComponent {

	public Script getScript( String resource );
	
}
