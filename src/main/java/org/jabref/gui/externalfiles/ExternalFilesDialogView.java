package org.jabref.gui.externalfiles;

import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.jabref.gui.DialogService;
import org.jabref.gui.Globals;
import org.jabref.gui.StateManager;
import org.jabref.gui.externalfiles.FindUnlinkedFilesDialog.FileNodeWrapper;
import org.jabref.gui.externalfiletype.ExternalFileType;
import org.jabref.gui.externalfiletype.ExternalFileTypes;
import org.jabref.gui.util.BackgroundTask;
import org.jabref.gui.util.BaseDialog;
import org.jabref.gui.util.ViewModelListCellFactory;
import org.jabref.gui.util.ViewModelTreeCellFactory;
import org.jabref.logic.l10n.Localization;
import org.jabref.preferences.PreferencesService;

import com.airhacks.afterburner.views.ViewLoader;

public class ExternalFilesDialogView extends BaseDialog<Void> {

    @FXML private TextField directoryPathField;
    @FXML private ComboBox<FileChooser.ExtensionFilter> fileTypeSelection;
    @FXML private TreeView<FileNodeWrapper> tree;
    @FXML private VBox panelSearchProgress;
    @FXML private Button buttonScan;
    @Inject private PreferencesService preferencesService;
    @Inject private DialogService dialogService;
    @Inject private StateManager stateManager;

    private ExternalFilesDialogViewModel viewModel;
    private BackgroundTask<CheckBoxTreeItem<FileNodeWrapper>> findUnlinkedFilesTask;


    public ExternalFilesDialogView() {
        this.setTitle(Localization.lang("Search for unlinked local files"));

        ViewLoader.view(this)
                  .load()
                  .setAsContent(this.getDialogPane());

        setResultConverter(buttonPressed -> {
            if (buttonPressed == ButtonType.OK) {
                startImport();
            } else {
                if (findUnlinkedFilesTask != null) {
                    findUnlinkedFilesTask.cancel();
                }
            }
            return null;
        });
    }

    private void startImport() {
        viewModel.startImport();
    }

    @FXML
    private void initialize() {

        viewModel = new ExternalFilesDialogViewModel(dialogService, ExternalFileTypes.getInstance(), Globals.undoManager, Globals.getFileUpdateMonitor(), preferencesService, stateManager);
        viewModel.directoryPath().bindBidirectional(directoryPathField.textProperty());

        fileTypeSelection.setItems(FXCollections.observableArrayList(viewModel.getFileFilters()));
        new ViewModelListCellFactory<FileChooser.ExtensionFilter>()
        .withText(fileFilter -> fileFilter.getDescription() + fileFilter.getExtensions().stream().collect(Collectors.joining(", ", " (", ")")))
        .withIcon(fileFilter -> ExternalFileTypes.getInstance().getExternalFileTypeByExt(fileFilter.getExtensions().get(0))
                                                 .map(ExternalFileType::getIcon)
                                                 .orElse(null))
        .install(fileTypeSelection);

        new ViewModelTreeCellFactory<FileNodeWrapper>()
                .withText(node -> {
                    if (Files.isRegularFile(node.path)) {
                        // File
                        return node.path.getFileName().toString();
                    } else {
                        // Directory
                        return node.path.getFileName() + " (" + node.fileCount + " file" + (node.fileCount > 1 ? "s" : "") + ")";
                    }
                })
                .install(tree);
    }

    @FXML
    void browseFileDirectory(ActionEvent event) {
        viewModel.browseFileDirectory();
    }

    @FXML
    void collapseAll(ActionEvent event) {

    }

    @FXML
    void expandAll(ActionEvent event) {

    }

    @FXML
    void scanFiles(ActionEvent event) {

    }

    @FXML
    void selectAll(ActionEvent event) {

    }

    @FXML
    void unselectAll(ActionEvent event) {

    }

}
