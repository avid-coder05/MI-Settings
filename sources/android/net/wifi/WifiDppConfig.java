package android.net.wifi;

/* loaded from: classes.dex */
public class WifiDppConfig {
    private DppResult mEventResult = new DppResult();
    public int peer_bootstrap_id = -1;
    public int own_bootstrap_id = -1;
    public int dpp_role = -1;
    public int isAp = -1;
    public int isDpp = -1;
    public int conf_id = -1;
    public int bootstrap_type = -1;
    public int expiry = 0;
    public String ssid = null;
    public String passphrase = null;
    public String chan_list = null;
    public String mac_addr = null;
    public String info = null;
    public String curve = null;
    public String key = null;

    /* loaded from: classes.dex */
    public static class DppResult {
        public boolean initiator = false;
        public int netID = -1;
        public byte capab = 0;
        public byte authMissingParam = 0;
        public byte configEventType = 0;
        public String iBootstrapData = null;
        public String ssid = null;
        public String connector = null;
        public String cSignKey = null;
        public String netAccessKey = null;
        public int netAccessKeyExpiry = 0;
        public String passphrase = null;
        public String psk = null;
    }

    public DppResult getDppResult() {
        return this.mEventResult;
    }
}
