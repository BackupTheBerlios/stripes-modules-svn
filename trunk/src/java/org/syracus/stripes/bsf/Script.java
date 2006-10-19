package org.syracus.stripes.bsf;

import java.io.File;

public class Script {

	private File source;
	private String language;
	private String code;
	private long modified;
	
	
	public long getModified() {
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public File getSource() {
		return source;
	}
	public void setSource(File source) {
		this.source = source;
	}
	
}
