package com.xiaomi.mslgrdp.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.xiaomi.mslgrdp.domain.BookmarkBase;

/* loaded from: classes5.dex */
public class SessionState implements Parcelable {
    public static final Parcelable.Creator<SessionState> CREATOR = new Parcelable.Creator<SessionState>() { // from class: com.xiaomi.mslgrdp.application.SessionState.1
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
    private BookmarkBase bookmark;
    private long instance;
    private Uri openUri;
    private boolean railChannelReady;
    private BitmapDrawable surface;
    private LibFreeRDP.UIEventListener uiEventListener;

    public SessionState(Parcel parcel) {
        this.railChannelReady = false;
        this.instance = parcel.readLong();
        this.railChannelReady = parcel.readBoolean();
        this.bookmark = (BookmarkBase) parcel.readParcelable(null);
        this.openUri = (Uri) parcel.readParcelable(null);
        Bitmap bitmap = (Bitmap) parcel.readParcelable(null);
        this.surface = new BitmapDrawable(bitmap);
    }

    public SessionState(long instance, BookmarkBase bookmark) {
        this.railChannelReady = false;
        this.instance = instance;
        this.bookmark = bookmark;
        this.openUri = null;
        this.uiEventListener = null;
        this.railChannelReady = false;
    }

    public SessionState(long instance, Uri openUri) {
        this.railChannelReady = false;
        this.instance = instance;
        this.bookmark = null;
        this.openUri = openUri;
        this.uiEventListener = null;
        this.railChannelReady = false;
    }

    public void connect(Context context) {
        BookmarkBase bookmarkBase = this.bookmark;
        if (bookmarkBase != null) {
            LibFreeRDP.setConnectionInfo(context, this.instance, bookmarkBase);
        } else {
            LibFreeRDP.setConnectionInfo(context, this.instance, this.openUri);
        }
        LibFreeRDP.connect(this.instance);
    }

    public long getInstance() {
        return this.instance;
    }

    public BookmarkBase getBookmark() {
        return this.bookmark;
    }

    public Uri getOpenUri() {
        return this.openUri;
    }

    public LibFreeRDP.UIEventListener getUIEventListener() {
        return this.uiEventListener;
    }

    public void setUIEventListener(LibFreeRDP.UIEventListener uiEventListener) {
        this.uiEventListener = uiEventListener;
    }

    public BitmapDrawable getSurface() {
        return this.surface;
    }

    public void setSurface(BitmapDrawable surface) {
        this.surface = surface;
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
        out.writeParcelable(this.bookmark, flags);
        out.writeParcelable(this.openUri, flags);
        out.writeParcelable(this.surface.getBitmap(), flags);
    }
}