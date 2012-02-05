package com.connor.helpdesk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

public class HelpDesk extends JavaPlugin {

    public Logger log = Logger.getLogger("Minecraft");
    private ArrayList<HelpTicket> tickets;
    
    private HelpCommandExecutor commandExecutor;
    
    public void onEnable() {
        tickets = new ArrayList<HelpTicket>();
        commandExecutor = new HelpCommandExecutor(this);

        getCommand("helpdesk").setExecutor(commandExecutor);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                int openTickets = 0;
                for (HelpTicket ticket : tickets) {
                    if (!ticket.isAssigned() && !ticket.isCompleted()) {
                        openTickets++;
                    }
                }
                if (openTickets > 0) {
                    if (openTickets == 1) {
                        notifyAllWithPermission(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There is " + ChatColor.DARK_GREEN + "1 ticket open");
                    } else {
                        notifyAllWithPermission(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There are " + ChatColor.DARK_GREEN + openTickets + " tickets open");
                    }
                }
            }
        }, 600, 600);

        log.info("HelpDesk enabled");
    }

    public void onDisable() {
        log.info("HelpDesk disabled");
    }

    public void addTicket(HelpTicket ticket) {
        synchronized (this) {
            if (!tickets.contains(ticket)) tickets.add(ticket);
        }
    }
    
    public HelpTicket getTicketWithID(int ID) {
        synchronized(this) {
            for (HelpTicket ticket : tickets) {
                if (ticket.getID() == ID) {
                    return ticket;
                }
            }
            return null;
        }
    }
    
    public HelpTicket getTicketWithID(String ID) {
        try {
            return getTicketWithID(Integer.parseInt(ID));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public boolean removeTicket(HelpTicket ticket) {
        synchronized (this) {
            return tickets.remove(ticket);
        }
    }
    
    public ArrayList<HelpTicket> sortTicketsByTime() {
        synchronized (this) {
            Collections.sort(tickets, new Comparator<HelpTicket>() {
                public int compare(HelpTicket o1, HelpTicket o2) {
                    if (o1.getFileTime() < o2.getFileTime()) {
                        return -1;
                    } else if (o1.getFileTime() == o2.getFileTime()) {
                        return 0; //Probably will never happen, ever.
                    } else {
                        return 1;
                    }
                }
            });
            return tickets;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        System.out.println("lawlawlawl");
        return false;
    }

    public void notifyAllWithPermission(HelpLevel level, String message) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if ((level == HelpLevel.MOD) && p.hasPermission("helpdesk.mod") || p.hasPermission("helpdesk.admin") || p.hasPermission("helpdesk.op")) {
                p.sendMessage(message);
            } else if ((level == HelpLevel.ADMIN) && p.hasPermission("helpdesk.admin") || p.hasPermission("helpdesk.op")) {
                p.sendMessage(message);
            } else if ((level == HelpLevel.OP) && p.hasPermission("helpdesk.op")) {
                p.sendMessage(message);
            }
        }
    } 
}
