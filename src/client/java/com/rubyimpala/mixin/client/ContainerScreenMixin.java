package com.rubyimpala.mixin.client;

import com.rubyimpala.features.ahsearch.input.AhSearchKeybinds;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMixin<T extends AbstractContainerMenu> {

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        AhSearchKeybinds.onKeyPressed(event);
    }
}