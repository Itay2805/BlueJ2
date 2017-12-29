package me.itay.bluej.resourcelocation.builtin.file;

import me.itay.bluej.resourcelocation.BlueJResolvedResource;
import me.itay.bluej.resourcelocation.BlueJResolver;
import me.itay.bluej.resourcelocation.BlueJResourceLocation;

public class BlueJFileSystemResolver implements BlueJResolver {

	@Override
	public boolean canResolve(BlueJResourceLocation location) {
		return location.getDomain().equalsIgnoreCase("file") && location.isBase();
	}

	@Override
	public BlueJResolvedResource resolve(BlueJResourceLocation location) {
		return new BlueJResolvedFile(location.getPath());
	}
	
	
	
}
