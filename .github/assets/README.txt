-= BedrockConnect =-
Developed by Pugmatt

Full video tutorial on how to set this up can be found on my channel: https://www.youtube.com/user/Pugmatt

Requires Java 8 or higher to run the JAR.

This setup requires a program called mod0Umleitung, which can be found here: https://modzero.github.io/mod0Umleitung/

-------------------

Execute run.bat. Once you choose the IP you want on the DNS records, the software will create a bc_dns.conf file in the parent directory.
Start up mod0Umleitung, and load bc_dns.conf as a ruleset, which will load the needed DNS records.

If you receive an error about "Only one usage of each socket address", you need to disable the Windows service "DNS Client", which is hogging port 53.

To disable "DNS Client", run the following command in command prompt as admin to set the needed registry: 
REG add "HKLM\SYSTEM\CurrentControlSet\services\Dnscache" /v Start /t REG_DWORD /d 4 /f

If you want to revert this change later, run this which will enable it again: 
REG add "HKLM\SYSTEM\CurrentControlSet\services\Dnscache" /v Start /t REG_DWORD /d 2 /f

(Both of these commands will require a restart)

You should then be able to set the DNS on your game console, and connect to the BedrockConnect serverlist through the Featured Server list.

-------------------

Although not required, if you like the software feel free to throw a few bucks to support what I do! http://paypal.me/Pugmatt