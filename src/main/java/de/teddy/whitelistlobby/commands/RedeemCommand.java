package de.teddy.whitelistlobby.commands;

import de.teddy.bansystem.tables.BansystemPlayer;
import de.teddy.bansystem.tables.BansystemToken;
import de.teddy.bansystem.tables.BansystemWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RedeemCommand implements CommandExecutor {
    private final SessionFactory sessionFactory;

    public RedeemCommand(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player))
            return true;

        if (strings.length < 1) {
            return false;
        }

        String token = strings[0];

        sessionFactory.inSession(session -> {
            session.beginTransaction();

            BansystemToken token1 = session.createQuery("FROM BansystemToken WHERE token = :token", BansystemToken.class)
                    .setParameter("token", token)
                    .uniqueResult();
            if (token1 != null) {

                BansystemWhitelist bansystemWhitelist = session.createQuery(
                                "FROM BansystemWhitelist WHERE player.uuid = :uuid AND bansystemToken.gamemode = :gamemode", BansystemWhitelist.class)
                        .setParameter("uuid", player.getUniqueId().toString())
                        .setParameter("gamemode", token1.getGamemode())
                        .uniqueResult();

                if (bansystemWhitelist != null) {
                    commandSender.sendMessage(ChatColor.RED + "Du bist bereits für diesen Spielmodus auf der Whitelist!");
                } else {
                    // Create a new BansystemWhitelist with the token and persist it to the database
                    BansystemWhitelist newBansystemWhitelist = new BansystemWhitelist();
                    newBansystemWhitelist.setBansystemToken(token1);

                    BansystemPlayer bansystemPlayer = session.find(BansystemPlayer.class, player.getUniqueId().toString());

                    if (bansystemPlayer == null) {
                        bansystemPlayer = new BansystemPlayer();
                        bansystemPlayer.setUuid(player.getUniqueId().toString());
                        bansystemPlayer.setUsername(player.getName());
                        session.persist(bansystemPlayer);
                    }

                    newBansystemWhitelist.setPlayer(bansystemPlayer);
                    session.persist(newBansystemWhitelist);

                    commandSender.sendMessage(ChatColor.GREEN + "Token erfolgreich eingelöst und du bist jetzt für diesen Spielmodus auf der Whitelist!");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "Ungültiger Token!");
            }

            session.getTransaction().commit();
        });
        return true;
    }
}
