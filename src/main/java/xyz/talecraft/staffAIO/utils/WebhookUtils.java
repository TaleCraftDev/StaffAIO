package xyz.talecraft.staffAIO.utils;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookUtils {

    public static void sendDiscordWebhook(String webhookUrl, JSONObject payload) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("StaffAIO"), () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = payload.toJSONString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204) {
                    Bukkit.getLogger().warning("Failed to send webhook: " + responseCode);
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Error while sending webhook: " + e.getMessage());
            }
        });
    }
}
