package pl.tymur.wifipicker;

/**
 * Created by user on 11.11.2016.
 */

public class WiFiNetwork {
    private long id;
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private String timestamp;
    private int level;
    private double latitude;
    private double longitude;

    public WiFiNetwork(String bssid, String capabilities, int frequency, long id, double latitude, int level, double longitude, String ssid, String timestamp, boolean triangulated) {
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.frequency = frequency;
        this.id = id;
        this.latitude = latitude;
        this.level = level;
        this.longitude = longitude;
        this.ssid = ssid;
        this.timestamp = timestamp;
        this.triangulated = triangulated;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTriangulated() {
        return triangulated;
    }

    public void setTriangulated(boolean triangulated) {
        this.triangulated = triangulated;
    }

    private boolean triangulated;
}
