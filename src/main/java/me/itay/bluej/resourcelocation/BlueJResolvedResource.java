package me.itay.bluej.resourcelocation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import net.minecraft.nbt.NBTTagCompound;

public interface BlueJResolvedResource {
	
	public void setOpenAppID(String appid, Runnable runnable);

	public void mkdirs(Runnable runnable);
	public void create(Runnable runnable);
	public void delete(Runnable runnable);
	public void read(Consumer<NBTTagCompound> consumer);
	public void write(NBTTagCompound compound, Runnable runnable);

	public default void mkdirsSync() {
		final AtomicBoolean bool = new AtomicBoolean(false);
		mkdirs(() -> {
			bool.set(true);
		});
		while(!bool.get()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public default void createSync() {
		final AtomicBoolean bool = new AtomicBoolean(false);
		create(() -> {
			bool.set(true);
		});
		while(!bool.get()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public default void deleteSync() {
		final AtomicBoolean bool = new AtomicBoolean(false);
		delete(() -> {
			bool.set(true);
		});
		while(!bool.get()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public default NBTTagCompound readSync() {
		final AtomicReference<NBTTagCompound> ref = new AtomicReference<NBTTagCompound>(null);
		read((data) -> {
			ref.set(data);
		});
		while(ref.get() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ref.get();
	}

	public default void writeSync(NBTTagCompound data) {
		final AtomicBoolean bool = new AtomicBoolean(false);
		write(data, () -> {
			bool.set(true);
		});
		while(!bool.get()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isFile();
	public boolean isFolder();
	public Folder getParent();
	public Folder getFolder();
	public File getFile();
	public boolean exists();
	
}
