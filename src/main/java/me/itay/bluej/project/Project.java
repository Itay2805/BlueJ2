package me.itay.bluej.project;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.BlueJApp;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class Project {

	private Folder projectRoot;
	private List<SourceFile> src = new ArrayList<>();
	private SourceFile startupFile;
	
	public static final String MIME_BLUEJ = "bluej";
	public static final String MIME_PROJECT = MIME_BLUEJ + "/project";
	public static final String MIME_SRC_FILE = MIME_BLUEJ + "/source";
	
	public static final String FIELD_CONTENT_TYPE = "content_type";
	public static final String FIELD_STARTUP = "startup";

	public static final String FILE_BLUEJ_PROJECT = ".bjproj";
	
	public Project(Folder projectRoot) {
		this.projectRoot = projectRoot;
		
		sync();
	}

	public void sync() {
		src.clear();
		Folder srcFolder = projectRoot.getFolder("src");
		List<File> files = srcFolder.search((f) -> {
			String contentType = f.getData().getString(FIELD_CONTENT_TYPE);
			boolean test = MIME_SRC_FILE.equalsIgnoreCase(contentType);
//			System.out.println("[DEBUG] " + MIME_SRC_FILE + " == " + contentType + " > " + test);
			return test;
		});
		for (File srcf : files) {
			src.add(new SourceFile(srcf));
		}
	}
	
	public void setStartupFile(SourceFile startupFile) {
		this.startupFile = startupFile;

		File projectFile = projectRoot.getFile(FILE_BLUEJ_PROJECT);
		NBTTagCompound compound = projectFile.getData();
		
		if(startupFile == null) {
			compound.removeTag(FIELD_STARTUP);
		}else {
			compound.setString(FIELD_STARTUP, startupFile.getName());
		}

		projectFile.setData(compound);
	}
	
	public SourceFile getStartupFile() {
		return startupFile;
	}

	public Folder getProjectRoot() {
		return projectRoot;
	}

	public List<SourceFile> getSrc() {
		return src;
	}
	
	public SourceFile getSourceFile(String name) {
		for(SourceFile src : getSrc()) {
			if(src.getName().equals(name)) {
				return src;
			}
		}
		return null;
	}
	
	public void createSourceFile(String name, Runnable runnable) {
		File f = new File(name, BlueJApp.id, new NBTTagCompound());
		projectRoot.getFolder("src").add(f, (resp, ok) -> {
			if(ok) {
				SourceFile srcF = new SourceFile(f);
				src.add(srcF);
				srcF.prepare(runnable);				
			}else {				
				System.err.println("[ERROR] could not create source file: " + resp.getMessage());
			}
		});
	}
	
	public void deleteSourceFile(String name, Runnable runnable) {
		SourceFile srcF = getSourceFile(name);
		src.remove(srcF);
		srcF.getFile().delete((resp, ok) -> {
			if(ok) {
				runnable.run();
			}else {
				// @Todo proper error handling
				System.err.println("[ERROR] Could not remove source file: " + resp.getMessage());
			}
		});
	}
	
	public static void createProject(Folder projectRoot, Runnable runnable) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString(FIELD_CONTENT_TYPE, MIME_PROJECT);

		createFolder(projectRoot, "src", () -> {
			createFolder(projectRoot, "res", () -> {
				createFolder(projectRoot, "build", () -> {
					projectRoot.add(new File(FILE_BLUEJ_PROJECT, BlueJApp.id, compound), (r3, o3) -> {
						runnable.run();
					});
				});
			});
		});
	}

	private static void createFolder(Folder parent, String name, Runnable runnable) {
		if (!parent.hasFolder(name)) {
			parent.add(new Folder(name), (resp, ok) -> {
				if(ok) {
					runnable.run();
				}else {
					// @Todo do proper error handling
					System.err.println("[ERROR] could not create folder: " + resp.getMessage());
				}
			});
		} else {
			runnable.run();
		}
	}

	public static void loadProject(Folder projectFolder, Consumer<Project> consumer) {
		if (!projectFolder.hasFile(FILE_BLUEJ_PROJECT)) {
			if (projectFolder.getParent().hasFile(FILE_BLUEJ_PROJECT)) {
				projectFolder = projectFolder.getParent();
			} else {
				// @Todo return proper errors
				System.err.println("[ERROR] no file .bjproj");
				consumer.accept(null);
			}
		}

		File projectFile = projectFolder.getFile(FILE_BLUEJ_PROJECT);

		NBTTagCompound compound = projectFile.getData();
		if (!MIME_PROJECT.equalsIgnoreCase(compound.getString(FIELD_CONTENT_TYPE))) {
			// @Todo return proper errors
			System.err.println("[ERROR] invalid content type");
			consumer.accept(null);
		}
		final String startupFile = compound.hasKey(FIELD_STARTUP, NBT.TAG_STRING) ? compound.getString(FIELD_STARTUP) : null;
		Folder projectRoot = projectFile.getParent();
		createFolder(projectRoot, "src", () -> {
			createFolder(projectRoot, "res", () -> {
				createFolder(projectRoot, "build", () -> {
					Project project = new Project(projectRoot);
					if(startupFile != null) {
						SourceFile srcF = project.getSourceFile(startupFile);
						project.setStartupFile(srcF);
					}
					consumer.accept(project);
				});
			});
		});

	}

}
