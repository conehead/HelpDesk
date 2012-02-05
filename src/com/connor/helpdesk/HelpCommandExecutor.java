package com.connor.helpdesk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HelpCommandExecutor implements CommandExecutor {

    private HelpDesk helpDeskInstance;
    
    public HelpCommandExecutor(HelpDesk helpDesk) {
        this.helpDeskInstance = helpDesk;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (args.length < 1 || args[0].equals("?") || args[0].equalsIgnoreCase("help")) {
            displayManual(player);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("file") || args[0].equalsIgnoreCase("create")) {
            return createTicket(player, args);
        }
        
        if (args[0].equalsIgnoreCase("list")) {
            return listTickets(player);
        }
        
        if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("read")) {
            return readTicket(player, args);
        }
        
        if (args[0].equalsIgnoreCase("assign")) {
            return assignTicket(player, args);
        }

        if (args[0].equalsIgnoreCase("elevate")) {
            return elevateTicket(player, args);
        }

        if (args[0].equalsIgnoreCase("remove")) {
            return removeTicket(player, args);
        }
        
        if (args[0].equalsIgnoreCase("complete")) {
            return completeTicket(player, args);
        }

        return false;
    }

    public boolean displayManual(Player player) {
        player.sendMessage(ChatColor.GRAY + "/ " + ChatColor.GOLD + "HelpDesk");
        player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "file <message>" + ChatColor.GRAY
                            + ": Files a help ticket");
        
        if (player.hasPermission("helpdesk.mod") || player.hasPermission("helpdesk.admin") || player.hasPermission("helpdesk.op")) {
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "list" + ChatColor.GRAY
                    + ": Lists currently-open tickets");
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "read <ID>" + ChatColor.GRAY
                    + ": Reads the contents of a help ticket");
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "assign <ID>" + ChatColor.GRAY
                    + ": Assigns you to the ticket");
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "elevate <ID>" + ChatColor.GRAY
                    + ": Elevates the ticket level to ADMIN or OP");
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "complete <ID>" + ChatColor.GRAY
                    + ": Marks the ticket as complete");
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "remove <ID>" + ChatColor.GRAY
                    + ": Removes the ticket");
        } else {
            player.sendMessage(ChatColor.GRAY + "| " + ChatColor.YELLOW + "read <ID>" + ChatColor.GRAY
                    + ": Reads a help ticket");
        }
        return false;
    }
    
    public boolean createTicket(Player player, String[] args) {
        if (args.length < 2)
            return false;
        
        StringBuilder contents = new StringBuilder().append(args[1]);
        for (int i = 2; i < args.length; i++) {
            contents.append(" " + args[i]);
        }
        
        HelpTicket ticket = new HelpTicket(player.getName(), contents.toString());
        helpDeskInstance.addTicket(ticket);
        
        helpDeskInstance.notifyAllWithPermission(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "Ticket " + ChatColor.DARK_GREEN +  ticket.getID() + ChatColor.GRAY + " has been " + ChatColor.DARK_GREEN + "submitted by " + ticket.getUserFiled());
        player.sendMessage(ChatColor.GRAY + "Ticket submitted. Your ticket ID is " + ChatColor.DARK_GREEN + ticket.getID());
        
        return true;
    }
    
    public boolean readTicket(Player player, String[] args) {
        if (args.length < 2) 
            return true;

        HelpTicket ticket = helpDeskInstance.getTicketWithID(args[1]);
        
        if (ticket == null) {
            player.sendMessage(ChatColor.GRAY + "Invalid ticket ID");
            return true;
        }
        
        if (!(ticket.getUserFiled().equalsIgnoreCase(player.getName())) && !player.hasPermission("helpdesk.mod") && !player.hasPermission("helpdesk.admin") && !player.hasPermission("helpdesk.op"))
            return true;
        
        if (ticket.isAssigned()) {
            player.sendMessage(ChatColor.GRAY + "Ticket assigned to " + ticket.getAssignedUser() + ":");
        }
        player.sendMessage(ChatColor.DARK_GREEN + "Ticket " + ticket.getID() + ChatColor.GRAY + " (" + ticket.getUserFiled() + "): "
                + ChatColor.WHITE + ticket.getContents());
        
        return true;
    }
    
    public boolean assignTicket(Player player, String[] args) {
        if (args.length < 2)
            return false;

        if (!player.hasPermission("helpdesk.mod") && !player.hasPermission("helpdesk.admin") && !player.hasPermission("helpdesk.op"))
            return true;
        
        HelpTicket ticket = helpDeskInstance.getTicketWithID(args[1]);

        if (ticket == null) {
            player.sendMessage(ChatColor.GRAY + "Invalid ticket ID");
            return true;
        }
        
        if (ticket.isAssigned()) {
            player.sendMessage(ChatColor.GRAY + "Ticket already assigned to " + ticket.getAssignedUser());
            return true;
        }
        
        ticket.setAssignedUser(player.getName());
        helpDeskInstance.notifyAllWithPermission(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "Ticket " + ChatColor.DARK_GREEN + ticket.getID() + ChatColor.GRAY + " was " + ChatColor.DARK_GREEN + "assigned to " + ticket.getAssignedUser());
        player.sendMessage(ChatColor.GRAY + "You have been assigned to Ticket " + ticket.getID());
        
        Player filed = Bukkit.getServer().getPlayerExact(ticket.getUserFiled());
        if (filed != null) 
            filed.sendMessage(ChatColor.GRAY + "Your ticket (" + ChatColor.DARK_GREEN + ticket.getID()
                    + ChatColor.GRAY + ")" + " has been " + ChatColor.DARK_GREEN + "assigned to " + ticket.getAssignedUser());
        return true;
    }

    private boolean elevateTicket(Player player, String[] args) {
        if (args.length < 2)
            return false;

        if (!player.hasPermission("helpdesk.mod") && !player.hasPermission("helpdesk.admin") && !player.hasPermission("helpdesk.op"))
            return true;
        
        HelpTicket ticket = helpDeskInstance.getTicketWithID(args[1]);

        if (ticket == null) {
            player.sendMessage(ChatColor.GRAY + "Invalid ticket ID");
            return true;
        }
        
        HelpLevel level = ticket.getLevel();
        ticket.elevate(player);
        if (level != ticket.getLevel()) {
            player.sendMessage(ChatColor.GRAY + "Ticket elevated to " + ticket.getLevel());
            helpDeskInstance.notifyAllWithPermission(HelpLevel.MOD, ChatColor.GOLD + "[HELPDESK] " + ChatColor.GRAY + "Ticket " + ChatColor.DARK_GREEN + ticket.getID() + ChatColor.GRAY + " was " + ChatColor.DARK_GREEN + "elevated to " + ticket.getLevel());
            Player filed = Bukkit.getServer().getPlayerExact(ticket.getUserFiled());
            if (filed != null)
                filed.sendMessage(ChatColor.GRAY + "Your ticket (" + ChatColor.DARK_GREEN + ticket.getID() + ChatColor.GRAY + ") was " + ChatColor.DARK_GREEN + "elevated to " + ticket.getLevel()
                        + " by " + player.getName());
        } else {
            player.sendMessage(ChatColor.GRAY + "Ticket couldn't be elevated");
        }
        
        return true;
    }

    private boolean removeTicket(Player player, String[] args) {
        if (args.length < 2)
            return false;

        if (!player.hasPermission("helpdesk.mod") && !player.hasPermission("helpdesk.admin") && !player.hasPermission("helpdesk.op"))
            return true;

        HelpTicket ticket = helpDeskInstance.getTicketWithID(args[1]);

        if (ticket == null) {
            player.sendMessage(ChatColor.GRAY + "Invalid ticket ID");
            return true;
        }

        if (helpDeskInstance.removeTicket(ticket)) {
            player.sendMessage(ChatColor.GRAY + "Removed Ticket " + ticket.getID());
            Player filed = Bukkit.getServer().getPlayerExact(ticket.getUserFiled());
            if (filed != null)
                filed.sendMessage(ChatColor.GRAY + "Your ticket (" + ChatColor.DARK_GREEN + ticket.getID() + ChatColor.GRAY + ") was "
                        + ChatColor.RED + "removed by " + player.getName());
        }

        return true;
    }

    private boolean completeTicket(Player player, String[] args) {
        if (args.length < 2)
            return true;

        if (!player.hasPermission("helpdesk.mod") && !player.hasPermission("helpdesk.admin") && !player.hasPermission("helpdesk.op"))
            return true;

        HelpTicket ticket = helpDeskInstance.getTicketWithID(args[1]);

        if (ticket == null) {
            player.sendMessage(ChatColor.GRAY + "Invalid ticket ID");
            return true;
        }

        ticket.setCompleted();
        if (helpDeskInstance.removeTicket(ticket)) {
            player.sendMessage(ChatColor.GRAY + "Marked Ticket " + ticket.getID() + " as complete");
            Player filed = Bukkit.getServer().getPlayerExact(ticket.getUserFiled());
            if (filed != null)
                filed.sendMessage(ChatColor.GRAY + "Your ticket (" + ChatColor.DARK_GREEN + ticket.getID() + ChatColor.GRAY +  ") was "
                        + ChatColor.DARK_GREEN + "marked as complete by " + player.getName());
        }

        return true;
    }

    private boolean listTickets(Player player) {
        if (!player.hasPermission("helpdesk.mod")
                && !player.hasPermission("helpdesk.admin")
                && !player.hasPermission("helpdesk.op"))
            return true;
        
        ArrayList<HelpTicket> tickets = helpDeskInstance.sortTicketsByTime();
        
        if (player.hasPermission("helpdesk.admin")) {
            Collections.sort(tickets, new Comparator<HelpTicket>() {
                public int compare(HelpTicket o1, HelpTicket o2) {
                    if (o1.getLevel() == HelpLevel.ADMIN && o2.getLevel() != HelpLevel.ADMIN) {
                        return -1;
                    } else if (o2.getLevel() == HelpLevel.ADMIN && o1.getLevel() != HelpLevel.ADMIN) {
                        return 1;
                    } else if (o1.getLevel() == HelpLevel.ADMIN && o2.getLevel() == HelpLevel.ADMIN) {
                        if (o1.getID() < o2.getID()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    return 0;
                }
            });
        }
        if (player.hasPermission("helpdesk.op")) {
            Collections.sort(tickets, new Comparator<HelpTicket>() {
                public int compare(HelpTicket o1, HelpTicket o2) {
                    if (o1.getLevel() == HelpLevel.OP && o2.getLevel() != HelpLevel.OP) {
                        return -1;
                    } else if (o2.getLevel() == HelpLevel.OP && o1.getLevel() != HelpLevel.OP) {
                        return 1;
                    } else if (o1.getLevel() == HelpLevel.OP && o2.getLevel() == HelpLevel.OP) {
                        if (o1.getID() < o2.getID()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    return 0;
                }
            });
        }
        
        player.sendMessage(ChatColor.GRAY + "/ Filed Tickets");
        for (int i = 0; i < 8; i++) {
            if (tickets.size() <= i)
                break;
            
            if (tickets.get(i).getLevel() == HelpLevel.ADMIN && !player.hasPermission("helpdesk.admin"))
                continue;
            else if (tickets.get(i).getLevel() == HelpLevel.OP && !player.hasPermission("helpdesk.op"))
                continue;
            
            if (tickets.get(i).isAssigned())
                continue;
            
            player.sendMessage(ChatColor.GRAY + "| "
                    + ChatColor.GOLD + "[" + tickets.get(i).getLevel() + "]"
                    + ChatColor.DARK_GREEN + "Ticket " + tickets.get(i).getID() + ChatColor.GRAY + " by "
                    + ChatColor.DARK_GREEN + tickets.get(i).getUserFiled() + ChatColor.WHITE + ": " + tickets.get(i).getContents());
        }
        return true;
    }
}
