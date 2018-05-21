package me.itay.bluej.resourcelocation.builtin.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import me.itay.bluej.resourcelocation.BlueJResolvedResource;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class BlueJResolvedFile implements BlueJResolvedResource {
	
	private static class FakeApp extends Application {
		public void save(NBTTagCompound tagCompound) {}
		public void load(NBTTagCompound tagCompound) {}
		public void init(@Nullable NBTTagCompound intent) {}
	}
	
	private static final FakeApp INSTANCE = new FakeApp();
	
	private Folder parent;
	private File file;
	private Folder folder;
	private Path p;
	
	public BlueJResolvedFile(String path) {
		if(Laptop.getMainDrive() == null) {
			FileBrowser browser = new FileBrowser(0, 0, INSTANCE, FileBrowser.Mode.BASIC);
			browser.init(new Layout());
			browser.handleLoad();
		}
		
		/// @Cleanup @Hack this is a temp hack to make sure it will load
		/// this will need to be removed or something :\
		while(Laptop.getMainDrive() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		p = Paths.get(path);
		
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		boolean exists = true;
		for(int i = 0; i < p.getNameCount() - 1; i++) {
			String name = p.getName(i).toString();
			if(folder.hasFolder(name)) {
				folder = folder.getFolder(name);
			}else {
				exists = false;
				break;
			}
		}
		if(exists) {
			parent = folder;
			if(folder.hasFolder(path)) {
				folder = parent.getFolder(p.getName(p.getNameCount() - 1).toString());
			}else if(folder.hasFile(path)) {
				file = parent.getFile(p.getName(p.getNameCount() - 1).toString());
			}
		}
	}
	
	@Override
	public void mkdirs(Runnable runnable) {
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		mkdirsRecursive(folder, 0, p.getNameCount(), (base) -> {
			this.folder = base;
			this.parent = folder.getParent();
			runnable.run();
		});
	}
	
	private void mkdirsRecursive(Folder base, int i, int max, Consumer<Folder> consumer) {
		if(i >= max) {
			consumer.accept(base);
			return;
		}
		
		String name = p.getName(i).toString();
		if(base.hasFolder(name)) {
			mkdirsRecursive(base, i + 1, max, consumer);
		}else {
			Folder temp = new Folder(name);
			base.add(temp, (resp, ok) -> {
				Folder valid = base.getFolder(name);
				mkdirsRecursive(valid, i + 1, max, consumer);
			});
		}
	}
	
	@Override
	public void create(Runnable runnable) {
		Drive drive = Laptop.getMainDrive();
		Folder folder = drive.getRoot();
		mkdirsRecursive(folder, 0, p.getNameCount() - 1, (base) -> {
			parent = base;
			String name = p.getName(p.getNameCount() - 1).toString();
			if(!parent.hasFile(name)) {
				File temp = new File(name, "", new NBTTagCompound());
				parent.add(temp, (resp, ok) -> {
					if(!ok) {
						// promise.reject(new Exception(resp.getMessage()));
						// @Todo handle these kind of errors
						System.err.println("[ERROR] could not create file: " + resp.getMessage());
					}else {
						this.file = parent.getFile(name);
						runnable.run();
					}
				});
			}
		});
	}

	@Override
	public void delete(Runnable runnable) {
		if(isFile()) {
			file.delete((resp, ok) -> {
				if(ok) {
					runnable.run();
				}else {
					// promise.reject(new Exception(resp.getMessage()));
					// @Todo handle these kind of errors
					System.err.println("[ERROR] could not delete file: " + resp.getMessage());
				}
			});
		}else {
			folder.delete((resp, ok) -> {
				if(ok) {
					runnable.run();
				}else {
					// promise.reject(new Exception(resp.getMessage()));
					// @Todo handle these kind of errors
					System.err.println("[ERROR] could not delete file: " + resp.getMessage());
				}
			});
		}
	}

	@Override
	public void read(Consumer<NBTTagCompound> consumer) {
		consumer.accept(file.getData());
	}

	@Override
	public void write(NBTTagCompound compound, Runnable runnable) {
		file.setData(compound, (resp, ok) -> {
			runnable.run();
		});
	}

	@Override
	public boolean isFile() {
		return file != null;
	}

	@Override
	public boolean isFolder() {
		return folder != null;
	}

	@Override
	public Folder getParent() {
		return parent;
	}
	
	@Override
	public Folder getFolder() {
		return folder;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean exists() {
		return !isFile() && !isFolder();
	}
	
	@Override
	public void setOpenAppID(String appid, Runnable runnable) {
		read((data) -> {
			delete(() -> {
				File temp = new File(p.getName(p.getNameCount() - 1).toString(), appid, data);
				parent.add(temp, (resp, ok) -> {
					file = temp;
					runnable.run();
				});
			});			
		});
	}
	
	
}
