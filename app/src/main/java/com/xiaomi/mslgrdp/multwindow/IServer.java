package com.xiaomi.mslgrdp.multwindow;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaomi.mslgrdp.multwindow.IAppClient;

/* loaded from: classes6.dex */
public interface IServer extends IInterface {
    public static final String DESCRIPTOR = "com.xiaomi.mslgrdp.multwindow.IServer";

    void activityOnResumed(long j, boolean z) throws RemoteException;

    void addClient(IAppClient iAppClient, int i) throws RemoteException;

    void backgroundOrNot(boolean z, int i) throws RemoteException;

    void removeClient(IAppClient iAppClient, int i) throws RemoteException;

    boolean sendCursorEvent(long j, int i, int i2, int i3) throws RemoteException;

    boolean sendKeyEvent(long j, int i, boolean z) throws RemoteException;

    void sendKillAppProcess(long j, int i) throws RemoteException;

    void sendStringTo(String str) throws RemoteException;

    void sendTypeUrl(long j, String str, String str2) throws RemoteException;

    boolean sendUnicodeKeyEvent(long j, int i, boolean z) throws RemoteException;

    void sendWindowEvent(long j, int i, int i2) throws RemoteException;

    void sendWindowFocusEvent(long j, int i, boolean z, int i2) throws RemoteException;

    void setTypeUrl(String str, String str2) throws RemoteException;

    /* loaded from: classes6.dex */
    public static class Default implements IServer {
        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void addClient(IAppClient client, int type) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void removeClient(IAppClient client, int type) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendCursorEvent(long inst, int x, int y, int flags) throws RemoteException {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendTypeUrl(long inst, String url, String app) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void setTypeUrl(String url, String app) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendStringTo(String data) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendWindowEvent(long inst, int windId, int cmdId) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendWindowFocusEvent(long inst, int windId, boolean focus, int appType) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendKillAppProcess(long inst, int appType) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void activityOnResumed(long inst, boolean resume) throws RemoteException {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void backgroundOrNot(boolean bg, int appType) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes6.dex */
    public static abstract class Stub extends Binder implements IServer {
        static final int TRANSACTION_activityOnResumed = 12;
        static final int TRANSACTION_addClient = 1;
        static final int TRANSACTION_backgroundOrNot = 13;
        static final int TRANSACTION_removeClient = 2;
        static final int TRANSACTION_sendCursorEvent = 3;
        static final int TRANSACTION_sendKeyEvent = 4;
        static final int TRANSACTION_sendKillAppProcess = 11;
        static final int TRANSACTION_sendStringTo = 8;
        static final int TRANSACTION_sendTypeUrl = 6;
        static final int TRANSACTION_sendUnicodeKeyEvent = 5;
        static final int TRANSACTION_sendWindowEvent = 9;
        static final int TRANSACTION_sendWindowFocusEvent = 10;
        static final int TRANSACTION_setTypeUrl = 7;

        public Stub() {
            attachInterface(this, IServer.DESCRIPTOR);
        }

        public static IServer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IServer.DESCRIPTOR);
            if (iin != null && (iin instanceof IServer)) {
                return (IServer) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            boolean z;
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IServer.DESCRIPTOR);
            }
            switch (i) {
                case 1598968902:
                    parcel2.writeString(IServer.DESCRIPTOR);
                    return true;
                default:
                    boolean z2 = false;
                    switch (i) {
                        case 1:
                            addClient(IAppClient.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 2:
                            removeClient(IAppClient.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 3:
                            boolean sendCursorEvent = sendCursorEvent(parcel.readLong(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeInt(sendCursorEvent ? 1 : 0);
                            return true;
                        case 4:
                            long readLong = parcel.readLong();
                            int readInt = parcel.readInt();
                            if (parcel.readInt() != 0) {
                                z2 = true;
                            }
                            boolean sendKeyEvent = sendKeyEvent(readLong, readInt, z2);
                            parcel2.writeNoException();
                            parcel2.writeInt(sendKeyEvent ? 1 : 0);
                            return true;
                        case 5:
                            long readLong2 = parcel.readLong();
                            int readInt2 = parcel.readInt();
                            if (parcel.readInt() != 0) {
                                z2 = true;
                            }
                            boolean sendUnicodeKeyEvent = sendUnicodeKeyEvent(readLong2, readInt2, z2);
                            parcel2.writeNoException();
                            parcel2.writeInt(sendUnicodeKeyEvent ? 1 : 0);
                            return true;
                        case 6:
                            sendTypeUrl(parcel.readLong(), parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 7:
                            setTypeUrl(parcel.readString(), parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 8:
                            sendStringTo(parcel.readString());
                            parcel2.writeNoException();
                            return true;
                        case 9:
                            sendWindowEvent(parcel.readLong(), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 10:
                            long readLong3 = parcel.readLong();
                            int readInt3 = parcel.readInt();
                            if (parcel.readInt() != 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            sendWindowFocusEvent(readLong3, readInt3, z, parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 11:
                            sendKillAppProcess(parcel.readLong(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 12:
                            long readLong4 = parcel.readLong();
                            if (parcel.readInt() != 0) {
                                z2 = true;
                            }
                            activityOnResumed(readLong4, z2);
                            parcel2.writeNoException();
                            return true;
                        case 13:
                            if (parcel.readInt() != 0) {
                                z2 = true;
                            }
                            backgroundOrNot(z2, parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
            }
        }

        /* loaded from: classes6.dex */
        private static class Proxy implements IServer {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IServer.DESCRIPTOR;
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void addClient(IAppClient client, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(type);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void removeClient(IAppClient client, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(type);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public boolean sendCursorEvent(long inst, int x, int y, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _status = _reply.readInt() != 0;
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public boolean sendKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(keycode);
                    _data.writeInt(down ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(keycode);
                    _data.writeInt(down ? 1 : 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void sendTypeUrl(long inst, String url, String app) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeString(url);
                    _data.writeString(app);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void setTypeUrl(String url, String app) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeString(app);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void sendStringTo(String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeString(data);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void sendWindowEvent(long inst, int windId, int cmdId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(windId);
                    _data.writeInt(cmdId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void sendWindowFocusEvent(long inst, int windId, boolean focus, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(windId);
                    _data.writeInt(focus ? 1 : 0);
                    _data.writeInt(appType);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void sendKillAppProcess(long inst, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(appType);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void activityOnResumed(long inst, boolean resume) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeLong(inst);
                    _data.writeInt(resume ? 1 : 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaomi.mslgrdp.multwindow.IServer
            public void backgroundOrNot(boolean bg, int appType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServer.DESCRIPTOR);
                    _data.writeInt(bg ? 1 : 0);
                    _data.writeInt(appType);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}