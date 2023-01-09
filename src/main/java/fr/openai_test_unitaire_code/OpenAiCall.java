package fr.openai_test_unitaire_code;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

public class OpenAiCall implements Runnable {

    private OpenAICallComponent component;

    @Override
    public void run() {
        OpenAiService service = new OpenAiService(component.getOpenAiKey());
        CompletionRequest completionRequest =
            CompletionRequest.builder().prompt(component.getPhrase() + " : \n" + component.getTextArea1()).maxTokens(2000).model("text-davinci-003").echo(false).build();
        service.createCompletion(completionRequest).getChoices().forEach(t -> {
            component.getHistory().add(t);
            component.setTextArea1(t.getText());
        });
    }

    public void setComponent(final OpenAICallComponent component) {
        this.component = component;
    }

    public OpenAICallComponent getComponent() {
        return component;
    }
}
