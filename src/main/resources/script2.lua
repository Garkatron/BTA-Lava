local thePlayer = Minecraft:getMinecraft().thePlayer
thePlayer.onJump:connect(
        function(n)
            thePlayer:sendMessage("Hello from BTA")
        end
)

function stop()
    os.exit()
end
