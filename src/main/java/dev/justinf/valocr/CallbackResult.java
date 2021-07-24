package dev.justinf.valocr;

import java.util.concurrent.atomic.AtomicBoolean;

public class CallbackResult {

    private final AtomicBoolean written;
    private final AtomicBoolean received;

    public CallbackResult() {
        written = new AtomicBoolean(false);
        received = new AtomicBoolean(false);
    }

    public AtomicBoolean getWritten() {
        return written;
    }

    public AtomicBoolean getReceived() {
        return received;
    }
}
