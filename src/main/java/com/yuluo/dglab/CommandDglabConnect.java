package com.yuluo.dglab;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class CommandDglabConnect extends CommandBase {
    private static WebSocket ws;
    private static CommandDglabConnect instance;

    public CommandDglabConnect() {
        instance = this;
    }

    public static WebSocket getClientInstance() {
        if (instance != null && ws != null) {
            System.out.println("Returning WebSocket client instance: " + ws);
        } else {
            System.out.println("No WebSocket client instance found.");
        }
        return ws;
    }

    public interface WebSocketMessageCallback {
        void onWebSocketMessage(ICommandSender sender, String message);
    }

    private static WebSocketMessageCallback messageCallback;

    public static void setWebSocketMessageCallback(WebSocketMessageCallback callback) {
        messageCallback = callback;
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String subCommand = args[0].toLowerCase();
        if (!subCommand.equals("disconnect")) {
            final String deviceAddress = args[0];
            int wsPort = 23301;

            try {
                URI deviceUri = new URI("ws://" + deviceAddress + ":" + wsPort);

                WebSocketFactory factory = new WebSocketFactory();
                ws = factory.createSocket(deviceUri);

                ws.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) {
                        if (messageCallback != null) {
                            messageCallback.onWebSocketMessage(sender, message);
                        }
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        sender.addChatMessage(new ChatComponentText("Connected to " + deviceAddress));
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                        sender.addChatMessage(new ChatComponentText("Disconnected from " + deviceAddress));
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) {
                        sender.addChatMessage(new ChatComponentText("Error: " + cause.getMessage()));
                    }
                });

                ws.connect();

            } catch (URISyntaxException e) {
                sender.addChatMessage(new ChatComponentText("Error: " + e.getMessage()));
                e.printStackTrace();
            } catch (IOException | WebSocketException e) {
                e.printStackTrace();
            }
        } else {
            ws.disconnect();
            sender.addChatMessage(new ChatComponentText("Connection closed by player."));
        }
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dglab connect [your device address]";
    }
}
