package ru.xunto.roleplaychat.features.middleware;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.PermissionAPI;
import ru.xunto.roleplaychat.framework.api.Environment;
import ru.xunto.roleplaychat.framework.api.Middleware;
import ru.xunto.roleplaychat.framework.api.Request;
import ru.xunto.roleplaychat.framework.api.Stage;

import java.util.Objects;
import java.util.Set;

/*
    TODO:
        incapsulate the minecraft-forge dependencies:
            - EntityPlayer
            - TextFormatting
            - World
            - PermissionAPI
*/

public class ToGmMiddleware extends Middleware {
    @Override public void process(Request request, Environment environment) {
        Environment newEnvironment = environment.clone();

        Set<EntityPlayer> originalRecipients = environment.getRecipients();
        Set<EntityPlayer> recipients = newEnvironment.getRecipients();
        recipients.clear();

        newEnvironment.getColors().clear();
        newEnvironment.getColors().put("default", TextFormatting.DARK_GRAY);

        World[] worlds = Objects.requireNonNull(request.getWorld().getMinecraftServer()).worlds;

        for (World world : worlds) {
            for (EntityPlayer player : world.playerEntities) {
                boolean allowed = PermissionAPI.hasPermission(player, "gm");
                if (allowed && !originalRecipients.contains(player))
                    recipients.add(player);
            }
        }

        if (recipients.size() > 0)
            environment.getCore().send(newEnvironment);
    }

    @Override public Stage getStage() {
        return Stage.POST;
    }
}
