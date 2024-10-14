## What does it do?
This mod adds the ability to run **Lua scripts** in **Minecraft (BTA)**. It introduces commands to manipulate Lua sandboxes, allowing you to execute code, files, and functions that have been declared within the created sandbox.

In addition to providing direct access to Minecraft, this mod includes a series of utility classes for manipulating the game from Lua. It also adds **signals** that can be connected and triggered during their respective events.

## :warning:  Warning
This mod is **unstable** and has **many security vulnerabilities**. While the Lua environment is controlled in a sandbox without the OS, IO, or LUAJAVA libraries (which provide access to the JVM), it remains risky. I recommend **not running unknown scripts**. If you choose to proceed, at least **review them** thoroughly and execute them **at your own risk**.

## :warning:  Multiplayer Warning:
This mod **does not support multiplayer yet**. However, this does not mean it poses no threat to servers, as it could theoretically allow packets to be sent from Lua to the server.

## :moyai:  Disclaimer
I am not responsible for any damages caused by this mod. I created it out of passion, without malicious intent. However, some may seek to exploit this. I will do my best to improve security without compromising the freedom this mod offers.
