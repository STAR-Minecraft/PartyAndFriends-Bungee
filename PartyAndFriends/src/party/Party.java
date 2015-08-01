package party;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import freunde.friends;
import freunde.joinEvent;
import freunde.kommandos.msg;
import mySql.mySql;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import party.command.P;
import party.command.PartyCommand;
import party.listener.PlayerDisconnectListener;
import party.listener.ServerSwitshListener;

public class Party extends Plugin implements Listener {

	public static String prefix = "�7[�5Party�7] ";
	private static Party instance;
	private String host;
	private int port;
	private String username;
	private String passwort;
	private String database;
	public static mySql verbindung;
	private boolean updateNotification;
	private String friendsAliasMsg;
	private String PartyAlias;
	private String friendAlias;
	private String joinAlias;
	private String inviteAlias;
	private String kickAlias;
	private String infoAlias;
	private String leaveAlias;
	private String chatAlias;
	private String leaderAlias;
	private String acceptAlias;
	private String addAlias;
	private String denyAlias;
	private String settingsAlias;
	private String jumpAlias;
	private String listAlias;
	private String removeAlias;
	private String language;
	private String friendPermission;
	private String partyPermission;
	private String partyChatShortAlias;
	private Boolean disableP;
	private int MaxPlayersInParty;
	private boolean disableMsg;
	private friends freunde;
	private PartyCommand KommandoParty;
	private String NoPlayerLimitForPartysPermission;

	@Override
	public void onDisable() {
		verbindung.close();
		if (language.equalsIgnoreCase("english")) {
			System.out.println(prefix + "PartyAndFriends was disabled!");
		} else {
			System.out.println(prefix + "PartyAndFriends wurde deaktiviert!");
		}
	}

	@Override
	public void onEnable() {
		instance = this;
		try {
			ladeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		verbindung = new mySql();
		verbindung.setDaten(host, username, passwort, port, database);
		try {
			verbindung.verbinde();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		try {
			verbindung.datenbankImportieren();
		} catch (SQLException e1) {
			if (language.equalsIgnoreCase("english")) {
				System.out.println(prefix + "The database couldn�t be imported.");
			} else {
				System.out.println(prefix + "Die Datenbank konnte nicht importiert werden.");
			}
			e1.printStackTrace();
		}
		KommandoParty = new PartyCommand(verbindung, partyPermission, PartyAlias, joinAlias, inviteAlias, kickAlias,
				infoAlias, leaveAlias, chatAlias, leaderAlias, language, MaxPlayersInParty,
				NoPlayerLimitForPartysPermission, this);
		BungeeCord.getInstance().getPluginManager().registerCommand(this, KommandoParty);
		if (disableP == false) {
			BungeeCord.getInstance().getPluginManager().registerCommand(this,
					new P(partyChatShortAlias, language, partyPermission));
		}
		BungeeCord.getInstance().getPluginManager().registerListener(this,
				new PlayerDisconnectListener(verbindung, language));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new ServerSwitshListener(language));
		freunde = new friends(verbindung, friendPermission, friendAlias, friendsAliasMsg, acceptAlias, addAlias,
				denyAlias, settingsAlias, jumpAlias, listAlias, removeAlias, language);
		getProxy().getPluginManager().registerCommand(this, freunde);
		BungeeCord.getInstance().getPluginManager().registerListener(this, new joinEvent(verbindung, language));
		if (disableMsg == false) {
			getProxy().getPluginManager().registerCommand(this, new msg(verbindung, friendsAliasMsg, language));
		}
		String localVersion = getDescription().getVersion();
		if (updateNotification) {
			try {
				HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
						.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				con.getOutputStream()
						.write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=10123")
								.getBytes("UTF-8"));
				String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
				if (localVersion.equalsIgnoreCase(version)) {

				} else {
					if (language.equalsIgnoreCase("english")) {
						System.out.println(prefix + "For the plugin PartyAndFriends is an update available");
					} else {
						System.out.println(prefix + "F�r das Plugin PartyAndFriends ist ein Update verf�gbar");
					}
				}
				if (language.equalsIgnoreCase("english")) {
					System.out.println(
							prefix + "Simonsators PartyAndFriends v." + localVersion + " was enabled successfully");
				} else {
					System.out.println(
							prefix + "Simonsators PartyAndFriends v." + localVersion + " wurde erfolgreich aktiviert");
				}
			} catch (IOException e) {
				if (language.equalsIgnoreCase("english")) {
					System.out.println(prefix + "It occurred an error while searching for updates");
				} else {
					System.out.println(prefix + "Es ist ein Fehler beim suchen nach updates aufgetreten");
				}
				e.printStackTrace();
			}
		} else {
			if (language.equalsIgnoreCase("english")) {
				System.out.println(
						prefix + "Simonsators PartyAndFriends v." + localVersion + " was enabled successfully");
				System.out.println(prefix + "Update Notification is disabled");
			} else {
				System.out.println(
						prefix + "Simonsators PartyAndFriends v." + localVersion + " wurde erfolgreich aktiviert");
				System.out.println(prefix + "Update Notification ist deaktiviert");
			}
		}
		if (language.equalsIgnoreCase("english")) {
			System.out.println(prefix + "PartyAndFriends was enabled successfully!");
		} else {
			System.out.println(prefix + "PartyAndFriends wurde aktiviert!");
		}
	}

