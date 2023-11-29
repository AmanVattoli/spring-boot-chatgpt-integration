package com.example.chatgptintegration.controller;

import com.example.chatgptintegration.dto.ChatMessagePrompt;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatGPTController {

    // In-memory store for conversation history
    private List<ChatMessage> conversationHistory = new ArrayList<>();

    @GetMapping("/getChat/{prompt}")
    public String getPrompt(@PathVariable String prompt) {

        OpenAiService service = new OpenAiService("");
        CompletionRequest completionRequest = CompletionRequest.builder().prompt(prompt).model("text-davinci-003")
                .echo(true).build();
        return service.createCompletion(completionRequest).getChoices().get(0).getText();
    }

    @PostMapping("/chat")
    public String getChat(@RequestBody ChatMessagePrompt prompt) {

        OpenAiService service = new OpenAiService("");

        // Add new user message to the conversation history
        conversationHistory.add(new ChatMessage("user", prompt.getChatMessage().get(0).getContent()));

        // Build the chat completion request with the entire conversation history
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(conversationHistory)
                .model("gpt-3.5-turbo-16k")
                .build();

        // Make the API call
        ChatCompletionChoice result = service.createChatCompletion(completionRequest).getChoices().get(0);

        // Add the assistant's reply to the conversation history
        conversationHistory.add(result.getMessage());

        // Return the assistant's reply
        return result.getMessage().getContent();
    }
}