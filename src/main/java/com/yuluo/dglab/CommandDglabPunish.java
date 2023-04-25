package com.yuluo.dglab;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Timer;
import java.util.TimerTask;

public class CommandDglabPunish extends CommandBase {

    private static boolean punishActive = false;
    private static boolean ultraPunishActive = false;
    private static int punishTime = 3; // 设置惩罚持续时间，单位为秒
    private static int punishRate = 5; // 设置惩罚比例
    private EntityPlayer playerToPunish;
    private long punishEndTime = 0;


    private Timer ultraPunishTimer;


    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (punishActive && System.currentTimeMillis() > punishEndTime) {
                float damage = event.ammount;
                System.out.println("received punish damage: " + damage);
                startPunish(player, damage);
            } else if (ultraPunishActive) {
                float damage = event.ammount;
                startUltraPunish(player, damage);
            }
            System.out.println(punishActive + " " + (System.currentTimeMillis() > punishEndTime) + " " + System.currentTimeMillis() + " " + punishEndTime);
            float damage = event.ammount;
            System.out.println("received damage: " + damage);
        }
    }


    private void startPunish(final ICommandSender sender, float damage) {
        // 获取当前强度值
        CommandDglabStrength.queryStrength(sender, true);

        // 计算惩罚强度值
        int punishStrengthA = CommandDglabStrength.getCurrentStrengthA() + (int) (damage * punishRate);
        int punishStrengthB = CommandDglabStrength.getCurrentStrengthB() + (int) (damage * punishRate);


        // 设置新的强度值
        CommandDglabStrength.instance.setStrength(null, punishStrengthA, punishStrengthB);

        ChatComponentText message = new ChatComponentText("Received damage: ");
        message.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText damageComponent = new ChatComponentText(String.valueOf(damage));
        damageComponent.getChatStyle().setColor(EnumChatFormatting.AQUA);

        ChatComponentText part2 = new ChatComponentText(" HP, setting strength to: ");
        part2.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText strengthComponent = new ChatComponentText(String.valueOf(punishStrengthA));
        strengthComponent.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

        ChatComponentText part3 = new ChatComponentText(", ");
        part3.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText strengthComponent2 = new ChatComponentText(String.valueOf(punishStrengthB));
        strengthComponent2.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

        message.appendSibling(damageComponent);
        message.appendSibling(part2);
        message.appendSibling(strengthComponent);
        message.appendSibling(part3);
        message.appendSibling(strengthComponent2);

        sender.addChatMessage(message);
        // 在惩罚结束时恢复基础强度值
        punishEndTime = System.currentTimeMillis() + (punishTime * 1000L);
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() {
                    CommandDglabStrength.instance.setStrength(sender, CommandDglabStrength.getBaseStrengthA(), CommandDglabStrength.getBaseStrengthB());
                }
            },
            punishTime * 1000L
        );
    }

    private void startUltraPunish(final ICommandSender sender, float damage) {
        System.out.println(CommandDglabStrength.getCurrentStrengthA() + " " + CommandDglabStrength.getCurrentStrengthB());
        // 获取当前强度值
        CommandDglabStrength.queryStrength(sender, true);
        // 计算惩罚强度值
        int punishStrengthA = (int) (damage * punishRate);
        int punishStrengthB = (int) (damage * punishRate);


        if (punishStrengthA + CommandDglabStrength.getCurrentStrengthA() > CommandDglabStrength.getMaxStrengthA()) {
            sender.addChatMessage(new ChatComponentText("Strength A exceeds the maximum limit (" + CommandDglabStrength.getMaxStrengthA() + "). Setting to max limit."));
            punishStrengthA = CommandDglabStrength.getMaxStrengthA() - CommandDglabStrength.getCurrentStrengthA();
        }
        if (punishStrengthB + CommandDglabStrength.getCurrentStrengthB() > CommandDglabStrength.getMaxStrengthB()) {
            sender.addChatMessage(new ChatComponentText("Strength B exceeds the maximum limit (" + CommandDglabStrength.getMaxStrengthB() + "). Setting to max limit."));
            punishStrengthB = CommandDglabStrength.getMaxStrengthB() - CommandDglabStrength.getCurrentStrengthB();
        }

        // 设置新的强度值
        int resultStrengthA = CommandDglabStrength.getCurrentStrengthA() + punishStrengthA;
        int resultStrengthB = CommandDglabStrength.getCurrentStrengthB() + punishStrengthB;
        CommandDglabStrength.instance.addStrength(sender, "a", punishStrengthA);
        CommandDglabStrength.instance.addStrength(sender, "b", punishStrengthB);


        //设置字体颜色
        ChatComponentText ultraMode = new ChatComponentText("[Ultra mode]");
        ultraMode.getChatStyle().setColor(EnumChatFormatting.RED);

        ChatComponentText message = new ChatComponentText("Received damage: ");
        message.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText damageComponent = new ChatComponentText(String.valueOf(damage));
        damageComponent.getChatStyle().setColor(EnumChatFormatting.BLUE);

        ChatComponentText part2 = new ChatComponentText(" HP, increasing strength to: ");
        part2.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText strengthComponent = new ChatComponentText(String.valueOf(resultStrengthA));
        strengthComponent.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

        ChatComponentText part3 = new ChatComponentText(", ");
        part3.getChatStyle().setColor(EnumChatFormatting.WHITE);

        ChatComponentText strengthComponent2 = new ChatComponentText(String.valueOf(resultStrengthB));
        strengthComponent2.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

        ultraMode.appendSibling(message);
        ultraMode.appendSibling(damageComponent);
        ultraMode.appendSibling(part2);
        ultraMode.appendSibling(strengthComponent);
        ultraMode.appendSibling(part3);
        ultraMode.appendSibling(strengthComponent2);

        sender.addChatMessage(ultraMode);


        // 在惩罚结束时恢复基础强度值
        punishEndTime = System.currentTimeMillis() + (punishTime * 1000L);
        if (ultraPunishTimer != null) {
            ultraPunishTimer.cancel(); // 取消上一个 Timer
        }
        ultraPunishTimer = new Timer();
        ultraPunishTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    CommandDglabStrength.instance.setStrength(sender, CommandDglabStrength.getBaseStrengthA(), CommandDglabStrength.getBaseStrengthB());
                }
            },
            punishTime * 1000L
        );
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        /*if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
            return;
        }*/

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "punish": {
                if (sender instanceof EntityPlayer) {
                    playerToPunish = (EntityPlayer) sender;
                } else {
                    sender.addChatMessage(new ChatComponentText("This command can only be used by a player."));
                    return;
                }

                String option = args[1].toLowerCase();

                if (option.equals("start")) {
                    if (punishActive) {
                        sender.addChatMessage(new ChatComponentText("Punish already started!"));
                    } else {
                        if (ultraPunishActive) {
                            sender.addChatMessage(new ChatComponentText(
                                "UltraPunish task is running, stopping ultraPunish task."));
                            ultraPunishActive = false;
                        }
                        punishActive = true;
                        sender.addChatMessage(new ChatComponentText("Punish started for player: " + playerToPunish.getName()));
                    }
                } else if (option.equals("stop")) {
                    if (!punishActive) {
                        sender.addChatMessage(new ChatComponentText("There's no punish task to stop!"));
                    } else {
                        punishActive = false;
                        sender.addChatMessage(new ChatComponentText("Punish stopped for player: " + playerToPunish.getName()));
                        playerToPunish = null;
                    }
                } else {
                    sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
                }
                break;
            }
            case "ultrapunish": {
                if (sender instanceof EntityPlayer) {
                    playerToPunish = (EntityPlayer) sender;
                } else {
                    sender.addChatMessage(new ChatComponentText("This command can only be used by a player."));
                    return;
                }

                String option = args[1].toLowerCase();

                if (option.equals("start")) {
                    if (ultraPunishActive) {
                        sender.addChatMessage(new ChatComponentText("UltraPunish already started!"));
                    } else {
                        if (punishActive) {
                            sender.addChatMessage(new ChatComponentText("Punish task is running, stopping punish task."));
                            punishActive = false;
                        }
                        ultraPunishActive = true;
                        sender.addChatMessage(new ChatComponentText("UltraPunish started for player: " + playerToPunish.getName()));
                    }
                } else if (option.equals("stop")) {
                    if (!ultraPunishActive) {
                        sender.addChatMessage(new ChatComponentText("There's no ultraPunish task to stop!"));
                    } else {
                        sender.addChatMessage(new ChatComponentText("UltraPunish stopped for player: " + playerToPunish.getName()));
                        playerToPunish = null;
                        ultraPunishActive = false;
                    }
                }
                break;
            }
            case "setpunishtime":
                punishTime = Integer.parseInt(args[1]);
                sender.addChatMessage(new ChatComponentText("Successfully set punishTime to: " + punishTime));
                break;
            case "setpunishrate":
                punishRate = Integer.parseInt(args[1]);
                sender.addChatMessage(new ChatComponentText("Successfully set punishRate to: " + punishRate));
                break;
            case "getpunishsetting":
                sender.addChatMessage(new ChatComponentText("Punish time: " + punishTime));
                sender.addChatMessage(new ChatComponentText("Punish rate: " + punishRate));
                break;
            default:
                sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
                break;
        }
    }


    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/dglab punish/ultraPunish <start/stop>\n" +
                "/dglab setPunishTime [seconds]\n" +
                "/dglab setPunishRate [value]\n" +
                "/dglab getPunishSetting";
    }

}
