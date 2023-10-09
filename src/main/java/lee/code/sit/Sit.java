package lee.code.sit;

import lee.code.sit.listeners.SitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Sit extends JavaPlugin {

  @Override
  public void onEnable() {
    registerListeners();
  }

  @Override
  public void onDisable() {
  }

  private void registerListeners() {
    getServer().getPluginManager().registerEvents(new SitListener(), this);
  }
}
