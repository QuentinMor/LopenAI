package fr.openai_test_unitaire_code.window;

import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeFactoryImpl;
import com.intellij.openapi.project.Project;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import fr.openai_test_unitaire_code.MouseListenerSimple;
import fr.openai_test_unitaire_code.OpenAIKey;
import fr.openai_test_unitaire_code.OpenAiCall;

import static fr.openai_test_unitaire_code.Utils.processSelectedSyntax;

public class OpenToolWindow {

    private JButton sendRequest;

    private JPanel toolWindowContent;

    private JTree tree;

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

    private MouseListenerSimple mouseListener;

    public OpenToolWindow(Project project) {
        inputToAnalyze.setSyntaxEditingStyle(processSelectedSyntax(selectedFileSyntax));
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
        mouseListener = new MouseListenerSimple();
        mouseListener.setOpenToolWindow(this);
        tree.setModel(fileSystemTreeFactory.createFileSystemTree(project, fileChooserDescriptor).getTree().getModel());
        tree.setRootVisible(true);
        tree.addMouseListener(mouseListener);
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

    private void sendRequestOpenAI() {
        OpenAiService service = new OpenAiService(openaikey.getText());
        CompletionRequest completionRequest =
            CompletionRequest.builder().prompt(phrase.getText() + " : \n" + inputToAnalyze.getText()).maxTokens(2000).model("text-davinci-003").echo(false).build();
        service.createCompletion(completionRequest).getChoices().forEach(t -> {
            history.add(t);
            inputToAnalyze.setText(t.getText());
        });
    }

    public JTree getTree() {
        return tree;
    }

    public void setTree(final JTree tree) {
        this.tree = tree;
    }

    public RSyntaxTextArea getInputToAnalyze() {
        return inputToAnalyze;
    }

    public void setInputToAnalyze(final RSyntaxTextArea inputToAnalyze) {
        this.inputToAnalyze = inputToAnalyze;
    }

    public String getSelectedFileSyntax() {
        return selectedFileSyntax;
    }

    public void setSelectedFileSyntax(final String selectedFileSyntax) {
        this.selectedFileSyntax = selectedFileSyntax;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setMouseListener(final MouseListenerSimple mouseListener) {
        this.mouseListener = mouseListener;
        if (this.mouseListener.getOpenToolWindow() == null) {
            this.mouseListener.setOpenToolWindow(this);
        }
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }
}
