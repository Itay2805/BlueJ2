package me.itay.bluej.resourcelocation;

import java.util.HashMap;

import me.itay.bluej.resourcelocation.builtin.file.BlueJFileSystemResolver;

public class BlueJResourceManager {
	
	private static HashMap<String, BlueJResolver> resolvers = new HashMap<>();
	
	static {
		registerResolver("file", new BlueJFileSystemResolver());
	}
	
	public static void registerResolver(String domain, BlueJResolver resolver) {
		if(resolvers.containsKey(domain)) {
			throw new IllegalArgumentException("resolver for that domain already exists");
		}
		resolvers.put(domain, resolver);
	}
	
	public static BlueJResolvedResource resolve(String path) {
		return resolve(new BlueJResourceLocation(path));
	}
	
	public static BlueJResolvedResource resolve(BlueJResourceLocation location) {
		if(resolvers.containsKey(location.getDomain())) {
			if(location.isBase()) {
				return resolvers.get(location.getDomain()).resolve(location, null);
			}
			BlueJResolvedResource base = resolve(location.getParent());
			return resolvers.get(location.getDomain()).resolve(location, base);
		}else {
			return null;
		}
	}
	
}
