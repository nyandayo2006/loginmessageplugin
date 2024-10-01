
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

public class LoginMessagePlugin extends JavaPlugin implements Listener {
    // 全プレイヤーが共通で使用するログインメッセージ
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
        // /setmessage コマンドの処理
        if (command.getName().equalsIgnoreCase("setmessage")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /setmessage <message>");
                return false;
            }
            // メッセージを結合してグローバルメッセージとして保存
            String message = String.join(" ", args);

            // 現在の日時を取得し、フォーマット
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // メッセージの先頭に日時を追加し、日時部分に色を付ける
            globalMessage = ChatColor.GOLD + "[" + timeStamp + "] " + ChatColor.WHITE + message;

            sender.sendMessage("The global login message has been set to: " + globalMessage);
            saveMessage();  // メッセージを保存
            return true;
        }

        // /clearmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("clearmessage")) {
            // グローバルメッセージをデフォルトに戻す
            globalMessage = "Welcome to the server!";
            sender.sendMessage("The global login message has been cleared.");
            saveMessage();  // メッセージを保存
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

    // メッセージを設定ファイルに保存
    private void saveMessage() {
        FileConfiguration config = this.getConfig();
        config.set("loginMessage", globalMessage);
        saveConfig();  // 設定ファイルに書き込む
    }

    // 設定ファイルからメッセージを読み込む
    private void loadMessage() {
        FileConfiguration config = this.getConfig();
        // 設定ファイルにメッセージが保存されていれば、それを読み込む
        if (config.contains("loginMessage")) {
            globalMessage = config.getString("loginMessage");
        } else {
            globalMessage = "Welcome to the server!";  // デフォルトメッセージ
        }
    }
}
