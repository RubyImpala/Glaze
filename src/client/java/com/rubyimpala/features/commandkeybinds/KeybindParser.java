package com.rubyimpala.features.commandkeybinds;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeybindParser {

    // Maps key name strings to GLFW key codes
    private static final Map<String, Integer> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put("A", GLFW.GLFW_KEY_A); KEY_MAP.put("B", GLFW.GLFW_KEY_B);
        KEY_MAP.put("C", GLFW.GLFW_KEY_C); KEY_MAP.put("D", GLFW.GLFW_KEY_D);
        KEY_MAP.put("E", GLFW.GLFW_KEY_E); KEY_MAP.put("F", GLFW.GLFW_KEY_F);
        KEY_MAP.put("G", GLFW.GLFW_KEY_G); KEY_MAP.put("H", GLFW.GLFW_KEY_H);
        KEY_MAP.put("I", GLFW.GLFW_KEY_I); KEY_MAP.put("J", GLFW.GLFW_KEY_J);
        KEY_MAP.put("K", GLFW.GLFW_KEY_K); KEY_MAP.put("L", GLFW.GLFW_KEY_L);
        KEY_MAP.put("M", GLFW.GLFW_KEY_M); KEY_MAP.put("N", GLFW.GLFW_KEY_N);
        KEY_MAP.put("O", GLFW.GLFW_KEY_O); KEY_MAP.put("P", GLFW.GLFW_KEY_P);
        KEY_MAP.put("Q", GLFW.GLFW_KEY_Q); KEY_MAP.put("R", GLFW.GLFW_KEY_R);
        KEY_MAP.put("S", GLFW.GLFW_KEY_S); KEY_MAP.put("T", GLFW.GLFW_KEY_T);
        KEY_MAP.put("U", GLFW.GLFW_KEY_U); KEY_MAP.put("V", GLFW.GLFW_KEY_V);
        KEY_MAP.put("W", GLFW.GLFW_KEY_W); KEY_MAP.put("X", GLFW.GLFW_KEY_X);
        KEY_MAP.put("Y", GLFW.GLFW_KEY_Y); KEY_MAP.put("Z", GLFW.GLFW_KEY_Z);
        KEY_MAP.put("0", GLFW.GLFW_KEY_0); KEY_MAP.put("1", GLFW.GLFW_KEY_1);
        KEY_MAP.put("2", GLFW.GLFW_KEY_2); KEY_MAP.put("3", GLFW.GLFW_KEY_3);
        KEY_MAP.put("4", GLFW.GLFW_KEY_4); KEY_MAP.put("5", GLFW.GLFW_KEY_5);
        KEY_MAP.put("6", GLFW.GLFW_KEY_6); KEY_MAP.put("7", GLFW.GLFW_KEY_7);
        KEY_MAP.put("8", GLFW.GLFW_KEY_8); KEY_MAP.put("9", GLFW.GLFW_KEY_9);
        KEY_MAP.put("F1", GLFW.GLFW_KEY_F1); KEY_MAP.put("F2", GLFW.GLFW_KEY_F2);
        KEY_MAP.put("F3", GLFW.GLFW_KEY_F3); KEY_MAP.put("F4", GLFW.GLFW_KEY_F4);
        KEY_MAP.put("F5", GLFW.GLFW_KEY_F5); KEY_MAP.put("F6", GLFW.GLFW_KEY_F6);
        KEY_MAP.put("F7", GLFW.GLFW_KEY_F7); KEY_MAP.put("F8", GLFW.GLFW_KEY_F8);
        KEY_MAP.put("F9", GLFW.GLFW_KEY_F9); KEY_MAP.put("F10", GLFW.GLFW_KEY_F10);
        KEY_MAP.put("F11", GLFW.GLFW_KEY_F11); KEY_MAP.put("F12", GLFW.GLFW_KEY_F12);
        KEY_MAP.put("SPACE", GLFW.GLFW_KEY_SPACE);
        KEY_MAP.put("ENTER", GLFW.GLFW_KEY_ENTER);
        KEY_MAP.put("TAB", GLFW.GLFW_KEY_TAB);
        KEY_MAP.put("UP", GLFW.GLFW_KEY_UP); KEY_MAP.put("DOWN", GLFW.GLFW_KEY_DOWN);
        KEY_MAP.put("LEFT", GLFW.GLFW_KEY_LEFT); KEY_MAP.put("RIGHT", GLFW.GLFW_KEY_RIGHT);
        KEY_MAP.put("LSHIFT", GLFW.GLFW_KEY_LEFT_SHIFT);
        KEY_MAP.put("RSHIFT", GLFW.GLFW_KEY_RIGHT_SHIFT);
        KEY_MAP.put("LCTRL", GLFW.GLFW_KEY_LEFT_CONTROL);
        KEY_MAP.put("RCTRL", GLFW.GLFW_KEY_RIGHT_CONTROL);
        KEY_MAP.put("LALT", GLFW.GLFW_KEY_LEFT_ALT);
        KEY_MAP.put("RALT", GLFW.GLFW_KEY_RIGHT_ALT);
        KEY_MAP.put("ESCAPE", GLFW.GLFW_KEY_ESCAPE);
        KEY_MAP.put("BACKSPACE", GLFW.GLFW_KEY_BACKSPACE);
        KEY_MAP.put("DELETE", GLFW.GLFW_KEY_DELETE);
        KEY_MAP.put("INSERT", GLFW.GLFW_KEY_INSERT);
        KEY_MAP.put("HOME", GLFW.GLFW_KEY_HOME);
        KEY_MAP.put("END", GLFW.GLFW_KEY_END);
        KEY_MAP.put("PAGEUP", GLFW.GLFW_KEY_PAGE_UP);
        KEY_MAP.put("PAGEDOWN", GLFW.GLFW_KEY_PAGE_DOWN);
        KEY_MAP.put("CAPS", GLFW.GLFW_KEY_CAPS_LOCK);
        KEY_MAP.put("NUMLOCK", GLFW.GLFW_KEY_NUM_LOCK);
        KEY_MAP.put("SCROLL", GLFW.GLFW_KEY_SCROLL_LOCK);
        KEY_MAP.put("PRINT", GLFW.GLFW_KEY_PRINT_SCREEN);
        KEY_MAP.put("PAUSE", GLFW.GLFW_KEY_PAUSE);
    }

    public record ParsedKeybind(int keyCode, boolean ctrl, boolean shift, boolean alt) {
        public boolean isValid() { return keyCode != -1; }
    }

    /** Parses "CTRL+Y", "SHIFT+F5", "Y" etc. into a ParsedKeybind */
    public static ParsedKeybind parse(String keybind) {
        if (keybind == null || keybind.isBlank()) return new ParsedKeybind(-1, false, false, false);

        String upper = keybind.toUpperCase().trim();
        String[] parts = upper.split("\\+");

        boolean ctrl = false, shift = false, alt = false;
        String keyPart = parts[parts.length - 1].trim(); // Last part is always the key

        for (int i = 0; i < parts.length - 1; i++) {
            switch (parts[i].trim()) {
                case "CTRL" -> ctrl = true;
                case "SHIFT" -> shift = true;
                case "ALT" -> alt = true;
            }
        }

        int keyCode = KEY_MAP.getOrDefault(keyPart, -1);
        return new ParsedKeybind(keyCode, ctrl, shift, alt);
    }

    /** Checks if a parsed keybind is currently pressed */
    public static boolean isPressed(long window, ParsedKeybind bind) {
        if (!bind.isValid()) return false;

        // If the main key is itself a modifier key, skip modifier matching
        boolean mainKeyIsModifier = isModifierKey(bind.keyCode());

        if (!mainKeyIsModifier) {
            boolean ctrlDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            boolean shiftDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            boolean altDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;

            if (bind.ctrl() != ctrlDown) return false;
            if (bind.shift() != shiftDown) return false;
            if (bind.alt() != altDown) return false;
        }

        return GLFW.glfwGetKey(window, bind.keyCode()) == GLFW.GLFW_PRESS;
    }

    private static boolean isModifierKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
                || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
                || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
                || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
                || keyCode == GLFW.GLFW_KEY_LEFT_ALT
                || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
    }
}