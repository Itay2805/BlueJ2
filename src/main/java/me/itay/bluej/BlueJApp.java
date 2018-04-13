package me.itay.bluej;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Laptop;
import me.itay.bluej.dialogs.CreateSourceFile;
import me.itay.bluej.dialogs.SelectLanguageDialog;
import me.itay.bluej.languages.BlueJRunResponse;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.FilenameUtils;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog.Input;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.io.Folder;

import me.itay.bluej.dialogs.SelectFolder;
import me.itay.bluej.languages.BlueJLanguage;
import me.itay.bluej.languages.BlueJRuntimeManager;
import me.itay.bluej.languages.js.JavaScriptRuntime;
import me.itay.bluej.project.Project;
import me.itay.bluej.project.SourceFile;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

public class BlueJApp extends Application {

	public static String id = null;

	private static final int WIDTH = 362, HEIGHT = 164;

	private Button btnNewProject, btnOpenProject, btnSaveFile, btnExportProject;
	private Button btnNewFile, btnDeleteFile;
	private Button btnCopyAll, btnPaste;
	private Button btnRun, btnStop;
	private Button btnSettings;

	private TextArea txtCodeEditor;
	private ItemList<String> lstFiles;
	// TODO add resources
	// TODO add terminal

	private int leftPanelWidth;
	private int middlePanelWidth;
	// private int rightPanelWidth;

	private int x;

	private List<Component> enabledComponents = new ArrayList<>();

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
	private String currentSourceFile;

	@Override
	public void init() {
		setDefaultWidth(WIDTH);
		setDefaultHeight(HEIGHT);

		// setup buttons

		resetLayout();

		btnNewProject = new Button(getNextBtnPos(), 1, Icons.NEW_FOLDER);
		btnNewProject.setToolTip("New Project", "Create new project");
		btnNewProject.setClickListener(this::createProjectHandler);
		btnOpenProject = new Button(getNextBtnPos(), 1, Icons.LOAD);
		btnOpenProject.setToolTip("Open Project", "Open an exsting project");
		btnOpenProject.setClickListener(this::openProjectHandler);
		btnExportProject = new Button(getNextBtnPos(), 1, Icons.EXPORT);
		btnExportProject.setToolTip("Export Project", "Export the project as a runnable");

		addComponent(btnNewProject);
		addComponent(btnOpenProject);
		addComponent(btnExportProject);

		addSeperator();

		btnNewFile = new Button(getNextBtnPos(), 1, Icons.NEW_FILE);
		btnNewFile.setToolTip("New File", "Create new file");
		btnNewFile.setClickListener(this::createSourceFileHandler);
		btnDeleteFile = new Button(getNextBtnPos(), 1, Icons.TRASH);
		btnDeleteFile.setToolTip("Delete File", "Delete selected file");
		btnDeleteFile.setClickListener(this::deleteSourceFileHandler);
		btnSaveFile = new Button(getNextBtnPos(), 1, Icons.SAVE);
		btnSaveFile.setToolTip("Save File", "Save current file");
		btnSaveFile.setClickListener((x, y, b)->this.saveSourceFile());

		addComponent(btnNewFile);
		addComponent(btnDeleteFile);
		addComponent(btnSaveFile);

		addSeperator();

		btnCopyAll = new Button(getNextBtnPos(), 1, Icons.COPY);
		btnCopyAll.setToolTip("Copy All", "Copy all the contents of the current file to the clipboard");
		btnPaste = new Button(getNextBtnPos(), 1, Icons.CLIPBOARD);
		btnPaste.setToolTip("Paste", "Paste the contents of the clipboard to the current file");

		addComponent(btnCopyAll);
		addComponent(btnPaste);

		addSeperator();

		btnRun = new Button(getNextBtnPos(), 1, Icons.PLAY);
		btnRun.setToolTip("Run", "Run code");
        btnRun.setClickListener(this::runHandler);
		btnStop = new Button(getNextBtnPos(), 1, Icons.STOP);
		btnStop.setToolTip("Stop", "Stop running code");

		addComponent(btnRun);
		addComponent(btnStop);

		addSeperator();

		btnSettings = new Button(getNextBtnPos(), 1, Icons.WRENCH);
		btnSettings.setToolTip("Settings", "Open and edit project settings");

		addComponent(btnSettings);

		// setup layout

		lstFiles = new ItemList<>(1, 18, leftPanelWidth, (HEIGHT - 18) / 15 + 1);
		lstFiles.setItemClickListener(this::fileSelectedHandler);

		txtCodeEditor = new TextArea(1 + leftPanelWidth, 18, middlePanelWidth, HEIGHT - 23);
		txtCodeEditor.setKeyListener((c)->{
		    this.btnSaveFile.setEnabled(true);
		    return true;
        });

		addComponent(lstFiles);
		addComponent(txtCodeEditor);

		// disable project buttons until a project is loaded

		id = getInfo().getFormattedId();

		toggleProjectButtons(false);
	}

	////////////////// Project Buttons //////////////////

	private void createProjectHandler(int x, int y, int button) {
		SelectFolder file = new SelectFolder(this);
		file.setResponseHandler((ok, f) -> {
			if (ok) {
                SelectLanguageDialog dialog = new SelectLanguageDialog("Select Language");
                dialog.setResponseHandler((s, e)->{
                    if(s){
                        unloadProject(() -> {
                            BlueJLanguage lang = BlueJRuntimeManager.getLanguage(e);
                            Project.createProject(f, lang, () -> {
                                loadProject(f, () -> {
                                });
                            });
                        });
                    }
                    return true;
                });
                openDialog(dialog);
			}
			return true;
		});
		openDialog(file);
	}

