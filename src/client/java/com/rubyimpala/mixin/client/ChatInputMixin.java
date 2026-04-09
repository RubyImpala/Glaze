package com.rubyimpala.mixin.client;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.features.chatrules.ChatRuleService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ChatScreen.class)
public class ChatInputMixin {

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void onHandleChatInput(String msg, boolean addToRecent, CallbackInfo ci) {
        Optional<String> transformed = ChatRuleService.apply(msg);
        if (transformed.isEmpty()) return;

        // Cancel the original message
        ci.cancel();

        String result = transformed.get();
        Minecraft minecraft = Minecraft.getInstance();

        // Show notification if enabled
        if (GlazeSettings.CONFIG().showRedirectNotification) {
            var player = minecraft.player;
            if (player == null) return;
            player.sendSystemMessage(
                    Component.literal("§6[Glaze] §7Redirected: §e" + msg + " §7→ §a" + result)
            );
        }

        // Send the transformed message — command or chat
        var connection = minecraft.getConnection();
        if (connection == null) return;

        if (result.startsWith("/")) {
            connection.sendCommand(result.substring(1));
        } else {
            connection.sendChat(result);
        }
    }
}