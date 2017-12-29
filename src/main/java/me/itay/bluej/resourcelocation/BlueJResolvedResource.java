package me.itay.bluej.resourcelocation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import net.minecraft.nbt.NBTTagCompound;

public interface BlueJResolvedResource {
	
	public interface BlueJResponse<T> {
		public void handle(T result);
	}
	
	public interface BlueJAfter {
		public void handle();
	}
	
	public void setOpenAppID(String appid, BlueJAfter after);
	
	public void mkdirs(BlueJAfter after);
	public void create(BlueJAfter after);
	public void delete(BlueJAfter after);
	public void read(BlueJResponse<NBTTagCompound> response);
	public void write(NBTTagCompound compound, BlueJAfter after);
	
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
