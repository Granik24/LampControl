package cz.ceph.lampcontrol.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import cz.ceph.lampcontrol.LampControl;
import cz.ceph.lampcontrol.commands.core.IBasicCommand;
import cz.ceph.lampcontrol.commands.core.RegisterCommand;
import cz.ceph.lampcontrol.utils.ChatWriter;
import cz.ceph.lampcontrol.utils.SoundPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static cz.ceph.lampcontrol.LampControl.getMain;

/**
 * Created by Ceph on 10.01.2017.
 */

@RegisterCommand(value = "onlamp", alias = {"lampoff", "offlamp"})
public class OffCommand implements IBasicCommand {

    @Override
    public String getPermission() {
        return "lampcontrol.command.of";
    }

    @Override
    public String getDescription() {
        return LampControl.localization.get("command.off_lamp_description");
    }

    @Override
    public String getUsage() {
        return "/lampoff or /offlamp";
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            player.sendMessage(ChatWriter.prefix(LampControl.localization.get("error.no_worldedit")));
            return true;

        } else {
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            Selection selection = worldEdit.getSelection(player);

            if (selection == null) {
                player.sendMessage(ChatWriter.prefix(LampControl.localization.get("error.no_selection")));
                return true;
            }

            boolean checkForSelection = false;
            if (!(selection instanceof CuboidSelection)) {
                checkForSelection = true;
            }

            org.bukkit.Location min = selection.getMinimumPoint();
            org.bukkit.Location max = selection.getMaximumPoint();
            getMain().getSwitchBlock().initWorld(min.getWorld());

            int affected = 0;

            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++)
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                        Location loc = new Location(min.getWorld(), x, y, z);

                        if (!checkForSelection || selection.contains(loc)) {
                            Block block = min.getWorld().getBlockAt(loc);

                            if (block.getType().equals(Material.REDSTONE_LAMP_ON)) {
                                try {
                                    getMain().getSwitchBlock().switchLamp(block, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                affected++;
                            }
                        }
                    }
            }

            if (affected < 1) {
                player.sendMessage(ChatWriter.prefix(LampControl.localization.get("info.no_lamps_affecetd")));
                SoundPlayer.play(player.getLocation(), SoundPlayer.fail(), 0.5F, 1F);
            } else
                player.sendMessage(ChatWriter.prefix(LampControl.localization.get("info.affected_lamps_on").replace("%affected", "" + affected + "")));
            SoundPlayer.play(player.getLocation(), SoundPlayer.success(), 0.5F, 1F);
            return true;
        }
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage(ChatWriter.prefix(LampControl.localization.get("error.console_use")));
        return true;
    }
}