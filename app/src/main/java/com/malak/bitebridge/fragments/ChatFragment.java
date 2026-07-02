package com.malak.bitebridge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.BuildConfig;
import com.malak.bitebridge.R;
import com.malak.bitebridge.adapters.ChatAdapter;
import com.malak.bitebridge.database.MenuRepository;
import com.malak.bitebridge.models.MenuItem;
import com.malak.bitebridge.utils.NetworkReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {

    // Injected at build time from local.properties via BuildConfig — never hard-code secrets.
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/" +
        "models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private RecyclerView recyclerView;
    private EditText etInput;
    private Button btnSend;
    private ChatAdapter adapter;
    private List<ChatAdapter.ChatMessage> messages;
    private OkHttpClient httpClient;
    private String menuContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.rv_chat);
        etInput = view.findViewById(R.id.et_chat_input);
        btnSend = view.findViewById(R.id.btn_send);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        httpClient = new OkHttpClient();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Build menu context for AI
        menuContext = buildMenuContext();

        // Welcome message
        addAiMessage("👋 Hi! I'm your BiteBridge AI assistant!\n\n" +
                "I can help you with:\n" +
                "• Menu recommendations\n" +
                "• Ingredient questions\n" +
                "• Dietary suggestions\n\n" +
                "What can I get for you today?");

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private String buildMenuContext() {
        MenuRepository repo =
                new MenuRepository(requireContext());
        List<MenuItem> items = repo.getAllItems();
        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful assistant for " +
                "BiteBridge restaurant. " +
                "Here is the current menu:\n\n");
        for (MenuItem item : items) {
            sb.append("- ").append(item.getName())
                    .append(" (").append(item.getCategory())
                    .append(") — $").append(item.getPrice())
                    .append(": ").append(item.getDescription())
                    .append("\n");
        }
        sb.append("\nAnswer questions about the menu, " +
                "suggest items based on preferences, " +
                "and help customers make decisions. " +
                "Keep responses friendly and concise.");
        return sb.toString();
    }

    private void sendMessage() {
        String userText = etInput.getText().toString().trim();
        if (userText.isEmpty()) return;

        if (!NetworkReceiver.isConnected(requireContext())) {
            Toast.makeText(requireContext(),
                    "No internet connection",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to chat
        addUserMessage(userText);
        etInput.setText("");
        btnSend.setEnabled(false);

        // Add typing indicator
        addAiMessage("...");
        int typingIndex = messages.size() - 1;

        // Build request
        try {
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();

            // System context
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "user");
            JSONArray systemParts = new JSONArray();
            JSONObject systemText = new JSONObject();
            systemText.put("text", menuContext);
            systemParts.put(systemText);
            systemMsg.put("parts", systemParts);
            contents.put(systemMsg);

            // User message
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            JSONArray userParts = new JSONArray();
            JSONObject userTextObj = new JSONObject();
            userTextObj.put("text", userText);
            userParts.put(userTextObj);
            userMsg.put("parts", userParts);
            contents.put(userMsg);

            requestBody.put("contents", contents);

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(
                    new Callback() {
                        @Override
                        public void onFailure(
                                @NonNull Call call,
                                @NonNull IOException e) {
                            requireActivity().runOnUiThread(() -> {
                                updateMessage(typingIndex,
                                        "❌ Failed to get response. " +
                                                "Please try again.");
                                btnSend.setEnabled(true);
                            });
                        }

                        @Override
                        public void onResponse(
                                @NonNull Call call,
                                @NonNull Response response)
                                throws IOException {
                            String responseBody =
                                    response.body() != null ?
                                    response.body().string() : "";

                            requireActivity().runOnUiThread(() -> {
                                try {
                                    JSONObject json =
                                            new JSONObject(responseBody);

                                    // Check for API error first
                                    if (json.has("error")) {
                                        String errorMsg = json
                                                .getJSONObject("error")
                                                .getString("message");
                                        updateMessage(typingIndex,
                                                "❌ API Error: " + errorMsg);
                                        btnSend.setEnabled(true);
                                        return;
                                    }

                                    // Parse successful response
                                    String aiText = json
                                            .getJSONArray("candidates")
                                            .getJSONObject(0)
                                            .getJSONObject("content")
                                            .getJSONArray("parts")
                                            .getJSONObject(0)
                                            .getString("text");

                                    updateMessage(typingIndex,
                                            aiText.trim());

                                } catch (Exception e) {
                                    // Show raw response for debugging
                                    updateMessage(typingIndex,
                                            "❌ Error: " + e.getMessage() +
                                            "\nResponse: " +
                                            responseBody.substring(0,
                                                    Math.min(200,
                                                            responseBody.length())));
                                }
                                btnSend.setEnabled(true);
                            });
                        }   
                    });

        } catch (Exception e) {
            addAiMessage("❌ Error building request.");
            btnSend.setEnabled(true);
        }
    }

    private void addUserMessage(String text) {
        messages.add(new ChatAdapter.ChatMessage(text, true));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addAiMessage(String text) {
        messages.add(new ChatAdapter.ChatMessage(text, false));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void updateMessage(int index, String text) {
        if (index < messages.size()) {
            messages.get(index).text = text;
            adapter.notifyItemChanged(index);
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }
}