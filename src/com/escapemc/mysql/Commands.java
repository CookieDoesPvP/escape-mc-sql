package com.escapemc.mysql;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private static final String PERMISSION_ERROR = ChatColor.RED + "You are not allowed to do this.";
    private static final String USAGE = ChatColor.RED + "Usage: /mysql <connect | disconnect | reconnect>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("mysql")) {
            return false;
        }

        // ✅ Permission check (only required for Players)
        if (sender instanceof Player && !sender.hasPermission("mysql.admin")) {
            sender.sendMessage(PERMISSION_ERROR);
            return true;
        }

        // ✅ Handle missing or incorrect arguments
        if (args.length != 1) {
            sender.sendMessage(USAGE);
            return true;
        }

        // ✅ Convert to lowercase for case-insensitive matching
        switch (args[0].toLowerCase()) {
            case "connect":
                sender.sendMessage(ChatColor.GREEN + "✅ MySQL is connecting... Check console for details.");
                DatabaseManager.initialize(SQLInfo.getHost(), SQLInfo.getUser(), SQLInfo.getPassword(), SQLInfo.getDatabase(), SQLInfo.getPort());
                break;
            case "disconnect":
                sender.sendMessage(ChatColor.GREEN + "✅ MySQL is disconnecting... Check console for details.");
                DatabaseManager.close();
                break;
            case "reconnect":
                sender.sendMessage(ChatColor.GREEN + "✅no no no allow my fren");
               // MySQL.reconnect();
                break;
            default:
                sender.sendMessage(USAGE);
                break;
        }
        return true;
    }
}
