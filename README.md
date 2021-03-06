HelpDesk
========

This is a simple Bukkit plugin designed to allow three levels of users (Mods, Admins, and OPs) to handle help requests.

The reason for making this is apparent as soon as you get an elevated rank on a server with a reasonably-high population. Too many people often need help at once, and some are often left waiting indefinitely if you're busy and forget about their request.

Setting up HelpDesk
-------------------

HelpDesk operates in a way that it requires very little setup, and it assumes that there are three levels of staff on a server -- Mod, Admin, and OP. Each level has its own Permissions node, and those are as follows:

	helpdesk.mod
	helpdesk.admin
	helpdesk.op

`helpdesk.op` is automatically assigned to OPs on the server. It is not required for Admins to have helpdesk.mod, etc.

Usage
-----

HelpDesk operates under the `/helpdesk`, `/ticket`, and `/hd` commands, and they operate completely identically.

Each help ticket added to HelpDesk has a "handler level." For example, if a Mod doesn't have the ability to handle a given ticket, they can use `/helpdesk elevate <ID>` to remove the ticket from their queue, and mark the ticket as something that an Admin has to handle. (Admins can subsequently do the same command to elevate it to OP)

Each HelpTicket also has an urgency flag. If at any time the person filing the ticket or a staff member thinks a ticket is urgent, they can use `/helpdesk urgent <ID>` to flag it as such. If the person filing the ticket or a staff member doesn't think it's urgent, they can use `/helpdesk noturgent <ID>` to change its priority to normal.

If a staff member wants to accept responsibility for a ticket, they can use `/helpdesk assign <ID>` and it'll be removed from the active queue, assigned to them. **They will be godded until they complete or remove the ticket**

If a staff member decides they want to pass responsibility of a ticket to another staff member, they can use `/helpdesk pass <ID> <user>`.

When a help ticket has been filled, a staff member can use `/helpdesk complete <ID>` to mark the ticket as completed. If the ticket should be removed without completion, the staff member should use `/helpdesk remove <ID>`.

HelpDesk will automatically notify all appropriate staff members every thirty seconds that there are any given number of tickets open.

**Commands for Normal Players:**

	/helpdesk ?                     | displays the help menu
	/helpdesk file/create <message> | creates a ticket and notifies staff
	/helpdesk read <Ticket ID>      | displays the contents of a ticket they've opened
	/helpdesk urgent <Ticket ID>    | marks a ticket as urgent
	/helpdesk noturgent <Ticket ID> | marks a ticket as normal

**Commands for Staff (Mod, Admin, OP):**

	/helpdesk list                    | displays a list of tickets open
	/helpdesk read <Ticket ID>        | reads the contents of any of the tickets
	/helpdesk assign <Ticket ID>      | assigns the player to the ticket
	/helpdesk pass <Ticket ID> <user> | passes the ticket to a different staff member
	/helpdesk elevate <Ticket ID>     | elevates the ticket level to ADMIN or OP
	/helpdesk complete <Ticket ID>    | marks the ticket as complete
	/helpdesk remove <Ticket ID>      | removes a ticket
	/helpdesk urgent <Ticket ID>      | marks a ticket as urgent
	/helpdesk noturgent <Ticket ID>   | marks a ticket as normal
It's that simple.