package me.itay.bluej.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.BlueJApp;
import me.itay.bluej.languages.BlueJLanguage;
import me.itay.bluej.languages.BlueJRuntimeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class Project {

	private Folder projectRoot;
	private List<SourceFile> src = new ArrayList<>();
	private SourceFile startupFile;
	private BlueJLanguage language;

	public static final String MIME_BLUEJ = "bluej";
	public static final String MIME_PROJECT = MIME_BLUEJ + "/project";
	public static final String MIME_SRC_FILE = MIME_BLUEJ + "/source";
	public static final String MIME_PROJ_LANG = "lang";

	public static final String FIELD_CONTENT_TYPE = "content_type";
	public static final String FIELD_STARTUP = "startup";
	public static final String FIELD_FILES_LIST = "files";

	public static final String FILE_BLUEJ_PROJECT = "bjproj";

	public Project(Folder projectRoot, BlueJLanguage lang) {
		this.projectRoot = projectRoot;
        this.language = lang;
		sync();
	}

	public void sync() {
		src.clear();
		Folder srcFolder = projectRoot.getFolder("src");
		List<File> files = Objects.requireNonNull(srcFolder).search((f) -> {
			String contentType = Objects.requireNonNull(f.getData()).getString(FIELD_CONTENT_TYPE);
			@SuppressWarnings("UnnecessaryLocalVariable") boolean test = MIME_SRC_FILE.equalsIgnoreCase(contentType);
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
		NBTTagCompound compound = Objects.requireNonNull(projectFile).getData();

		if(startupFile == null) {
			Objects.requireNonNull(compound).removeTag(FIELD_STARTUP);
		}else {
			Objects.requireNonNull(compound).setString(FIELD_STARTUP, startupFile.getName());
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

	public SourceFile createSourceFile(String name, Runnable runnable) {
		File f = new File(name, BlueJApp.id, new NBTTagCompound());
		Objects.requireNonNull(projectRoot.getFolder("src")).add(f, (resp, ok) -> {
			if(ok) {
				SourceFile srcF = new SourceFile(f);
				src.add(srcF);
				srcF.prepare(runnable);
			}else {
				System.err.println("[ERROR] could not create source file: " + Objects.requireNonNull(resp).getMessage());
			}
		});
		return new SourceFile(f);
	}

	public void deleteSourceFile(String name, Runnable runnable) {
		SourceFile srcF = getSourceFile(name);
		src.remove(srcF);
		srcF.getFile().delete((resp, ok) -> {
			if(ok) {
				runnable.run();
			}else {
				// @Todo proper error handling
				System.err.println("[ERROR] Could not remove source file: " + Objects.requireNonNull(resp).getMessage());
			}
		});
	}

	public static void createProject(Folder projectRoot, BlueJLanguage lang, Runnable runnable) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString(FIELD_CONTENT_TYPE, MIME_PROJECT);
		compound.setString(MIME_PROJ_LANG, lang.getName());

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
					System.err.println("[ERROR] could not create folder: " + Objects.requireNonNull(resp).getMessage());
				}
			});
		} else {
			runnable.run();
		}
	}

	public static boolean loadProject(Folder projectFolder, Consumer<Project> consumer) {
	    System.out.println("projectFolder: " + projectFolder.getName());
	    System.out.println("Files in project folder: ");
        projectFolder.getFiles().forEach((f)-> System.out.println("\t" + f.getName()));
		if (!projectFolder.hasFile(FILE_BLUEJ_PROJECT)) { //This is returning true regardless on if .bjproj exists
			if (Objects.requireNonNull(projectFolder.getParent()).hasFile(FILE_BLUEJ_PROJECT)) {
				projectFolder = projectFolder.getParent();
			} else {
				// @Todo return proper errors
				return false;
			}
		}

		File projectFile = projectFolder.getFile(FILE_BLUEJ_PROJECT);

		NBTTagCompound compound = Objects.requireNonNull(projectFile).getData();
		if (!MIME_PROJECT.equalsIgnoreCase(Objects.requireNonNull(compound).getString(FIELD_CONTENT_TYPE))) {
			// @Todo return proper errors
			System.err.println("[ERROR] invalid content type");
			consumer.accept(null);
		}
		System.out.println(compound.toString());
		final String langname = compound.hasKey(MIME_PROJ_LANG) ? compound.getString(MIME_PROJ_LANG) : "";
        final File startupFile = compound.hasKey(FIELD_STARTUP, NBT.TAG_STRING) ? File.fromTag(FIELD_STARTUP, compound) : null;
        Folder projectRoot = projectFile.getParent();
        if(langname.isEmpty()){
            return false;
        }
        Project proj = new Project(projectRoot, BlueJRuntimeManager.getLanguage(langname));
        if(startupFile != null){
            System.out.println(startupFile.getData().toString());
            proj.setStartupFile(new SourceFile(startupFile));
        }
        consumer.accept(proj);
        return true;
	}

    public BlueJLanguage getProjectLanguage() {
        return this.language;
    }
}