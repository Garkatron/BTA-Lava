package deus.lava.api.player;

import deus.lava.api.interfaces.IEntityPlayerLua;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.server.entity.player.EntityPlayerMP;

public class PlayerUtils {
	private EntityPlayerSP playerSP;
	private EntityPlayer player;
	public void setPlayerSP(EntityPlayerSP player) {
		this.playerSP = player;
	}
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	public EntityPlayer getPlayer() {
		return player;
	}
	public IEntityPlayerLua getLavaPlayer() {
		return (IEntityPlayerLua) player;
	}
	public void setGamemode(int id) {
		this.playerSP.setGamemode(Gamemode.gamemodesList[id]);
	}
}
