package me.itay.bluej.resourcelocation;

public interface BlueJResolver {
	
	BlueJResolvedResource resolve(BlueJResourceLocation location, BlueJResolvedResource res);
	
}
