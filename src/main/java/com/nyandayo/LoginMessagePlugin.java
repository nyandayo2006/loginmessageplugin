
package com.nyandayo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginMessagePlugin extends JavaPlugin implements Listener {
    // 全プレイヤーが共通で使用するログインメッセージ
    private String globalMessage = "Welcome to the server!";  // デフォルトメッセージ

    @Override
    public void onEnable() {
        // プラグインが有効になったときの処理
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LoginMessagePlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // プラグインが無効になったときの処理
        getLogger().info("LoginMessagePlugin has been disabled.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // 全プレイヤーに共通のメッセージを表示
        player.sendMessage(globalMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /setmessage コマンドの処理
        if (command.getName().equalsIgnoreCase("setmessage")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /setmessage <message>");
                return false;
            }
            // メッセージを結合してグローバルメッセージとして保存
            String message = String.join(" ", args);
            globalMessage = message;
            sender.sendMessage("The global login message has been set to: " + message);
            return true;
        }

        // /clearmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("clearmessage")) {
            // グローバルメッセージをデフォルトに戻す
            globalMessage = "Welcome to the server!";
            sender.sendMessage("The global login message has been cleared.");
            return true;
        }

        // /showmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("showmessage")) {
            // 現在のグローバルメッセージを表示
            sender.sendMessage("The current global login message is: " + globalMessage);
            return true;
        }

        return false;
    }
}
