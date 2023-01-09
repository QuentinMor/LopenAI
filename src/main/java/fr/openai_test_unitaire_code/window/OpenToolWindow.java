package fr.openai_test_unitaire_code.window;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeFactoryImpl;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import fr.openai_test_unitaire_code.OpenAIKey;
import fr.openai_test_unitaire_code.OpenAiCall;

public class OpenToolWindow {

    private JButton sendRequest;

    private JPanel toolWindowContent;

    private JTree tree1;

    private RSyntaxTextArea inputToAnalyze;

    private JTextField phrase;

    private JTextField openaikey;

    private JButton saveopenapikey;

    private JLabel savedKey;

    private JRadioButton javaRadioButton;

    private JRadioButton jsRadioButton;

    private JRadioButton jspRadioButton;

    private JRadioButton sqlRadioButton;

    private JRadioButton aucunRadioButton;

    private ButtonGroup buttonGroup = new ButtonGroup();

    private String selectedFileSyntax = "";

    private final String rootPath;

    private final List<CompletionChoice> history;

    private OpenAiCall openAiCall;

    private MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            return;
        }

        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            int selRow = tree1.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
            TreePath selPath = tree1.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (selRow != -1) {
                if (mouseEvent.getClickCount() == 2) {
                    String path = Arrays.stream(selPath.getPath()).skip(1)
                        .collect(Collectors.toList())
                        .stream()
                        .map(Object::toString)
                        .filter(l -> !l.equals("Select Path"))
                        .filter(l -> !l.equals("/"))
                        .collect(Collectors.joining("/"));
                    path = rootPath + "/" + path;
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
                    if (virtualFile != null && !virtualFile.isDirectory()) {
                        String[] fracturedPath = StringUtils.split(path, '.');
                        String extension = "." + fracturedPath[fracturedPath.length - 1];
                        selectedFileSyntax = extension;
                        inputToAnalyze.setSyntaxEditingStyle(setSelectedSyntax());
                        inputToAnalyze.setText(removeCommentary(LoadTextUtil.loadText(virtualFile).toString()));
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
    };

    public OpenToolWindow(Project project) {
        inputToAnalyze.setSyntaxEditingStyle(setSelectedSyntax());
        history = new ArrayList<>();
        buttonGroup.add(javaRadioButton);
        buttonGroup.add(jsRadioButton);
        buttonGroup.add(jspRadioButton);
        buttonGroup.add(sqlRadioButton);
        buttonGroup.add(aucunRadioButton);
        FileSystemTreeFactoryImpl fileSystemTreeFactory = new FileSystemTreeFactoryImpl();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, true);
        fileChooserDescriptor.setRoots(project.getBaseDir());
        rootPath = project.getBasePath();
        tree1.setModel(fileSystemTreeFactory.createFileSystemTree(project, fileChooserDescriptor).getTree().getModel());
        tree1.setRootVisible(true);
        tree1.addMouseListener(mouseListener);
        sendRequest.addActionListener(e -> {
            ApplicationManager.getApplication().invokeLater(new Runnable() {

                @Override
                public void run() {
                    sendRequestOpenAI();
                }
            });
        });
        saveopenapikey.addActionListener(actionEvent -> OpenAIKey.getInstance(project).loadState(openaikey.getText()));
        openaikey.setText(OpenAIKey.getInstance(project).getState());
        openAiCall = new OpenAiCall();
    }

    public JPanel getContent() {
        return toolWindowContent;
    }

    private String removeCommentary(final String javaCode) {
        String newJavaCode = "";
        boolean isCommentary = false;
        for (int i = 0; i < javaCode.length(); i++) {
            if (i + 1 < javaCode.length() && javaCode.charAt(i) == '/' && javaCode.charAt(i + 1) == '*') {
                isCommentary = true;
            } else if (i >= 2 && javaCode.charAt(i - 2) == '*' && javaCode.charAt(i - 1) == '/') {
                isCommentary = false;
            }
            if (!isCommentary) {
                newJavaCode += javaCode.charAt(i);
            }
        }
        return newJavaCode;
    }

    private void sendRequestOpenAI() {
        OpenAiService service = new OpenAiService(openaikey.getText());
        CompletionRequest completionRequest =
            CompletionRequest.builder().prompt(phrase.getText() + " : \n" + inputToAnalyze.getText()).maxTokens(2000).model("text-davinci-003").echo(false).build();
        service.createCompletion(completionRequest).getChoices().forEach(t -> {
            history.add(t);
            inputToAnalyze.setText(t.getText());
        });
    }

    private String setSelectedSyntax() {
        if (selectedFileSyntax.equals(".java")) {
            return SyntaxConstants.SYNTAX_STYLE_JAVA;
        } else if (selectedFileSyntax.equals(".js")) {
            return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        } else if (selectedFileSyntax.equals(".jsp")) {
            return SyntaxConstants.SYNTAX_STYLE_JSP;
        } else if (selectedFileSyntax.equals(".sql")) {
            return SyntaxConstants.SYNTAX_STYLE_SQL;
        } else {
            return StringUtils.EMPTY;
        }
    }
}
