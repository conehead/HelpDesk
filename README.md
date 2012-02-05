HelpDesk
========

This is a simple Bukkit plugin designed to allow three levels of users (Mods, Admins, and OPs) to handle help requests.

The reason for making this is apparent as soon as you get a high rank on a server with a reasonably-high population. Too many people often need help at once, and some are often left waiting indefinitely.

Setting up HelpDesk
-------------------

HelpDesk is set up in a way that it requires very little setup, and it assumes that there are three levels of staff on a server -- Mod, Admin, and OP. Each level has its own Permissions node, and those are as follows:

	helpdesk.mod
	helpdesk.admin
	helpdesk.op

It is not required for Admins to have helpdesk.mod, etc.

Usage
-----

HelpDesk operates under the /helpdesk and /ticket commands, and they operate completely identically.

**Commands for Normal Players:**

	/helpdesk ?                | displays the help menu
	/helpdesk file <message>   | creates a ticket and notifies staff
	/helpdesk read <Ticket ID> | displays the contents of a ticket they've opened

**Commands for Staff (Mod, Admin, OP):**

	/helpdesk list                 | displays a list of tickets open
	/helpdesk read <Ticket ID>     | reads the contents of any of the tickets
	/helpdesk assign <Ticket ID>   | assigns the player to the ticket
	/helpdesk elevate <Ticket ID>  | elevates the ticket level to ADMIN or OP
	/helpdesk complete <Ticket ID> | marks the ticket as complete
	/helpdesk remove <Ticket ID>   | removes a ticket
It's that simple.