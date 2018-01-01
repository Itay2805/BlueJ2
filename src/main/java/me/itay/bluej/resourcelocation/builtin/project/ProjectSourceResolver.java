package me.itay.bluej.resourcelocation.builtin.project;

import me.itay.bluej.resourcelocation.BlueJResolvedResource;
import me.itay.bluej.resourcelocation.BlueJResolver;
import me.itay.bluej.resourcelocation.BlueJResourceLocation;
import me.itay.bluej.resourcelocation.BlueJResourceManager;

public class ProjectSourceResolver implements BlueJResolver {

	@Override
	public BlueJResolvedResource resolve(BlueJResourceLocation location, BlueJResolvedResource res) {
		String path = res.getFolder().getPath();
		String fullPath = "file://c" + res.getFile().getPath() + "/" + location.getPath();
		return BlueJResourceManager.resolve(fullPath);
	}
	
}
