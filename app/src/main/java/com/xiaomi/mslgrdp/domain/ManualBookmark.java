package com.xiaomi.mslgrdp.domain;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes5.dex */
public class ManualBookmark extends BookmarkBase {
    public static final Parcelable.Creator<ManualBookmark> CREATOR = new Parcelable.Creator<ManualBookmark>() { 
        
        @Override
        public ManualBookmark createFromParcel(Parcel in) {
            return new ManualBookmark(in);
        }

        
        @Override
        public ManualBookmark[] newArray(int size) {
            return new ManualBookmark[size];
        }
    };
    private boolean enableGatewaySettings;
    private GatewaySettings gatewaySettings;
    private String hostname;
    private int port;

    public ManualBookmark(Parcel parcel) {
        super(parcel);
        this.type = 1;
        this.hostname = parcel.readString();
        this.port = parcel.readInt();
        this.enableGatewaySettings = parcel.readInt() == 1;
        this.gatewaySettings = (GatewaySettings) parcel.readParcelable(GatewaySettings.class.getClassLoader());
    }

    public ManualBookmark() {
        init();
    }

    private void init() {
        this.type = 1;
        this.hostname = "";
        this.port = 3389;
        this.enableGatewaySettings = false;
        this.gatewaySettings = new GatewaySettings();
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getEnableGatewaySettings() {
        return this.enableGatewaySettings;
    }

    public void setEnableGatewaySettings(boolean enableGatewaySettings) {
        this.enableGatewaySettings = enableGatewaySettings;
    }

    public GatewaySettings getGatewaySettings() {
        return this.gatewaySettings;
    }

    public void setGatewaySettings(GatewaySettings gatewaySettings) {
        this.gatewaySettings = gatewaySettings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.hostname);
        out.writeInt(this.port);
        out.writeInt(this.enableGatewaySettings ? 1 : 0);
        out.writeParcelable(this.gatewaySettings, flags);
    }

    @Override
    public void writeToSharedPreferences(SharedPreferences sharedPrefs) {
        super.writeToSharedPreferences(sharedPrefs);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("bookmark.hostname", this.hostname);
        editor.putInt("bookmark.port", this.port);
        editor.putBoolean("bookmark.enable_gateway_settings", this.enableGatewaySettings);
        editor.putString("bookmark.gateway_hostname", this.gatewaySettings.getHostname());
        editor.putInt("bookmark.gateway_port", this.gatewaySettings.getPort());
        editor.putString("bookmark.gateway_username", this.gatewaySettings.getUsername());
        editor.putString("bookmark.gateway_password", this.gatewaySettings.getPassword());
        editor.putString("bookmark.gateway_domain", this.gatewaySettings.getDomain());
        editor.commit();
    }

    @Override
    public void readFromSharedPreferences(SharedPreferences sharedPrefs) {
        super.readFromSharedPreferences(sharedPrefs);
        this.hostname = sharedPrefs.getString("bookmark.hostname", "");
        this.port = sharedPrefs.getInt("bookmark.port", 3389);
        this.enableGatewaySettings = sharedPrefs.getBoolean("bookmark.enable_gateway_settings", false);
        this.gatewaySettings.setHostname(sharedPrefs.getString("bookmark.gateway_hostname", ""));
        this.gatewaySettings.setPort(sharedPrefs.getInt("bookmark.gateway_port", 443));
        this.gatewaySettings.setUsername(sharedPrefs.getString("bookmark.gateway_username", ""));
        this.gatewaySettings.setPassword(sharedPrefs.getString("bookmark.gateway_password", ""));
        this.gatewaySettings.setDomain(sharedPrefs.getString("bookmark.gateway_domain", ""));
    }

    @Override
    public Object clone() {
        return super.clone();
    }


    public static class GatewaySettings implements Parcelable {
        public static final Parcelable.Creator<GatewaySettings> CREATOR = new Parcelable.Creator<GatewaySettings>() { 
            @Override
            public GatewaySettings createFromParcel(Parcel in) {
                return new GatewaySettings(in);
            }

            @Override
            public GatewaySettings[] newArray(int size) {
                return new GatewaySettings[size];
            }
        };
        private String domain;
        private String hostname;
        private String password;
        private int port;
        private String username;

        public GatewaySettings() {
            this.hostname = "";
            this.port = 443;
            this.username = "";
            this.password = "";
            this.domain = "";
        }

        public GatewaySettings(Parcel parcel) {
            this.hostname = parcel.readString();
            this.port = parcel.readInt();
            this.username = parcel.readString();
            this.password = parcel.readString();
            this.domain = parcel.readString();
        }

        public String getHostname() {
            return this.hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDomain() {
            return this.domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.hostname);
            out.writeInt(this.port);
            out.writeString(this.username);
            out.writeString(this.password);
            out.writeString(this.domain);
        }
    }
}