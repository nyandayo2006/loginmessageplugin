
package com.nyandayo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginMessagePlugin extends JavaPlugin implements Listener {
    private String globalMessage = "Welcome to the server!";  // デフォルトのメッセージ

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
        player.sendMessage(globalMessage);  // すべてのプレイヤーに共通のメッセージを送信
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setmessage")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /setmessage <message>");
                return false;
            }
            // コマンド送信者が管理者かどうかの確認は、必要に応じて追加
            if (sender.hasPermission("loginmessage.set")) {  // 管理者権限チェック（オプション）
                String message = String.join(" ", args);
                globalMessage = message;  // グローバルメッセージを更新
                sender.sendMessage("Global login message has been set to: " + message);
                return true;
            } else {
                sender.sendMessage("You do not have permission to set the login message.");
                return false;
            }
        }
        return false;
    }
}
