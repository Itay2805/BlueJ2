package me.itay.bluej;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog.OpenFile;
import com.mrcrayfish.device.api.app.Dialog.SaveFile;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.languages.js.JavaScriptRuntime;
import me.itay.bluej.project.Project;
import me.itay.bluej.project.SourceFile;
import me.itay.bluej.resourcelocation.BlueJResolvedResource.BlueJAfter;
import net.minecraft.nbt.NBTTagCompound;

public class BlueJApp extends Application {
	
	public static String id = null;
	
	private static final int WIDTH = 362, HEIGHT = 164;
	
	private Button newProject, openProject, saveFile, exportProject;
	private Button newFile, deleteFile;
	private Button copyAll, paste;
	private Button run, stop;
	private Button settings;
	
	private TextArea codeEditor;
	private ItemList<String> files;
	// TODO add resources
	// TODO add terminal
	
	private int leftPanelWidth;
	private int middlePanelWidth;
//	private int rightPanelWidth;
	
	private int x;
	
	private void resetLayout() {
		x = 1;
		leftPanelWidth = 80;
		middlePanelWidth = 280;
	}
	
	private int getNextBtnPos() {
		int curr = x;
		x += 16;
		return curr;
	}
	
	private void addSeperator() {
		x += 2;
	}
	
	private Project currentProject;
	
	@Override
	public void init() {
		setDefaultWidth(WIDTH);
		setDefaultHeight(HEIGHT);
		
		// setup buttons
		
		resetLayout();
		
		newProject = new Button(getNextBtnPos(), 1, Icons.NEW_FOLDER);
		newProject.setToolTip("New Project", "Create new project");
		newProject.setClickListener(this::createProjectHandler);
		openProject = new Button(getNextBtnPos(), 1, Icons.LOAD);
		openProject.setToolTip("Open Project", "Open an exsting project");
		openProject.setClickListener(this::openProjectHandler);
		exportProject = new Button(getNextBtnPos(), 1, Icons.EXPORT);
		exportProject.setToolTip("Export Project", "Export the project as a runnable");
		
		addComponent(newProject);
		addComponent(openProject);
		addComponent(exportProject);
		
		addSeperator();
		
		newFile = new Button(getNextBtnPos(), 1, Icons.NEW_FILE);
		newFile.setToolTip("New File", "Create new file");
		deleteFile = new Button(getNextBtnPos(), 1, Icons.TRASH);
		deleteFile.setToolTip("Delete File", "Delete selected file");
		saveFile = new Button(getNextBtnPos(), 1, Icons.SAVE);
		saveFile.setToolTip("Save File", "Save current file");
		
		addComponent(newFile);
		addComponent(deleteFile);
		addComponent(saveFile);

		addSeperator();
		
		copyAll = new Button(getNextBtnPos(), 1, Icons.COPY);
		copyAll.setToolTip("Copy All", "Copy all the contents of the current file to the clipboard");
		paste = new Button(getNextBtnPos(), 1, Icons.CLIPBOARD);
		paste.setToolTip("Paste", "Paste the contents of the clipboard to the current file");
		
		addComponent(copyAll);
		addComponent(paste);
		
		addSeperator();
		
		run = new Button(getNextBtnPos(), 1, Icons.PLAY);
		run.setToolTip("Run", "Run code");
		stop = new Button(getNextBtnPos(), 1, Icons.STOP);
		stop.setToolTip("Stop", "Stop running code");
		
		addComponent(run);
		addComponent(stop);
		
		addSeperator();

		settings = new Button(getNextBtnPos(), 1, Icons.WRENCH);
		settings.setToolTip("Settings", "Open and edit project settings");
		
		addComponent(settings);
		
		// setup layout
		
		files = new ItemList<>(1, 18, leftPanelWidth, (HEIGHT - 18) / 15 + 1);
		
		codeEditor = new TextArea(1 + leftPanelWidth, 18, middlePanelWidth, HEIGHT - 23);
		codeEditor.setHighlight(new JavaScriptRuntime());
		
		addComponent(files);
		addComponent(codeEditor);
		
		// disable project buttons until a project is loaded
		
		id = getInfo().getFormattedId();
		
		toggleProjectButtons(false);
	}
	
	////////////////// Project Buttons //////////////////
	
	private void createProjectHandler(int x, int y, int button) {
		SaveFile file = new SaveFile(this, new NBTTagCompound());
		file.setResponseHandler((ok, f) -> {
			if(ok) {
				unloadProject(() -> {
					Folder root = f.getParent();
					Project.createProject(root, "test", () -> {
						loadProject(root, () -> {
							f.delete();
						});
					});
				});
			}
			return true;
		});
		openDialog(file);
	}
	
	private void openProjectHandler(int x, int y, int button) {
		OpenFile file = new OpenFile(this);
		file.setResponseHandler((ok, f) -> {
			if(ok) {
				unloadProject(() -> {
					loadProject(f.getParent(), () -> {});
				});
			}
			return true;
		});
		openDialog(file);
	}
	
	////////////////// other utils ////////////////// 
	
	private void toggleProjectButtons(boolean b) {
		codeEditor.setEditable(b);
		exportProject.setEnabled(b);
		newFile.setEnabled(b);
		run.setEnabled(b);
		stop.setEnabled(b);
		settings.setEnabled(b);
		toggleFileButtons(false);
	}
	
	private void toggleFileButtons(boolean b) {
		deleteFile.setEnabled(b);
		saveFile.setEnabled(b);
		paste.setEnabled(b);
		copyAll.setEnabled(b);
	}
	
	private void loadProject(Folder f, BlueJAfter after) {
		Project.loadProject(f, (proj) -> {
			this.currentProject = proj;
			for(SourceFile file : proj.getSrc()) {
				files.addItem(file.getFile().getName());
			}
			after.handle();
		});
	}
	
	private void unloadProject(BlueJAfter after) {
		// do saving and such
		
		currentProject = null;
		after.handle();
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		unloadProject(() -> {});
	}
	
	@Override
	public void load(NBTTagCompound tagCompound) {
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
		
	}
	
}
