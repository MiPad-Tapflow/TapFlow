package com.xiaomi.mslgrdp.multwindow;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SessionState implements Parcelable {
    public static final Parcelable.Creator<SessionState> CREATOR = new Parcelable.Creator<SessionState>() { // from class: com.xiaomi.mslgrdp.multwindow.SessionState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SessionState createFromParcel(Parcel in) {
            return new SessionState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SessionState[] newArray(int size) {
            return new SessionState[size];
        }
    };
    private long instance;
    private boolean railChannelReady;
    private Set<LibFreeRDP.UIEventListener> uiEventListeners = new CopyOnWriteArraySet();

    public SessionState(Parcel parcel) {
        this.railChannelReady = false;
        this.instance = parcel.readLong();
        this.railChannelReady = parcel.readBoolean();
    }

    public SessionState(long instance) {
        this.railChannelReady = false;
        this.instance = instance;
        this.railChannelReady = false;
    }

    public SessionState(long instance, Uri openUri) {
        this.railChannelReady = false;
        this.instance = instance;
        this.railChannelReady = false;
    }

    public void connect() {
        LibFreeRDP.setConnectionInfo(this.instance);
        LibFreeRDP.connect(this.instance);
    }

    public long getInstance() {
        return this.instance;
    }

    public Set<LibFreeRDP.UIEventListener> getUIEventListeners() {
        return this.uiEventListeners;
    }

    public void addUIEventListener(LibFreeRDP.UIEventListener uiEventListener) {
        if (uiEventListener != null) {
            this.uiEventListeners.add(uiEventListener);
        }
    }

    public void removeUIEventListener(LibFreeRDP.UIEventListener uiEventListener) {
        this.uiEventListeners.remove(uiEventListener);
    }

    public boolean getRailChannelStatus() {
        return this.railChannelReady;
    }

    public void setRailChannelStatus(boolean ready) {
        this.railChannelReady = ready;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.instance);
        out.writeBoolean(this.railChannelReady);
    }
}