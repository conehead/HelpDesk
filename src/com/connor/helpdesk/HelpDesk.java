package com.connor.helpdesk;

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
                int modTickets = 0;
                int adminTickets = 0;
                int opTickets = 0;
                for (HelpTicket ticket : tickets) {
                    if (!ticket.isAssigned() && !ticket.isCompleted()) {
                        if (ticket.getLevel() == HelpLevel.MOD) {
                            modTickets++;
                            adminTickets++;
                            opTickets++;
                        } else if (ticket.getLevel() == HelpLevel.ADMIN) {
                            adminTickets++;
                            opTickets++;
                        } else if (ticket.getLevel() == HelpLevel.OP) {
                            opTickets++;
                        }
                    }
                }
                
                if (modTickets > 0) {
                    if (modTickets == 1) {
                        sendMessageToStaffLevel(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There is " + ChatColor.DARK_GREEN + "1 ticket open");
                    } else {
                        sendMessageToStaffLevel(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There are " + ChatColor.DARK_GREEN + modTickets + " tickets open");
                    }
                }
                if (adminTickets > 0) {
                    if (modTickets == 1) {
                        sendMessageToStaffLevel(HelpLevel.ADMIN, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There is " + ChatColor.DARK_GREEN + "1 ticket open");
                    } else {
                        sendMessageToStaffLevel(HelpLevel.ADMIN, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There are " + ChatColor.DARK_GREEN + adminTickets + " tickets open");
                    }
                }
                if (opTickets > 0) {
                    if (opTickets == 1) {
                        sendMessageToStaffLevel(HelpLevel.OP, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There is " + ChatColor.DARK_GREEN + "1 ticket open");
                    } else {
                        sendMessageToStaffLevel(HelpLevel.OP, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "There are " + ChatColor.DARK_GREEN + opTickets + " tickets open");
                    }
                }
            }
        }, 600, 600);

        log.info("HelpDesk enabled");
    }

    public void onDisable() {
        getServer().broadcastMessage(ChatColor.GOLD + "[HELPDESK] " + ChatColor.RED + "Tickets cleared");
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

    public void sendMessageToStaffLevel(HelpLevel level, String message) {
        for (Player p : getServer().getOnlinePlayers()) {
            if (level == HelpLevel.MOD) {
                if (p.hasPermission("helpdesk.mod")) {
                    p.sendMessage(message);
                }
            } else if (level == HelpLevel.ADMIN) {
                if (p.hasPermission("helpdesk.admin")) {
                    p.sendMessage(message);
                }
            } else if (level == HelpLevel.OP) {
                if (p.hasPermission("helpdesk.op")) {
                    p.sendMessage(message);
                }
            }
        }
    }

    public void notifyAllHelpdeskStaff(String message) {
        for (Player p : getServer().getOnlinePlayers()) {
            if (isHelpdeskStaff(p)) {
                p.sendMessage(message);
            }
        }
    } 
    
    public boolean isHelpdeskStaff(Player player) {
        return player.hasPermission("helpdesk.mod") || player.hasPermission("helpdesk.admin") || player.hasPermission("helpdesk.op");
    }
}
