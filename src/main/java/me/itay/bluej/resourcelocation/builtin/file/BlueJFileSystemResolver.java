package me.itay.bluej.resourcelocation.builtin.file;

import me.itay.bluej.resourcelocation.BlueJResolvedResource;
import me.itay.bluej.resourcelocation.BlueJResolver;
import me.itay.bluej.resourcelocation.BlueJResourceLocation;

public class BlueJFileSystemResolver implements BlueJResolver {

	@Override
	public BlueJResolvedResource resolve(BlueJResourceLocation location, BlueJResolvedResource parent) {
		return new BlueJResolvedFile(location.getPath());
	}
	
	
	
}