	private void openProjectHandler(int x, int y, int button) {
		SelectFolder file = new SelectFolder(this);
		file.setResponseHandler((ok, f) -> {
			if (ok) {
				unloadProject(() -> {
					loadProject(f, () -> {
					});
				});
			}
			return true;
		});
		openDialog(file);
	}

	////////////////// Project Sources Buttons //////////////////

	private void createSourceFileHandler(int x, int y, int button) {
		CreateSourceFile file = new CreateSourceFile("File name");
		file.setResponseHandler((ok, f) -> {
			if (ok) {
				SourceFile createdFile = currentProject.createSourceFile(f, () -> {
					lstFiles.setItems(new ArrayList<>());
					for (SourceFile srcF : currentProject.getSrc()) {
						lstFiles.addItem(srcF.getFile().getName());
					}
                });
                if(file.getIsStartupFile().isSelected()) {
                    currentProject.setStartupFile(createdFile);
                }
            }
			return true;
		});
		openDialog(file);
	}

	private void deleteSourceFileHandler(int x, int y, int button) {
		String name = lstFiles.getSelectedItem();
		if(Objects.equals(name, currentSourceFile)) {
			toggleFileButtons(false);
		}
		currentProject.deleteSourceFile(name, () -> {
			lstFiles.setItems(new ArrayList<>());
			for (SourceFile file : currentProject.getSrc()) {
				lstFiles.addItem(file.getFile().getName());
			}
		});
		this.txtCodeEditor.clear();
	}

	private void saveSourceFile(){
        String name = lstFiles.getSelectedItem();
        SourceFile source = currentProject.getSourceFile(name);
        if (source != null)
            source.setSource(txtCodeEditor.getText(), () -> {
            });
        this.btnSaveFile.setEnabled(false);
        this.enabledComponents.remove(this.btnSaveFile);
    }

	////////////////// Files listener //////////////////

	private void fileSelectedHandler(String item, int index, int mouseButton) {
		SourceFile file = currentProject.getSourceFile(item);
		toggleFileButtons(true);
		txtCodeEditor.setText(file.getSource().replace("\n\n", "\n"));
		currentSourceFile = item;
	}

    private void runHandler(int x, int y, int button) {
        if(currentProject.getProjectLanguage() == null) {
            openDialog(new Dialog.Message("Unknown file type"));
            return;
        }

        this.saveSourceFile();

        BlueJRunResponse resp = currentProject.getProjectLanguage().run(currentProject);
        // @Todo: open the console to see whats going on
		BlueJConsoleDialog message = new BlueJConsoleDialog();
		message.setOutput(resp.getOutput());
        this.openDialog(message);
    }

	////////////////// other utils //////////////////

	private void toggleProjectButtons(boolean b) {
		txtCodeEditor.setText("");
		btnExportProject.setEnabled(b);
		btnNewFile.setEnabled(b);
		btnRun.setEnabled(b);
		btnStop.setEnabled(b);
		btnSettings.setEnabled(b);
		if (!b){
            lstFiles.setItems(new ArrayList<>());
            this.enabledComponents.remove(btnExportProject);
            this.enabledComponents.remove(btnNewFile);
            this.enabledComponents.remove(btnStop);
            this.enabledComponents.remove(btnSettings);
        }
		toggleFileButtons(false);
	}

	private void toggleFileButtons(boolean b) {
		txtCodeEditor.setEditable(b);
		btnDeleteFile.setEnabled(b);
		btnSaveFile.setEnabled(b);
		btnPaste.setEnabled(b);
		btnCopyAll.setEnabled(b);
		if(!b){
		    this.enabledComponents.clear();
		    this.enabledComponents.remove(btnDeleteFile);
		    this.enabledComponents.remove(btnSaveFile);
		    this.enabledComponents.remove(btnPaste);
		    this.enabledComponents.remove(btnCopyAll);
        }
	}

	private void loadProject(Folder f, Runnable runnable) {
		if(!Project.loadProject(f, loadProject(runnable))){
		    this.openDialog(new Dialog.Message("Could not load project! Project does not exist!"));
        }
	}

	private Consumer<Project> loadProject(Runnable runnable) {
        return (proj) -> {
            this.currentProject = proj;
            if (!proj.getSrc().isEmpty()) {
                ArrayList<String> list = new ArrayList<>();
                for (SourceFile file : proj.getSrc()) {
                    list.add(file.getFile().getName());
                }
                lstFiles.setItems(list);
            }
            this.txtCodeEditor.setHighlight(this.currentProject.getProjectLanguage());
            toggleProjectButtons(true);
            runnable.run();
        };
    }

	private void unloadProject(Runnable runnable) {
		// do saving and such

		currentProject = null;
		toggleProjectButtons(false);
		runnable.run();
	}

	private void loadSourceFile(String name) {
		for (SourceFile f : currentProject.getSrc()) {
			if (name.equalsIgnoreCase(f.getName())) {
				toggleFileButtons(true);
				txtCodeEditor.setText(f.getSource());
				return;
			}
		}
		toggleFileButtons(false);
	}

	@Override
	public void onClose() {
		super.onClose();

		unloadProject(() -> {
		});
	}

	@Override
	public void load(NBTTagCompound tagCompound) {

	}

	@Override
	public void save(NBTTagCompound tagCompound) {

	}

}
