package fr.openai_test_unitaire_code;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import fr.openai_test_unitaire_code.window.OpenToolWindow;

import static fr.openai_test_unitaire_code.Utils.processSelectedSyntax;
import static fr.openai_test_unitaire_code.Utils.removeCommentary;

public class MouseListenerSimple implements MouseListener {
    private OpenToolWindow openToolWindow;

    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        int selRow = openToolWindow.getTree().getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
        TreePath selPath = openToolWindow.getTree().getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (selRow != -1) {
            if (mouseEvent.getClickCount() == 2) {
                String path = Arrays.stream(selPath.getPath()).skip(1)
                    .collect(Collectors.toList())
                    .stream()
                    .map(Object::toString)
                    .filter(l -> !l.equals("Select Path"))
                    .filter(l -> !l.equals("/"))
                    .collect(Collectors.joining("/"));
                path = openToolWindow.getRootPath() + "/" + path;
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
                if (virtualFile != null && !virtualFile.isDirectory()) {
                    String[] fracturedPath = StringUtils.split(path, '.');
                    String extension = "." + fracturedPath[fracturedPath.length - 1];
                    openToolWindow.setSelectedFileSyntax(extension);
                    openToolWindow.getInputToAnalyze().setSyntaxEditingStyle(processSelectedSyntax(openToolWindow.getSelectedFileSyntax()));
                    openToolWindow.getInputToAnalyze().setText(removeCommentary(LoadTextUtil.loadText(virtualFile).toString()));
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
    }

    public void setOpenToolWindow(final OpenToolWindow openToolWindow) {
        this.openToolWindow = openToolWindow;
        if (this.openToolWindow.getMouseListener() == null) {
            this.openToolWindow.setMouseListener(this);
        }
    }

    public OpenToolWindow getOpenToolWindow() {
        return this.openToolWindow;
    }
}
