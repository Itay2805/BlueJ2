package me.itay.bluej.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.BlueJApp;
import me.itay.bluej.languages.BlueJLanguage;
import me.itay.bluej.languages.BlueJRuntimeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class Project {

	private Folder projectRoot;
	private List<SourceFile> src = new ArrayList<>();
	private SourceFile startupFile;
	private BlueJLanguage language;
	private String name;
	private File projectFile;

	public static final String FIELD_ROOT = "root";
	public static final String FIELD_NAME = "name";
    public static final String FIELD_LANG = "lang";
	public static final String FIELD_STARTUP = "startup";
	public static final String FIELD_FILES_LIST = "files";
	public static final String FIELD_FILE = "field_";

	public static final String FILE_BLUEJ_PROJECT = "bjproj";

	public Project(Folder projectRoot, String name, BlueJLanguage lang) {
		this.projectRoot = projectRoot;
        this.language = lang;
        this.name = name;
        this.projectFile = this.projectRoot.getFile(FILE_BLUEJ_PROJECT);
	}

	public void save() {
	    NBTTagCompound projectdata = new NBTTagCompound();
	    projectdata.setString(FIELD_ROOT, this.projectRoot.getName());
	    projectdata.setString(FIELD_NAME, this.name);
	    projectdata.setString(FIELD_LANG, this.language.getName());
	    if(this.startupFile != null) {
            NBTTagCompound startupnbt = this.startupFile.getData();
            startupnbt.setString("name", this.startupFile.getName());
            projectdata.setTag(FIELD_STARTUP, startupnbt);
        }
        if(!src.isEmpty()) {
            NBTTagList files = new NBTTagList();
            for (int i = 0; i < src.size(); i++) {
                SourceFile f = src.get(i);
                if(this.projectRoot.getFolder("src").hasFile(f.getName())){
                    NBTTagCompound fd = new NBTTagCompound();
                    fd.setString("name", f.getName());
                    fd.setTag(FIELD_FILE + i, f.getData());
                    files.appendTag(fd);
                    continue;
                }
                src.remove(f);
            }
            projectdata.setTag(FIELD_FILES_LIST, files);
        }
        this.projectFile.setData(projectdata);
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

	public void addSourceFile(SourceFile sourcefile){
        src.add(sourcefile);
        save();
    }

    /**
     * Use this for creating and adding a source file.
     * If you already have a source file, use  {@link Project#addSourceFile(SourceFile)} instead.
     * @param name name of the file
     * @param runnable a runnable lambda function to be executed during the creation of the source file
     * @return the source file
     */
	public SourceFile createSourceFile(String name, Runnable runnable) {
		File f = new File(name, BlueJApp.id, new NBTTagCompound());
		Objects.requireNonNull(projectRoot.getFolder("src")).add(f, (resp, ok) -> {
			if(ok) {
				addSourceFile(new SourceFile(f));
				if(runnable != null)
				    runnable.run();
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
            if (ok) {
                runnable.run();
            } else {
                // @Todo proper error handling
                System.err.println("[ERROR] Could not remove source file: " + Objects.requireNonNull(resp).getMessage());
            }
        });
    }

	public boolean load(Consumer<Project> consumer) {
	    Folder projectFolder = this.projectRoot;
	    System.out.println("projectFolder: " + projectFolder.getName());
	    System.out.println("Files in project folder: ");
        projectFolder.getFiles().forEach((f)-> System.out.println("\t" + f.getName()));
		if (!projectFolder.hasFile(FILE_BLUEJ_PROJECT)) {
			if (Objects.requireNonNull(projectFolder.getParent()).hasFile(FILE_BLUEJ_PROJECT)) {
				projectFolder = projectFolder.getParent();
			} else {
				// @Todo return proper errors
				return false;
			}
		}

		File projectFile = projectFolder.getFile(FILE_BLUEJ_PROJECT);
		Project project = ProjectUtilsKt.loadFromProjectFile(projectFile);
		if(project != null){
		    consumer.accept(project);
		    return true;
        }
        return false;
    }

    public BlueJLanguage getProjectLanguage() {
        return this.language;
    }
}