	public static Party getInstance() {
		return instance;
	}

	public void ladeConfig() throws IOException {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder().getPath(), "config.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		host = config.getString("MySQL.Host");
		if (host.equals("")) {
			config.set("MySQL.Host", "localhost");
		}
		port = config.getInt("MySQL.Port");
		if (port == 0) {
			config.set("MySQL.Port", 3306);
		}
		username = config.getString("MySQL.Username");
		if (username.equals("")) {
			config.set("MySQL.Username", "root");
		}
		passwort = config.getString("MySQL.Password");
		if (passwort.equals("")) {
			config.set("MySQL.Password", "Password");
		}
		database = config.getString("MySQL.Database");
		if (database.equals("")) {
			config.set("MySQL.Database", "freunde");
		}
		language = config.getString("General.Language");
		if (language.equals("")) {
			config.set("General.Language", "english");
		}
		String ownLanguage = config.getString("General.UseOwnLanguageFile");
		if (ownLanguage.equals("")) {
			config.set("General.UseOwnLanguageFile", "false");
		}
		if (config.getString("General.UseOwnLanguageFile").equalsIgnoreCase("true")) {
			language = "own";
			ladeMessageYML();
		}
		String updateNotificationS = config.getString("General.UpdateNotification");
		if (updateNotificationS.equals("")) {
			config.set("General.UpdateNotification", "true");
		}
		updateNotification = config.getString("General.UpdateNotification").equalsIgnoreCase("true");
		String version = config.getString("General.Version");
		if (!version.equals(getDescription().getVersion())) {
			config.set("General.Version", getDescription().getVersion());
		}
		String disablePs = config.getString("General.DisableCommandP");
		if (disablePs.equals("")) {
			config.set("General.DisableCommandP", "false");
		}
		disableP = config.getString("General.DisableCommandP").equals("true");
		String disableMsgs = config.getString("General.disableMsg");
		if (disableMsgs.equals("")) {
			config.set("General.DisableMsg", "false");
		}
		disableMsg = config.getString("General.disableMsg").equalsIgnoreCase("true");
		MaxPlayersInParty = config.getInt("General.MaxPlayersInParty");
		if (MaxPlayersInParty == 0) {
			config.set("General.MaxPlayersInParty", 0);
		}
		friendPermission = config.getString("Permissions.FriendPermission");
		if (friendPermission.equalsIgnoreCase("")) {
			config.set("Permissions.FriendPermission", "");
		}
		partyPermission = config.getString("Permissions.PartyPermission");
		if (partyPermission.equals("")) {
			config.set("Permissions.PartyPermission", "");
		}
		NoPlayerLimitForPartysPermission = config.getString("Permissions.NoPlayerLimitForPartys");
		if (NoPlayerLimitForPartysPermission.equals("")) {
			config.set("Permissions.NoPlayerLimitForPartys", "");
		}
		friendsAliasMsg = config.getString("Aliases.FriendsAliasMsg");
		if (friendsAliasMsg.equals("")) {
			config.set("Aliases.FriendsAliasMsg", "msg");
		}
		PartyAlias = config.getString("Aliases.PartyAlias");
		if (PartyAlias.equals("")) {
			config.set("Aliases.PartyAlias", "party");
		}
		joinAlias = config.getString("Aliases.JoinAlias");
		if (joinAlias.equals("")) {
			config.set("Aliases.JoinAlias", "join");
		}
		inviteAlias = config.getString("Aliases.InviteAlias");
		if (inviteAlias.equals("")) {
			config.set("Aliases.InviteAlias", "invite");
		}
		kickAlias = config.getString("Aliases.KickAlias");
		if (kickAlias.equals("")) {
			config.set("Aliases.KickAlias", "kick");
		}
		infoAlias = config.getString("Aliases.InfoAlias");
		if (infoAlias.equals("")) {
			config.set("Aliases.InfoAlias", "info");
		}
		leaveAlias = config.getString("Aliases.leaveAlias");
		if (leaveAlias.equals("")) {
			config.set("Aliases.leaveAlias", "leave");
		}
		chatAlias = config.getString("Aliases.ChatAlias");
		if (chatAlias.equals("")) {
			config.set("Aliases.ChatAlias", "chat");
		}
		leaderAlias = config.getString("Aliases.LeaderAlias");
		if (leaderAlias.equals("")) {
			config.set("Aliases.LeaderAlias", "leader");
		}
		acceptAlias = config.getString("Aliases.AcceptAlias");
		if (acceptAlias.equals("")) {
			config.set("Aliases.AcceptAlias", "accept");
		}
		addAlias = config.getString("Aliases.AddAlias");
		if (acceptAlias.equals("")) {
			config.set("Aliases.AddAlias", "add");
		}
		denyAlias = config.getString("Aliases.DenyAlias");
		if (denyAlias.equals("")) {
			config.set("Aliases.denyAlias", "deny");
		}
		settingsAlias = config.getString("Aliases.SettingsAlias");
		if (settingsAlias.equals("")) {
			config.set("Aliases.SettingsAlias", "settings");
		}
		jumpAlias = config.getString("Aliases.JumpAlias");
		if (jumpAlias.equals("")) {
			config.set("Aliases.JumpAlias", "jump");
		}
		listAlias = config.getString("Aliases.ListAlias");
		if (listAlias.equals("")) {
			config.set("Aliases.ListAlias", "list");
		}
		removeAlias = config.getString("Aliases.RemoveAlias");
		if (removeAlias.equals("")) {
			config.set("Aliases.RemoveAlias", "remove");
		}
		friendAlias = config.getString("Aliases.FriendsAlias");
		if (friendAlias.equals("")) {
			config.set("Aliases.FriendsAlias", "friend");
		}
		partyChatShortAlias = config.getString("Aliases.PartyChatShortAlias");
		if (partyChatShortAlias.equals("")) {
			config.set("Aliases.PartyChatShortAlias", "p");
		}
		ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
	}

