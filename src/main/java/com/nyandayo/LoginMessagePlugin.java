
package com.nyandayo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginMessagePlugin extends JavaPlugin implements Listener {
    // 各プレイヤーのメッセージを管理するマップ
    private Map<String, String> playerMessages = new HashMap<>();
    private String globalMessage;

    @Override
    public void onEnable() {
        // プラグインが有効になったときの処理
        getServer().getPluginManager().registerEvents(this, this);
        loadMessage();  // 設定ファイルからメッセージを読み込む
        getLogger().info("LoginMessagePlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // プラグインが無効になったときの処理
        saveMessage();  // メッセージを設定ファイルに保存する
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        // /setmessage コマンドの処理
        if (command.getName().equalsIgnoreCase("setmessage")) {
            if (args.length == 0) {
                player.sendMessage("Usage: /setmessage <message>");
                return false;
            }
            // メッセージを結合してプレイヤー専用メッセージとして保存
            String message = String.join(" ", args);
            
            // 現在の日時を取得し、フォーマット
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            // メッセージの先頭に日時を追加し、色を付ける
            String formattedMessage = ChatColor.GOLD + "[" + timeStamp + "] " + ChatColor.WHITE + message;

            // プレイヤーごとにメッセージを保存し、グローバルメッセージとしても設定
            playerMessages.put(playerName, formattedMessage);
            globalMessage = formattedMessage;  // 全プレイヤーに共通のメッセージとして設定

            player.sendMessage("Your login message has been set to: " + formattedMessage);
            saveMessage();  // メッセージを保存
            return true;
        }

        // /clearmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("clearmessage")) {
            // プレイヤーのメッセージをクリアし、デフォルトのメッセージに戻す
            playerMessages.remove(playerName);
            globalMessage = "Welcome to the server!";
            player.sendMessage("Your login message has been cleared.");
            saveMessage();  // メッセージを保存
            return true;
        }

        // /showmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("showmessage")) {
            // 現在のグローバルメッセージを表示
            player.sendMessage("The current global login message is: " + globalMessage);
            return true;
        }

        return false;
    }

    // メッセージを設定ファイルに保存
    private void saveMessage() {
        FileConfiguration config = this.getConfig();
        for (Map.Entry<String, String> entry : playerMessages.entrySet()) {
            config.set("playerMessages." + entry.getKey(), entry.getValue());
        }
        config.set("globalMessage", globalMessage);
        saveConfig();  // 設定ファイルに書き込む
    }

    // 設定ファイルからメッセージを読み込む
    private void loadMessage() {
        FileConfiguration config = this.getConfig();
        if (config.contains("globalMessage")) {
            globalMessage = config.getString("globalMessage");
        } else {
            globalMessage = "Welcome to the server!";  // デフォルトメッセージ
        }

        if (config.contains("playerMessages")) {
            for (String playerName : config.getConfigurationSection("playerMessages").getKeys(false)) {
                playerMessages.put(playerName, config.getString("playerMessages." + playerName));
            }
        }
    }
}
