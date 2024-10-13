package deus.lava.api.interfaces;

import deus.lava.signal.LuaSignal;

public interface IEntityPlayerLuaCommon {
	LuaSignal onJump = new LuaSignal();
	LuaSignal onIsInWater = new LuaSignal();
	LuaSignal onIsInLava = new LuaSignal();
	LuaSignal onBreakBlock = new LuaSignal();
	LuaSignal onPickUpItem = new LuaSignal();
	LuaSignal onDropItem = new LuaSignal();
	LuaSignal onShift = new LuaSignal();
	LuaSignal onRun = new LuaSignal();
	LuaSignal onWalk = new LuaSignal();
	LuaSignal onDeath = new LuaSignal();

	// Métodos get para acceder a las señales
	default LuaSignal getOnJump() {
		return onJump;
	}

	default LuaSignal getOnIsInWater() {
		return onIsInWater;
	}

	default LuaSignal getOnIsInLava() {
		return onIsInLava;
	}

	default LuaSignal getOnBreakBlock() {
		return onBreakBlock;
	}

	default LuaSignal getOnPickUpItem() {
		return onPickUpItem;
	}

	default LuaSignal getOnDropItem() {
		return onDropItem;
	}

	default LuaSignal getOnShift() {
		return onShift;
	}

	default LuaSignal getOnRun() {
		return onRun;
	}

	default LuaSignal getOnWalk() {
		return onWalk;
	}

	default LuaSignal getOnDeath() {
		return onDeath;
	}
}
