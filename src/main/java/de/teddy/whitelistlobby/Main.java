package de.teddy.whitelistlobby;

import de.teddy.whitelistlobby.commands.RedeemCommand;
import de.teddybear2004.library.TeddyLibrary;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        TeddyLibrary plugin = getPlugin(TeddyLibrary.class);
        SessionFactory sessionFactory = plugin.getSessionFactory();

        Objects.requireNonNull(getCommand("redeem")).setExecutor(new RedeemCommand(sessionFactory));
    }

    @Override
    public void onDisable() {

    }
}
