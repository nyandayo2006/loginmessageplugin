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

    @Override
    public void onEnable() {
        // プラグインが有効になったときの処理
        getServer().getPluginManager().registerEvents(this, this);
        loadMessages();  // 設定ファイルからメッセージを読み込む
        getLogger().info("LoginMessagePlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // プラグインが無効になったときの処理
        saveMessages();  // メッセージを設定ファイルに保存する
        getLogger().info("LoginMessagePlugin has been disabled.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // すべての登録されたメッセージを表示
        if (!playerMessages.isEmpty()) {
            for (String message : playerMessages.values()) {
                player.sendMessage(message);
            }
        } else {
            player.sendMessage("いらっしゃい～");  // デフォルトメッセージ
        }
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

            // メッセージの先頭に日時を追加し、末尾に「by [プレイヤー名]」を追加、色を変える
            String formattedMessage = ChatColor.GOLD + "[" + timeStamp + "] " + ChatColor.WHITE + message + 
                                      ChatColor.AQUA + " by " + playerName;

            // プレイヤーごとにメッセージを保存
            playerMessages.put(playerName, formattedMessage);

            player.sendMessage("伝言を登録しました。: " + formattedMessage);
            saveMessages();  // メッセージを保存
            return true;
        }

        // /clearmessage コマンドの処理
        // else if (command.getName().equalsIgnoreCase("clearmessage")) {
        //     // プレイヤーのメッセージをクリア
        //     playerMessages.remove(playerName);
        //     player.sendMessage("伝言をクリアしました。");
        //     saveMessages();  // メッセージを保存
        //     return true;
        // }
        // /clearmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("clearmessage")) {
            // プレイヤーのメッセージをクリア
            playerMessages.remove(playerName);

            // 設定ファイルからも削除
            FileConfiguration config = this.getConfig();
            config.set("playerMessages." + playerName, null); // メッセージを削除
            saveConfig(); // 設定ファイルを保存

            player.sendMessage("伝言をクリアしました。");
            saveMessages();  // メッセージを保存
            return true;
        }


        // /showmessage コマンドの処理
        else if (command.getName().equalsIgnoreCase("showmessage")) {
            if (!playerMessages.isEmpty()) {
                player.sendMessage("伝言メッセージ一覧:");
                for (String message : playerMessages.values()) {
                    player.sendMessage(message);
                }
            } else {
                player.sendMessage("伝言メッセージはありません。");
            }
            return true;
        }

        return false;
    }

    // メッセージを設定ファイルに保存
    private void saveMessages() {
        FileConfiguration config = this.getConfig();
        for (Map.Entry<String, String> entry : playerMessages.entrySet()) {
            config.set("playerMessages." + entry.getKey(), entry.getValue());
        }
        saveConfig();  // 設定ファイルに書き込む
    }

    // 設定ファイルからメッセージを読み込む
    private void loadMessages() {
        FileConfiguration config = this.getConfig();
        if (config.contains("playerMessages")) {
            for (String playerName : config.getConfigurationSection("playerMessages").getKeys(false)) {
                playerMessages.put(playerName, config.getString("playerMessages." + playerName));
            }
        }
    }
}
