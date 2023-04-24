package com.yuluo.dglab;

import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class CommandDglab extends CommandBase {
    private CommandDglabConnect connectHandler;
    private CommandDglabStrength strengthHandler;
    private CommandDglabPunish punishHandler;

    private List<String> subCommands = Arrays.asList("connect", "disconnect", "getstrength", "setmaxstrengtha", "setmaxstrengthb", "getmaxstrength", "setstrength", "addstrength", "setbasestrength", "punish", "ultrapunish", "setpunishtime", "setpunishrate", "getpunishsetting");

    public CommandDglab() {
        connectHandler = new CommandDglabConnect();
        strengthHandler = new CommandDglabStrength();
        punishHandler = new CommandDglabPunish();
    }

    @Override
    public String getCommandName() {
        return "dglab";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dglab <command>\n" +
                "/dglab connect <your phone's ip>\n" +
                "/dglab disconnect\n" +
                "/dglab getStrength\n" +
                "/dglab setMaxStrengthA [10~276]\n" +
                "/dglab setMaxStrengthB [10~276]\n" +
                "/dglab getMaxStrength\n" +
                "/dglab setStrength [0~maxStrengthA] [0~maxStrengthB]\n" +
                "/dglab addStrength <A/B> [value]\n" +
                "/dglab setBaseStrength [seconds]\n" +
                "/dglab setPunishTime [seconds]\n" +
                "/dglab setPunishRate [value]\n" +
                "/dglab getPunishSetting\n";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws WrongUsageException{
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            displayHelp(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommands.contains(subCommand)) {
            if ("connect".equalsIgnoreCase(subCommand)) {
                if (args.length < 2) {
                    throw new WrongUsageException(connectHandler.getCommandUsage(sender));
                }
                connectHandler.processCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if ("disconnect".equalsIgnoreCase(subCommand)) {
                connectHandler.processCommand(sender, args);
            } else if ("getStrength".equalsIgnoreCase(subCommand)) {
                strengthHandler.processCommand(sender, args);
            } else if ("setMaxStrengthA".equalsIgnoreCase(subCommand) || "setMaxStrengthB".equalsIgnoreCase(subCommand)){
                if (args.length < 2) {
                    throw new WrongUsageException(strengthHandler.getCommandUsage(sender));
                }
                strengthHandler.processCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if ("setStrength".equalsIgnoreCase(subCommand)){
                if (args.length < 3) {
                    throw new WrongUsageException(strengthHandler.getCommandUsage(sender));
                }
                strengthHandler.processCommand(sender, args);
            } else if ("addStrength".equalsIgnoreCase(subCommand)){
                if (args.length < 3) {
                    throw new WrongUsageException(strengthHandler.getCommandUsage(sender));
                }
                strengthHandler.processCommand(sender, args);
            } else if ("setBaseStrength".equalsIgnoreCase(subCommand)){
                if (args.length < 3) {
                    throw new WrongUsageException(strengthHandler.getCommandUsage(sender));
                }
                strengthHandler.processCommand(sender, args);
            } else if ("setPunishTime".equalsIgnoreCase(subCommand)){
                if (args.length < 2) {
                    throw new WrongUsageException(punishHandler.getCommandUsage(sender));
                }
                punishHandler.processCommand(sender, args);
            } else if ("setPunishRate".equalsIgnoreCase(subCommand)){
                if (args.length < 2) {
                    throw new WrongUsageException(punishHandler.getCommandUsage(sender));
                }
                punishHandler.processCommand(sender, args);
            } else if("getPunishSetting".equalsIgnoreCase(subCommand)){
                punishHandler.processCommand(sender, args);
            } else if("punish".equalsIgnoreCase(subCommand)){
                punishHandler.processCommand(sender, args);
            } else if("ultrapunish".equalsIgnoreCase(subCommand)){
                punishHandler.processCommand(sender, args);
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
    }
}
