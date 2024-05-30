abstract class Bridge {
    Bridge other = null;

    abstract void receive_bridge_event(int header, byte[] data);
    
    void set_bridge(Bridge other) {
        this.other = other;
    }

    void send_bridge_event(int header, byte[] data) {
        if (this.other != null) this.other.receive_bridge_event(header, data);
    }
}
