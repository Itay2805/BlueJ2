package me.itay.bluej;

import com.mrcrayfish.device.api.app.Application;

import me.itay.bluej.resourcelocation.BlueJResolvedResource;
import me.itay.bluej.resourcelocation.BlueJResourceManager;
import net.minecraft.nbt.NBTTagCompound;

public class BlueJApp extends Application {
	
	@Override
	public void init() {
		final BlueJResolvedResource res = BlueJResourceManager.resolve("file://c/some/test/file/test.txt");
		res.create(() -> {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("test", "this is a test");
			res.write(compound, () -> {
				res.read((data) -> {
					System.out.println(data);
				});
			});
		});
	}
	
	@Override
	public void load(NBTTagCompound tagCompound) {}

	@Override
	public void save(NBTTagCompound tagCompound) {}
	
}
