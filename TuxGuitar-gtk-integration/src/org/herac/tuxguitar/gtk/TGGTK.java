package org.herac.tuxguitar.gtk;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.Platform;

public class TGGTK {

    public static byte[] ascii(String name) {
        int length = name.length();
        char[] chars = new char[length];
        name.getChars(0, length, chars, 0);
        byte[] buffer = new byte[length + 1];
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) chars[i];
        }
        return buffer;
    }

    public static String charPtrToString(long address) {
        int length = C.strlen(address);
        byte[] buffer = new byte[length];
        C.memmove(buffer, address, length);
        return new String(Converter.mbcsToWcs(buffer));
    }

    public static native long _createHeaderBar();

    public static native long _createButtonBox();

    public static native long _createHidingScrolledWindow(int alignment);

    public static native long _createMenuButton(long box);

    public static native long _createMenuActionItem(long menu, String nameStr);

    public static native long _createMenuCheckboxItem(long menu, String nameStr);

    public static native long _createMenuRadioItem(long menu, String nameStr);

    public static native void _menuItemConnectActivated(long item, long callback);

    public static native long _createMenuSubMenuItem(long menu, String menuName, long box);

    public static native long _createMenuGoBackItem(String menuName);

    public static native void _menuItemSetEnabled(long object, boolean enabled);

    public static native boolean _menuItemGetEnabled(long object);

    public static native void _headerBarPackLeft(long headerBar, long widget);

    public static native void _headerBarPackCenter(long headerBar, long widget);

    public static native void _headerBarPackRight(long headerBar, long widget);

    public static native void _windowSetHeaderBar(long window, long header);

    public static native void _showAll(long widget);

    public static long createHeaderBar() {
        Platform.lock.lock();
        try {
            return _createHeaderBar();
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createButtonBox() {
        Platform.lock.lock();
        try {
            return _createButtonBox();
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createHidingScrolledWindow(int alignment) {
        Platform.lock.lock();
        try {
            return _createHidingScrolledWindow(alignment);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuButton(long box) {
        Platform.lock.lock();
        try {
            return _createMenuButton(box);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuActionItem(long menu, String nameStr) {
        Platform.lock.lock();
        try {
            return _createMenuActionItem(menu, nameStr);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuCheckboxItem(long menu, String nameStr) {
        Platform.lock.lock();
        try {
            return _createMenuCheckboxItem(menu, nameStr);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuRadioItem(long menu, String nameStr) {
        Platform.lock.lock();
        try {
            return _createMenuRadioItem(menu, nameStr);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void menuItemConnectActivated(long item, long callback) {
        Platform.lock.lock();
        try {
            _menuItemConnectActivated(item, callback);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuSubMenuItem(long menu, String menuName, long box) {
        Platform.lock.lock();
        try {
            return _createMenuSubMenuItem(menu, menuName, box);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static long createMenuGoBackItem(String menuName) {
        Platform.lock.lock();
        try {
            return _createMenuGoBackItem(menuName);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void menuItemSetEnabled(long object, boolean enabled) {
        Platform.lock.lock();
        try {
            _menuItemSetEnabled(object, enabled);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static boolean menuItemGetEnabled(long object) {
        Platform.lock.lock();
        try {
            return _menuItemGetEnabled(object);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void headerBarPackLeft(long headerBar, long widget) {
        Platform.lock.lock();
        try {
            _headerBarPackLeft(headerBar, widget);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void headerBarPackCenter(long headerBar, long widget) {
        Platform.lock.lock();
        try {
            _headerBarPackCenter(headerBar, widget);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void headerBarPackRight(long headerBar, long widget) {
        Platform.lock.lock();
        try {
            _headerBarPackRight(headerBar, widget);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void windowSetHeaderBar(long window, long header) {
        Platform.lock.lock();
        try {
            _windowSetHeaderBar(window, header);
        } finally {
            Platform.lock.unlock();
        }
    }

    public static void showAll(long widget) {
        Platform.lock.lock();
        try {
            _showAll(widget);
        } finally {
            Platform.lock.unlock();
        }
    }
}
