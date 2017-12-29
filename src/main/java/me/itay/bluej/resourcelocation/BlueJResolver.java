package me.itay.bluej.resourcelocation;

public interface BlueJResolver {
	
	boolean canResolve(BlueJResourceLocation location);
	BlueJResolvedResource resolve(BlueJResourceLocation location);
	
}
