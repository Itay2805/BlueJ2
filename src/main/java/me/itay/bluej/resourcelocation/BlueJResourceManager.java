package me.itay.bluej.resourcelocation;

import java.util.ArrayList;
import java.util.List;

import me.itay.bluej.resourcelocation.builtin.file.BlueJFileSystemResolver;

public class BlueJResourceManager {
	
	private static List<BlueJResolver> resolvers = new ArrayList<>();
	
	static {
		resolvers.add(new BlueJFileSystemResolver());
	}
	
	public static BlueJResolvedResource resolve(String path) {
		return resolve(new BlueJResourceLocation(path));
	}
	
	public static BlueJResolvedResource resolve(BlueJResourceLocation location) {
		for(BlueJResolver resolver : resolvers) {
			if(resolver.canResolve(location)) {
				return resolver.resolve(location);
			}
		}
		return null; 
	}
	
}
