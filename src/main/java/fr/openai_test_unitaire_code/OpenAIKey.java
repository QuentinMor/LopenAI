package fr.openai_test_unitaire_code;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;

@State(name = "OpenAIKey")
@Storage(StoragePathMacros.WORKSPACE_FILE)
public class OpenAIKey implements PersistentStateComponent<String> {

    private String key = "";

    public String getState() {
        return key;
    }

    public void loadState(String key) {
        this.key = key;
    }

    public static PersistentStateComponent<String> getInstance(Project projet) {
        return projet.getService(OpenAIKey.class);
    }
}
