package me.ethan.interception.api;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import me.ethan.interception.api.interfaces.Interception;
import me.ethan.interception.api.interfaces.InterceptionFilter;
import me.ethan.interception.api.interfaces.Kernel32;

public class Mouse extends Thread {
    private static Mouse instance;
    private final Interception lib;
    private final Callback mouseCallback, keyboardCallback;
    private final Pointer context;
    private final InjectedStroke emptyStroke;
    private static boolean DEBUG_DEVICES = false;

    public static void main(String[] args) {
        Mouse.getInstance().move(50, 50, 12);
    }

    public Mouse() {
        raise_process_priority();
        this.lib = Interception.interception;
        this.mouseCallback = new Callback() {
            public int callback(int device) {
                return lib.interception_is_mouse(device);
            }
        };
        this.keyboardCallback = new Callback() {
            public int callback(int device) {
                return lib.interception_is_keyboard(device);
            }
        };
        this.context = lib.interception_create_context();
        this.emptyStroke = new InjectedStroke();
        lib.interception_set_filter(context, keyboardCallback, (short) (InterceptionFilter.INTERCEPTION_FILTER_KEY_DOWN
                | InterceptionFilter.INTERCEPTION_FILTER_KEY_UP));
        lib.interception_set_filter(context, mouseCallback, (short) (InterceptionFilter.INTERCEPTION_FILTER_MOUSE_MOVE));
        this.start();
    }


    public void run() {
        int device;
        while (lib.interception_receive(context, device = lib.interception_wait(context), emptyStroke, 1) > 0) {
            if(DEBUG_DEVICES) {
                System.out.printf("device:%d code:%d(0x%h)\n", device, emptyStroke.code, emptyStroke.code);
            }
            if (lib.interception_is_mouse(device) == 1) {
                if(!emptyStroke.isInjected()) {
                    //System.out.println(emptyStroke.x + " - "+emptyStroke.y);
                }
            }
            if(!emptyStroke.isInjected()) {
                lib.interception_send(context, device, emptyStroke, 1);
            }
        }

        lib.interception_destroy_context(context);

    }

    public static Mouse getInstance() {
        if(instance == null)
            instance = new Mouse();
        return instance;
    }

    /**
     * @param x - moves the mouse along the x-axis in x amount of increments. ex: -50 will move to the left 50 pixels.
     * @param y moves the mouse along the y-axis in x amount of increments. ex: 50 will move down 50 pixels.
     */
    public void move(int x, int y, int deviceId) {
        final InjectedStroke stroke = new InjectedStroke((short) 0x00, (short) 0x00, (short) 0x00, (short) 0x00, x, y,0, true);
        if(lib != null) {
            lib.interception_send(context, deviceId, stroke, 1);
        }
    }

    private static void raise_process_priority() {
        final Kernel32 k32 = Kernel32.k32;
        final boolean ok = k32.SetPriorityClass(k32.GetCurrentProcess(), k32.HIGH_PRIORITY_CLASS);
        System.out.println("raise_process_priority:" + ok);
    }
}
