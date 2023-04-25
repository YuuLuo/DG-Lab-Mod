package com.yuluo.dglab;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;

public class CommandDglab extends CommandBase {
    private final CommandDglabConnect connectHandler;
    private final CommandDglabStrength strengthHandler;
    private final CommandDglabPunish punishHandler;

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
                "/dglab setStrength [0~maxStrengthA] [0~maxStrengthB]\n" +
                "/dglab getStrength\n" +
                "/dglab setMaxStrength [20~276] [20~276]\n" +
                "/dglab getMaxStrength\n" +
                "/dglab addStrength <A/B> [value]\n" +
                "/dglab setBaseStrength [value]\n" +
                "/dglab getBaseStrength\n" +
                "/dglab setPunishTime [seconds]\n" +
                "/dglab setPunishRate [value]\n" +
                "/dglab getPunishSetting\n";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            displayHelp(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "connect":
                if (args.length < 2) {
                    throw new WrongUsageException(connectHandler.getCommandUsage(sender));
                }
                connectHandler.processCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                break;

            case "disconnect":
                connectHandler.processCommand(sender, args);
                break;

            case "getstrength":
            case "getbasestrength":
            case "getmaxstrength":
                strengthHandler.processCommand(sender, args);
                break;

            case "setmaxstrength":
            case "setbasestrength":
            case "addstrength":
            case "setstrength":
                if (args.length < 3) {
                    throw new WrongUsageException(strengthHandler.getCommandUsage(sender));
                }
                strengthHandler.processCommand(sender, args);
                break;

            case "setpunishtime":
            case "setpunishrate":
                if (args.length < 2) {
                    throw new WrongUsageException(punishHandler.getCommandUsage(sender));
                }
                punishHandler.processCommand(sender, args);
                break;

            case "getpunishsetting":
            case "punish":
            case "ultrapunish":
                punishHandler.processCommand(sender, args);
                break;

            default:
                System.out.println("2");
                throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
    }
}
