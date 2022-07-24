# SignSetHome
A simple sign sethome/warping system for connecting an SMP world.

## Description

In SignSetHome every player has two signs in their name: A warp sign from their base to spawn, and a warp sign from spawn to their base.
To better understand how this system works, it helps to look at the major goals of the plugin:

1) Allow players to easily travel to spawn and to other player's bases.

Having sign warps for every player's base at spawn makes it easy to explore the server.
It encourages player's to go see other players bases and interact with more players than they may would've normally.
It can be especially useful if players build their bases thousands of blocks apart from eachother.

2) Have the system be intuitive for regular players.

To make the system as intuite as possible there is only one player command: `/sethome`.
While in their territory, players can run the `/sethome` command.
This command will initiate a series of actions.
First, a warp sign is created below their feet. This sign will warp any player to spawn.
Second, the player is teleported to spawn (warp lobby).
They are informed to "claim" a warp sign from the wall of preplaced (by the admin) SignSetHome signs.
Once they claim a sign it will teleport them back to their new home.
This command works slightly differently if the player already has a valid `/sethome`. See more information below.

3) Maintain the difficulty of vanilla survival.

In an effort to maintain the game difficulty, sign warping was chosen over command warping.
With a command warping system, players are able to teleport out deep caves, far away lands, or a hoard of mobs.
It takes the difficulty and nuisance out of survival minecraft, which quite honestly ruins the fun for a lot of players.
This is why we chose sign warping, its doesn't have this major disadvantage that makes the game easier.
It comes at a slight cost of the inconvience of having to walk back to the sign, but overall its benefits outweight the downsides in many SMP servers like ours.

## Dependencies

`GriefProtection` Required for restricting where players can make their `/sethomes`.

## Player Commands

`/sethome` Sets a players home. This will run a series of actions.
1) Run checks to make sure this is a valid location for the player to set their home. If not, the command will throw in error.
    1) Checks to make sure the player is in their GriefPrevention territory or territory they are trusted in.
    2) Checks to make sure the player is standing on a solid block, and not inside any blocks.
2) Removes the sign at the player's old set home, if it still exists.
3) Creates a sign below their feet that will warp any players to Spawn
4) Teleports the player to the Warp Lobby if they have not claimed a Warp Sign. The player is then told to right-click to claim their sign.

## Admin (OP) Commands

`/signsethome-reload` Reloads the SignSetHome config & database

`/signsethome-setspawn` Sets the Spawn Point for SignSetHome. The Spawn Point is used for the warp sign created at every player's home.

`/signsethome-setwarplobby` Sets the Warp Lobby for SignSetHome.
The Warp Lobby is used to teleport new players looking to claim a warp sign after running the `/sethome` command.
This will most likely be the same or near the Spawn Point.

`/signsethome-givewarplobbysign` Gives the admin the Warp Lobby Sign. Place these signs around the Warp Lobby location.
Player's who havn't claimed a Warp Lobby Sign yet can right click to claim them once they have set their `/sethome`.

Note: All admin commands have a shortcut `ssh` alias. Ex: `/ssh-reload`.

## Contact

Contact [liamsnow03@gmail.com](mailto:liamsnow03@gmail.com) if you have an questions!
