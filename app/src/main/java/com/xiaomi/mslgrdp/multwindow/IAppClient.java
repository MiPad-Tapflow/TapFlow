package com.xiaomi.mslgrdp.multwindow;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* loaded from: classes6.dex */
public interface IAppClient extends IInterface {
    public static final String DESCRIPTOR = "com.xiaomi.mslgrdp.multwindow.IAppClient";

    void OnDeleteOptimg(int i) throws RemoteException;

    void OnGraphicsUpdateMultiWindow(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, String str, int i11, boolean z, boolean z2, int i12, boolean z3, boolean z4) throws RemoteException;

    void OnMinimizeRequested(long j, int i, boolean z) throws RemoteException;

    void OnRailChannelReady(boolean z) throws RemoteException;

    void OnUpdatePointerIcon(long j, int i, int i2, int i3, int i4, Bitmap bitmap) throws RemoteException;

    void UpdateCursorRect(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void appFinishandExit(long j, int i) throws RemoteException;

    void inputMethodActivate(boolean z) throws RemoteException;

    void onWindowClosed(long j, int i, int i2) throws RemoteException;

    void onWindowResize(long j, int i, int i2) throws RemoteException;

    void updateSession(SessionState sessionState) throws RemoteException;

    /* loaded from: classes6.dex */
    public static class Default implements IAppClient {
        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void updateSession(SessionState session) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnRailChannelReady(boolean ready) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void onWindowClosed(long inst, int windowId, int appType) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnMinimizeRequested(long inst, int appType, boolean minimized) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void inputMethodActivate(boolean activate) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY, Bitmap bitmap) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void UpdateCursorRect(int appType, int left, int top, int width, int height) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void onWindowResize(long inst, int windowId, int appType) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void appFinishandExit(long inst, int appType) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnDeleteOptimg(int appType) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes6.dex */
    public static abstract class Stub extends Binder implements IAppClient {
        static final int TRANSACTION_OnDeleteOptimg = 11;
        static final int TRANSACTION_OnGraphicsUpdateMultiWindow = 3;
        static final int TRANSACTION_OnMinimizeRequested = 5;
        static final int TRANSACTION_OnRailChannelReady = 2;
        static final int TRANSACTION_OnUpdatePointerIcon = 7;
        static final int TRANSACTION_UpdateCursorRect = 8;
        static final int TRANSACTION_appFinishandExit = 10;
        static final int TRANSACTION_inputMethodActivate = 6;
        static final int TRANSACTION_onWindowClosed = 4;
        static final int TRANSACTION_onWindowResize = 9;
        static final int TRANSACTION_updateSession = 1;

        public Stub() {
            attachInterface(this, IAppClient.DESCRIPTOR);
        }

        public static IAppClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppClient.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppClient)) {
                return (IAppClient) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean _arg0;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IAppClient.DESCRIPTOR);
            }
            switch (code) {
                case 1598968902:
                    reply.writeString(IAppClient.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            updateSession((SessionState) _Parcel.readTypedObject(data, SessionState.CREATOR));
                            reply.writeNoException();
                            return true;
                        case 2:
                            boolean _arg02 = data.readInt() != 0;
                            OnRailChannelReady(_arg02);
                            reply.writeNoException();
                            return true;
                        case 3:
                            long _arg03 = data.readLong();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            int _arg8 = data.readInt();
                            int _arg9 = data.readInt();
                            int _arg10 = data.readInt();
                            String _arg11 = data.readString();
                            int _arg12 = data.readInt();
                            boolean _arg13 = data.readInt() != 0;
                            boolean _arg14 = data.readInt() != 0;
                            int _arg15 = data.readInt();
                            boolean _arg16 = data.readInt() != 0;
                            boolean _arg17 = data.readInt() != 0;
                            OnGraphicsUpdateMultiWindow(_arg03, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10, _arg11, _arg12, _arg13, _arg14, _arg15, _arg16, _arg17);
                            reply.writeNoException();
                            return true;
                        case 4:
                            long _arg04 = data.readLong();
                            int _arg18 = data.readInt();
                            int _arg22 = data.readInt();
                            onWindowClosed(_arg04, _arg18, _arg22);
                            reply.writeNoException();
                            return true;
                        case 5:
                            long _arg05 = data.readLong();
                            int _arg19 = data.readInt();
                            _arg0 = data.readInt() != 0;
                            OnMinimizeRequested(_arg05, _arg19, _arg0);
                            reply.writeNoException();
                            return true;
                        case 6:
                            _arg0 = data.readInt() != 0;
                            inputMethodActivate(_arg0);
                            reply.writeNoException();
                            return true;
                        case 7:
                            long _arg06 = data.readLong();
                            int _arg110 = data.readInt();
                            int _arg23 = data.readInt();
                            int _arg32 = data.readInt();
                            int _arg42 = data.readInt();
                            Bitmap _arg52 = (Bitmap) _Parcel.readTypedObject(data, Bitmap.CREATOR);
                            OnUpdatePointerIcon(_arg06, _arg110, _arg23, _arg32, _arg42, _arg52);
                            reply.writeNoException();
                            return true;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg111 = data.readInt();
                            int _arg24 = data.readInt();
                            int _arg33 = data.readInt();
                            int _arg43 = data.readInt();
                            UpdateCursorRect(_arg07, _arg111, _arg24, _arg33, _arg43);
                            reply.writeNoException();
                            return true;
                        case 9:
                            long _arg08 = data.readLong();
                            int _arg112 = data.readInt();
                            int _arg25 = data.readInt();
                            onWindowResize(_arg08, _arg112, _arg25);
                            reply.writeNoException();
                            return true;
                        case 10:
                            long _arg09 = data.readLong();
                            int _arg113 = data.readInt();
                            appFinishandExit(_arg09, _arg113);
                            reply.writeNoException();
                            return true;
                        case 11:
                            OnDeleteOptimg(data.readInt());
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes6.dex */
        private static class Proxy implements IAppClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppClient.DESCRIPTOR;
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void updateSession(SessionState session) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _Parcel.writeTypedObject(_data, session, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void OnRailChannelReady(boolean ready) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeInt(ready ? 1 : 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeInt(windowId);
                } catch (Throwable th2) {
                    _reply.recycle();
                    _data.recycle();
                    throw th2;
                }
                try {
                    _data.writeInt(x);
                } catch (Throwable th3) {
                    _reply.recycle();
                    _data.recycle();
                    throw th3;
                }
                try {
                    _data.writeInt(y);
                } catch (Throwable th4) {
                    _reply.recycle();
                    _data.recycle();
                    throw th4;
                }
                try {
                    _data.writeInt(b_width);
                } catch (Throwable th5) {
                    _reply.recycle();
                    _data.recycle();
                    throw th5;
                }
                try {
                    _data.writeInt(b_height);
                } catch (Throwable th6) {
                    _reply.recycle();
                    _data.recycle();
                    throw th6;
                }
                try {
                    _data.writeInt(left);
                } catch (Throwable th7) {
                    _reply.recycle();
                    _data.recycle();
                    throw th7;
                }
                try {
                    _data.writeInt(top);
                    try {
                        _data.writeInt(dirty_w);
                    } catch (Throwable th8) {
                        _reply.recycle();
                        _data.recycle();
                        throw th8;
                    }
                    try {
                        _data.writeInt(dirty_h);
                    } catch (Throwable th9) {
                        _reply.recycle();
                        _data.recycle();
                        throw th9;
                    }
                    try {
                        _data.writeInt(stride);
                    } catch (Throwable th10) {
                        _reply.recycle();
                        _data.recycle();
                        throw th10;
                    }
                    try {
                        _data.writeString(file_name);
                        _data.writeInt(size);
                        _data.writeInt(isPopWindow ? 1 : 0);
                        _data.writeInt(isAlpha ? 1 : 0);
                        _data.writeInt(appType);
                        _data.writeInt(isModal ? 1 : 0);
                        _data.writeInt(isMaximized ? 1 : 0);
                        this.mRemote.transact(3, _data, _reply, 0);
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    } catch (Throwable th11) {
                        _reply.recycle();
                        _data.recycle();
                        throw th11;
                    }
                } catch (Throwable th12) {
                    _reply.recycle();
                    _data.recycle();
                    throw th12;
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void onWindowClosed(long inst, int windowId, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(windowId);
                    _data.writeInt(appType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void OnMinimizeRequested(long inst, int appType, boolean minimized) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(appType);
                    _data.writeInt(minimized ? 1 : 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void inputMethodActivate(boolean activate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeInt(activate ? 1 : 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY, Bitmap bitmap) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(hotSpotX);
                    _data.writeInt(hotSpotY);
                    _Parcel.writeTypedObject(_data, bitmap, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void UpdateCursorRect(int appType, int left, int top, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeInt(appType);
                    _data.writeInt(left);
                    _data.writeInt(top);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void onWindowResize(long inst, int windowId, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(windowId);
                    _data.writeInt(appType);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void appFinishandExit(long inst, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(appType);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
            public void OnDeleteOptimg(int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppClient.DESCRIPTOR);
                    _data.writeInt(appType);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }

    /* loaded from: classes6.dex */
    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> c) {
            if (parcel.readInt() != 0) {
                return c.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T value, int parcelableFlags) {
            if (value != null) {
                parcel.writeInt(1);
                value.writeToParcel(parcel, parcelableFlags);
            } else {
                parcel.writeInt(0);
            }
        }
    }
}