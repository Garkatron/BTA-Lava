package deus.lava.api.interfaces;

public interface IEntityPlayerLua extends IEntityPlayerLuaCommon {
	void lava$sleepPlayer();
	void lava$sleepPlayerAtCoords(int x, int y, int z);
}
