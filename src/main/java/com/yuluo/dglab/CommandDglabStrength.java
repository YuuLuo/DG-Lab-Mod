package com.yuluo.dglab;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import com.neovisionaries.ws.client.WebSocket;
import org.json.JSONObject;

public class CommandDglabStrength extends CommandBase implements CommandDglabConnect.WebSocketMessageCallback {
    public static int maxStrengthA = 100;
    public static int maxStrengthB = 100;
    public static int currentStrengthA = 20;
    public static int currentStrengthB = 20;

    public static int getMaxStrengthA() {
        return maxStrengthA;
    }

    public static int getMaxStrengthB() {
        return maxStrengthB;
    }

    public static int getCurrentStrengthA() {
        return currentStrengthA;
    }

    public static int getCurrentStrengthB() {
        return currentStrengthB;
    }

    public static int getBaseStrengthA() {
        return baseStrengthA;
    }

    public static int getBaseStrengthB() {
        return baseStrengthB;
    }

    public static int baseStrengthA = 20;
    public static int baseStrengthB = 20;
    public static CommandDglabStrength instance = new CommandDglabStrength();


    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        String subCommand = args[0].toLowerCase();
        WebSocket client = CommandDglabConnect.getClientInstance();

        if (client == null || !client.isOpen()) {
            sender.addChatMessage(new ChatComponentText("WebSocket connection is not established."));
            return;
        }
        // Set the callback for the WebSocket messages
        CommandDglabConnect.setWebSocketMessageCallback(this);
        if(subCommand.equals("getstrength")) {
            queryStrength(sender, false);
        }else if(subCommand.equals("setstrength")){
            int strengthA = Integer.parseInt(args[1]);
            int strengthB = Integer.parseInt(args[2]);
            setStrength(sender, strengthA, strengthB);
        }else if (subCommand.equals("addstrength")) {
            if (args.length < 3) {
                sender.addChatMessage(new ChatComponentText("Usage: /dglab addstrength <A/B> [strength]"));
                return;
            }
            String channel = args[1].toLowerCase();
            int strength = Integer.parseInt(args[2]);
            addStrength(sender, channel, strength);
        }else if(subCommand.equals("setbasestrength")){
            baseStrengthA = Integer.parseInt(args[1]);
            baseStrengthB = Integer.parseInt(args[2]);
            sender.addChatMessage(new ChatComponentText("Successfully set baseStrengthA to: " + baseStrengthA));
            sender.addChatMessage(new ChatComponentText("Successfully set baseStrengthB to: " + baseStrengthB));
        }else if(subCommand.equals("getbasestrength")){
            sender.addChatMessage(new ChatComponentText("BaseStrengthA: " + baseStrengthA));
            sender.addChatMessage(new ChatComponentText("BaseStrengthB: " + baseStrengthB));
        } else if(subCommand.equals("setmaxstrength")){
            maxStrengthA = Integer.parseInt(args[1]);
            maxStrengthB = Integer.parseInt(args[2]);
            sender.addChatMessage(new ChatComponentText("Successfully set maxStrengthA to: " + maxStrengthA));
            sender.addChatMessage(new ChatComponentText("Successfully set maxStrengthB to: " + maxStrengthB));
        }else if(subCommand.equals("getmaxstrength")){
            sender.addChatMessage(new ChatComponentText("MaxStrengthA: " + maxStrengthA));
            sender.addChatMessage(new ChatComponentText("MaxStrengthB: " + maxStrengthB));
        }




    }
    @Override
    public void onWebSocketMessage(ICommandSender sender, String message) {
        JSONObject response = new JSONObject(message);
        System.out.println(message);
        int responseId = response.getInt("id");

        if (responseId == 100001) { // queryStrength response
            // Process the response
            JSONObject responseData = response.getJSONObject("data");
            int totalStrengthA = responseData.getInt("totalStrengthA") == 0 ? 0 : responseData.getInt("totalStrengthA") - 9;
            int totalStrengthB = responseData.getInt("totalStrengthB") == 0 ? 0 : responseData.getInt("totalStrengthB") - 9;

            // Send the message to the player
            sender.addChatMessage(new ChatComponentText("Strength values:"));
            sender.addChatMessage(new ChatComponentText("Total Strength A: " + totalStrengthA));
            sender.addChatMessage(new ChatComponentText("Total Strength B: " + totalStrengthB));
        } else if (responseId == 100002) { // setStrength response
            if (response.getInt("code") == 0) {
                System.out.println("Strength values set successfully.");
                //sender.addChatMessage(new ChatComponentText("Strength values set successfully."));
            } else {
                System.out.println("Failed to set strength values. ");
                sender.addChatMessage(new ChatComponentText("Failed to set strength values. Error: " + response.getString("result")));
            }
        }else if (responseId == 100003) { //addStrength response
            if (response.getInt("code") == 0) {
                System.out.println("Strength values added successfully.");
            } else {
                System.out.println("Failed to add strength values. ");
                sender.addChatMessage(new ChatComponentText("Failed to add strength values. Error: " + response.getString("result")));
            }
        }else if (responseId == 100000) { // queryStrength(Hide) response
            // Process the response
            JSONObject responseData = response.getJSONObject("data");
            int totalStrengthA = responseData.getInt("totalStrengthA") == 0 ? 0 : responseData.getInt("totalStrengthA") - 9;
            int totalStrengthB = responseData.getInt("totalStrengthB") == 0 ? 0 : responseData.getInt("totalStrengthB") - 9;
        }
    }

    public void setStrength(ICommandSender sender, int strengthA, int strengthB) {
        if (strengthA > maxStrengthA) {
            sender.addChatMessage(new ChatComponentText("Strength A exceeds the maximum limit (" + maxStrengthA + "). Setting to max limit."));
            strengthA = maxStrengthA;
        }
        if (strengthB > maxStrengthB) {
            sender.addChatMessage(new ChatComponentText("Strength B exceeds the maximum limit (" + maxStrengthB + "). Setting to max limit."));
            strengthB = maxStrengthB;
        }

        // 获取已建立连接的 WebSocketClient
        WebSocket client = CommandDglabConnect.getClientInstance();

        if (client == null || !client.isOpen()) {
            sender.addChatMessage(new ChatComponentText("WebSocket connection is not established."));
            return;
        }
        currentStrengthA = strengthA;
        currentStrengthB = strengthB;
        strengthA = strengthA==0?0:strengthA+9;
        strengthB = strengthB==0?0:strengthB+9;


        // 创建 JSON 请求
        JSONObject requestData = new JSONObject();
        requestData.put("strengthA", strengthA);
        requestData.put("strengthB", strengthB);

        JSONObject request = new JSONObject();
        request.put("id", 100002);
        request.put("method", "setStrength");
        request.put("data", requestData);

        // 发送 JSON 请求
        client.sendText(request.toString());
    }

    public void addStrength(ICommandSender sender, String channel, int addedStrength) {
        queryStrength(sender, true);
        boolean channelA;
        if (channel.equals("a")) {
            channelA = true;
            if(currentStrengthA + addedStrength > maxStrengthA){
                addedStrength = maxStrengthA - currentStrengthA;
                sender.addChatMessage(new ChatComponentText("Strength A exceeds the maximum limit (" + maxStrengthA + "). Setting to max limit."));
            }
            // 更新 currentStrengthA
            currentStrengthA = currentStrengthA + addedStrength;
        } else if (channel.equals("b")) {
            channelA = false;
            if(currentStrengthB + addedStrength > maxStrengthB){
                addedStrength = maxStrengthB - currentStrengthB;
                sender.addChatMessage(new ChatComponentText("Strength B exceeds the maximum limit (" + maxStrengthB + "). Setting to max limit."));
            }
            // 更新 currentStrengthB
            currentStrengthB = currentStrengthB + addedStrength;
        } else {
            sender.addChatMessage(new ChatComponentText("Invalid channel. Please use 'a' or 'b'."));
            return;
        }

        // 获取已建立连接的 WebSocketClient
        WebSocket client = CommandDglabConnect.getClientInstance();

        if (client == null || !client.isOpen()) {
            sender.addChatMessage(new ChatComponentText("WebSocket connection is not established."));
            return;
        }

        // 创建 JSON 请求
        JSONObject requestData = new JSONObject();
        requestData.put("channel", channelA);
        requestData.put("strength", addedStrength);

        JSONObject request = new JSONObject();
        request.put("id", 100003); // 使用唯一ID
        request.put("method", "addStrength");
        request.put("data", requestData);

        // 发送 JSON 请求
        client.sendText(request.toString());
        System.out.println(request.toString());
    }

    public static void queryStrength(ICommandSender sender, boolean silent) {
        // 获取已建立连接的 WebSocketClient
        WebSocket client = CommandDglabConnect.getClientInstance();

        if (client == null || !client.isOpen()) {
            if (!silent) {
                sender.addChatMessage(new ChatComponentText("WebSocket connection is not established."));
            }
            return;
        }

        // 创建 JSON 请求
        JSONObject request = new JSONObject();
        if(!silent)
            request.put("id", 100001);
        else
            request.put("id", 100000);

        request.put("method", "queryStrength");

        // 发送 JSON 请求
        client.sendText(request.toString());
    }
}
