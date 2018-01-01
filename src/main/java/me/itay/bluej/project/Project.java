package me.itay.bluej.project;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.BlueJApp;
import me.itay.bluej.resourcelocation.BlueJResolvedResource.BlueJAfter;
import me.itay.bluej.resourcelocation.BlueJResolvedResource.BlueJResponse;
import net.minecraft.nbt.NBTTagCompound;

public class Project {
	
	private Folder projectRoot;
	private List<SourceFile> src = new ArrayList<>();
	
	public static final String MIME_BLUEJ = "bluej";
	public static final String MIME_PROJECT = MIME_BLUEJ + "/project";
	public static final String MIME_SRC_FILE = MIME_BLUEJ + "/source";
	
	public Project(Folder projectRoot) {
		this.projectRoot = projectRoot;
		
		sync();
	}
	
	public void sync() {
		src.clear();
		Folder srcFolder = projectRoot.getFolder("src");
		for(File srcf : srcFolder.search((f) -> MIME_SRC_FILE.equalsIgnoreCase(f.getData().getString("content_type")))) {
			src.add(new SourceFile(srcf));
		}
	}
	
	public Folder getProjectRoot() {
		return projectRoot;
	}
	
	public List<SourceFile> getSrc() {
		return src;
	}
	
	public static void createProject(Folder projectFile, String projectType, BlueJAfter after) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("content_type", MIME_PROJECT);
		
		Folder projectRoot = projectFile.getParent();
		createFolder(projectRoot, "src", () -> {
			createFolder(projectRoot, "res", () -> {
				createFolder(projectRoot, "build", () -> {
					projectRoot.add(new File(".bjproj", BlueJApp.id, compound), (r3, o3) -> {
						after.handle();							
					});
				});
			});				
		});
	}
	
	private static void createFolder(Folder parent, String name, BlueJAfter after) {
		if(!parent.hasFolder(name)) {
			parent.add(new Folder(name), (resp, ok) -> {
				after.handle();
			});
		}else {			
			after.handle();
		}
	}
	
	public static void loadProject(Folder projectFolder, BlueJResponse<Project> after) {
		if(!projectFolder.hasFile(".bjproj")) {
			if(projectFolder.getParent().hasFile(".bjproj")) {
				projectFolder = projectFolder.getParent();
			}else {
				after.handle(null);
			}
		}
		
		File projectFile = projectFolder.getFile(".bjproj");
		
		NBTTagCompound compound = projectFile.getData();
		if(!MIME_PROJECT.equalsIgnoreCase(compound.getString("content_type"))) {
			after.handle(null);
		}
		
		Folder projectRoot = projectFile.getParent();
		createFolder(projectRoot, "src", () -> {
			createFolder(projectRoot, "res", () -> {
				createFolder(projectRoot, "build", () -> {
					Project project = new Project(projectRoot);
					after.handle(project);
				});
			});				
		});
		
	}
	
	
}
