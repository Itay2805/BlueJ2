package me.itay.bluej.resourcelocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlueJResourceLocation {
	
	private BlueJResourceLocation parent;
	private String domain;
	private String context;
	private String path;
	
	private static final String RESOURCE_LOCATION_REGEX = "^([a-zA-Z0-9_\\-\\.]+):(?:\\/\\/|\\\\\\\\)((\\[[\\[\\]a-zA-Z0-9_\\-\\\\\\/\\.:]+\\])|([a-zA-Z0-9_\\-\\.]+))(((?:\\/|\\\\)[a-zA-Z0-9_\\-\\\\\\/\\.]+).|(?:\\/|\\\\))";
	private static final Pattern PATTERN = Pattern.compile(RESOURCE_LOCATION_REGEX);
	
	public BlueJResourceLocation(String domain, String context, String path) {
		this.domain = domain;
		this.context = context;
		this.path = path;
		try {
			parent = new BlueJResourceLocation(context);
		}catch(Exception e){
			// Ignore
		}
	}
	
	public BlueJResourceLocation(String domain, BlueJResourceLocation parent, String path) {
		this.domain = domain;
		this.parent = parent;
		this.context = parent.toString();
		this.path = path;
	}
	
	public BlueJResourceLocation(String resource) {
		Matcher matcher = PATTERN.matcher(resource);
		if(matcher.matches()) {
			domain = matcher.group(1);
			context = matcher.group(2);
			if(context.startsWith("[")) {
				context = context.substring(1, context.length() - 1);
				try {
					parent = new BlueJResourceLocation(context);
				}catch(Exception e) {
					throw new IllegalArgumentException("Invalid context resource location", e);
				}
			}
			path = matcher.group(5);
		}else {
			throw new IllegalArgumentException("Not a valid resource location");
		}
	}
	
	public BlueJResourceLocation getParent() {
		return parent;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getContext() {
		return context;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isBase() {
		return parent == null;
	}
	
	@Override
	public String toString() {
		if(parent == null) {
			return domain + "://" + context + "/" + path;
		}else {
			return domain + "://[" + context + "]/" + path;
		}
	}
	
}