	public void ladeMessageYML() throws IOException {
		File file = new File(getDataFolder().getPath(), "Messages.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		Configuration messagesYml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		if (messagesYml.getString("General.LanguageName").equals("")) {
			messagesYml.set("General.LanguageName", "Own");
		}
		if (messagesYml.getString("Party.General.PartyPrefix").equals("")) {
			messagesYml.set("Party.General.PartyPrefix", "�7[�5Party�7] ");
		}
		prefix = messagesYml.getString("Party.General.PartyPrefix");
		if (messagesYml.getString("Party.Error.CommandNotFound").equals("")) {
			messagesYml.set("Party.Error.CommandNotFound", "�cThis command doesn�t exist!");
		}
		if (messagesYml.getString("Party.CommandUsage.Join").equals("")) {
			messagesYml.set("Party.CommandUsage.Join", "�8/�5Party " + "join <Name>" + " �8- �7Join �7a �7party");
		}
		if (messagesYml.getString("Party.CommandUsage.Invite").equals("")) {
			messagesYml.set("Party.CommandUsage.Invite",
					"�8/�5Party " + "invite <player>" + " �8- �7Invite �7a �7player �7into �7your �7Party");
		}
		if (messagesYml.getString("Party.CommandUsage.List").equals("")) {
			messagesYml.set("Party.CommandUsage.List",
					"�8/�5Party " + "list" + " �8- �7List �7all �7players �7who �7are �7in �7the �7party");
		}
		if (messagesYml.getString("Party.CommandUsage.Chat").equals("")) {
			messagesYml.set("Party.CommandUsage.Chat",
					"�8/�5Party " + "chat <message>" + " �8- �7Send �7all �7players �7in �7the �7party �7a �7message");
		}
		if (messagesYml.getString("Party.CommandUsage.Leave").equals("")) {
			messagesYml.set("Party.CommandUsage.Leave", "�8/�5Party " + "leave" + " �8- �7Leave the party");
		}
		if (messagesYml.getString("Party.CommandUsage.Kick").equals("")) {
			messagesYml.set("Party.CommandUsage.Kick",
					"�8/�5Party " + "kick <player>" + " �8- �7Kicks �7a �7player �7out �7of �7the �7party");
		}
		if (messagesYml.getString("Party.CommandUsage.Leader").equals("")) {
			messagesYml.set("Party.CommandUsage.Leader", "�8/�5Party " + "leader �5<player>"
					+ " �8- �7Makes �7another �7player �7to �7the �7party �7leader");
		}
		if (messagesYml.getString("Party.Command.Chat.Prefix").equals("")) {
			messagesYml.set("Party.Command.Chat.Prefix", "�7[�5PartyChat�7] ");
		}
		if (messagesYml.getString("Party.Command.Chat.ContentColor").equals("")) {
			messagesYml.set("Party.Command.Chat.ContentColor", "�7");
		}
		if (messagesYml.getString("Party.Command.Chat.PartyChatOutput").equals("")) {
			messagesYml.set("Party.Command.Chat.PartyChatOutput", "�e[SENDERNAME]�5:[MESSAGE_CONTENT]");
		}
		if (messagesYml.getString("Party.Command.Chat.ErrorNoMessage").equals("")) {
			messagesYml.set("Party.Command.Chat.ErrorNoMessage", "�5You need to give a message");
		}
		if (messagesYml.getString("Party.Command.Chat.ErrorNoParty").equals("")) {
			messagesYml.set("Party.Command.Chat.ErrorNoParty", "�5You need to be in a party");
		}
		ConfigurationProvider.getProvider(YamlConfiguration.class).save(messagesYml, file);
	}
}