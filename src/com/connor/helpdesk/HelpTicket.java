package com.connor.helpdesk;

import org.bukkit.entity.Player;

public class HelpTicket {
    
    private static int currentID = 1;
    
    private String userFiled;
    private int ID;
    private long fileTime;
    private HelpLevel level;
    private String contents;
    private boolean assigned = false;
    private String assignedUser = null;
    private boolean completed = false;
    private boolean urgent = false;
    
    public HelpTicket(String userFiled, String contents) {
        this.userFiled = userFiled;
        this.contents = contents;
        this.ID = currentID++;
        this.fileTime = System.currentTimeMillis();
        this.level = HelpLevel.MOD;
    }

    public String getUserFiled() {
        return userFiled;
    }

    public int getID() {
        return ID;
    }

    public long getFileTime() {
        return fileTime;
    }

    public HelpLevel getLevel() {
        return level;
    }

    public void setLevel(HelpLevel level) {
        this.level = level;
    }

    public String getContents() {
        return contents;
    }
    
    public boolean isAssigned() {
        return assigned;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        assigned = true;
        this.assignedUser = assignedUser;
    }
    
    public void elevate(Player player) {
        if (level == HelpLevel.MOD && player.hasPermission("helpdesk.mod")) {
            level = HelpLevel.ADMIN;
        } else if (level == HelpLevel.ADMIN && player.hasPermission("helpdesk.admin")) {
            level = HelpLevel.OP;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        completed = true;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
