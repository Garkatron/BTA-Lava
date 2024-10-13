
-- luajava bindClass
-- Gamemode = luajava.bindClass("net.minecraft.core.player.gamemode.Gamemode")

local mc = Minecraft:getMinecraft()
Lava.onInit:connect(
        function(n)
            print(n)
        end
)
Minecraft.onWorldStarted:connect(
        function(n)
            print(n)
        end
)
Minecraft.onWorldChanged:connect(
        function(n)
            print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",n)
            local player = mc.thePlayer
            PlayerUtils:setPlayer(setPlayer)
            local lavaPlayer = PlayerUtils:getLavaPlayer()
            print(lavaPlayer)
        end
)


-- Lava Utils

--[[ Embedded function
Minecraft:whenTickRun("onTick")

function onTick()
end

-- Signals callback
local playerSP = mc.thePlayer
print(type(playerSP))

local lavaPlayer = PlayerUtils:getLavaPlayer()

lavaPlayer.getOnJump:connect(
        function(n)

            player:sendMessage("DADADADADAD")

--            public void playSound(String soundPath, SoundCategory category, float volume, float pitch) {



            --PlayerUtils:setPlayer(player)
            --PlayerUtils:setGamemode(0)
            --player:sleepPlayer()

        end
)

lavaPlayer:onPickUpItem():connect(
        function(n)
            print(n)
        end
)
]]--

