package com.ultikits.plugins.basic.listeners;

import com.ultikits.plugins.basic.BasicFunctions;
import com.ultikits.ultitools.UltiTools;
import com.ultikits.ultitools.annotations.EventListener;
import com.ultikits.ultitools.services.NotificationService;
import com.ultikits.ultitools.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@EventListener(manualRegister = true)
public class AtListener implements Listener {
    @Autowired
    private NotificationService notificationService;

    /*
     * at玩家功能
     * Code by Shpries, rewrite by wisdomme
     */
    private void atNotification(Player player, Player sender) {
        player.sendMessage(String.format(BasicFunctions.getInstance().i18n("§e你被玩家§b%s§e艾特了"), sender.getName()));
        UltiTools.getInstance().getVersionWrapper().sendActionBar(player, ChatColor.BOLD + String.format(BasicFunctions.getInstance().i18n("§e你被玩家§b%s§e艾特了"), sender.getName()));
        notificationService.sendBossBarNotification(player, ChatColor.BOLD + String.format(BasicFunctions.getInstance().i18n("§e你被玩家§b%s§e艾特了"), sender.getName()), 10);
    }

    @EventHandler
    public void onPlayerAt(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        Player sender = e.getPlayer();
        if (msg.contains("@")) {
            if (msg.toLowerCase().contains("@" + BasicFunctions.getInstance().i18n("所有人"))
                    || msg.toLowerCase().contains("@ " + BasicFunctions.getInstance().i18n("所有人"))) {
                if (sender.hasPermission("ultikits.tools.atall") || sender.isOp() || sender.hasPermission("ultitools.tools.admin")) {
                    String msg0 = msg.replace("@", ChatColor.DARK_GREEN + "@" + ChatColor.RESET);
                    sender.sendMessage(MessageUtils.info(BasicFunctions.getInstance().i18n("你艾特了全体玩家")));
                    e.setMessage(msg0.replace(BasicFunctions.getInstance().i18n("你艾特了全体玩家"), ChatColor.DARK_GREEN + "" + ChatColor.BOLD + BasicFunctions.getInstance().i18n("你艾特了全体玩家") + ChatColor.RESET));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        atNotification(player, sender);
                    }
                } else {
                    sender.sendMessage(MessageUtils.warning(BasicFunctions.getInstance().i18n("权限不足")));
                    e.setCancelled(true);
                }
                return;
            }
            //被@的玩家的列表
            List<Player> atedPlayer = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                //无视大小写比较
                if (msg.toLowerCase().contains("@" + player.getName().toLowerCase()) || msg.toLowerCase().contains("@ " + player.getName().toLowerCase())) {
                    atNotification(player, sender);
                    atedPlayer.add(player);
                }
            }
            if (!atedPlayer.isEmpty()) {
                //@成功
                String msg1 = msg.replace("@", ChatColor.DARK_GREEN + "@" + ChatColor.RESET);
                msg1 += " ";
                for (Player player : atedPlayer) {
                    String playerName = player.getName();
                    //校正大小写的玩家名字
                    String name = "";
                    int nameLength = playerName.length();
                    int msg1Length = msg1.length();
                    //读取到需要校正大小写的玩家名字
                    for (int i = 0; i < msg1Length - nameLength; i++) {
                        name = msg1.substring(i, i + nameLength);
                        if (name.equalsIgnoreCase(playerName)) {
                            break;
                        }
                    }
                    msg1 = msg1.replace(name, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + playerName + ChatColor.RESET);
                }
                e.setMessage(msg1);
                sender.sendMessage(String.format(BasicFunctions.getInstance().i18n("你艾特了 %d 名玩家！"), atedPlayer.size()));
            } else {
                //@不成功
                sender.sendMessage(MessageUtils.warning(BasicFunctions.getInstance().i18n("艾特失败！玩家不在线或不存在")));
                String msg2 = msg.replace("@", ChatColor.RED + "@" + ChatColor.RESET);
                e.setMessage(msg2);
            }
        }
    }
}
