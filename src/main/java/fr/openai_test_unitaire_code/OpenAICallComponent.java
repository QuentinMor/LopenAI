package fr.openai_test_unitaire_code;

import java.util.ArrayList;
import java.util.List;

import com.theokanning.openai.completion.CompletionChoice;

public class OpenAICallComponent {
    private String openAiKey;
    private String phrase;
    private String textArea1;

    private final List<CompletionChoice> history;

    public OpenAICallComponent(String openAiKey, String phrase, String textArea1) {
        this.openAiKey = openAiKey;
        this.phrase = phrase;
        this.textArea1 = textArea1;
        this.history = new ArrayList<>();
    }

    public String getOpenAiKey() {
        return openAiKey;
    }

    public void setOpenAiKey(final String openAiKey) {
        this.openAiKey = openAiKey;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(final String phrase) {
        this.phrase = phrase;
    }

    public String getTextArea1() {
        return textArea1;
    }

    public void setTextArea1(final String textArea1) {
        this.textArea1 = textArea1;
    }

    public List<CompletionChoice> getHistory() {
        return history;
    }
}
