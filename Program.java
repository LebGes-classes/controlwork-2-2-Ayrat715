class Program {
    private String channel;
    private BroadcastTime time;
    private String name;

    public Program(String channel, BroadcastTime time, String name) {
        this.channel = channel;
        this.time = time;
        this.name = name;
    }

    public String getChannel() {
        return channel;
    }

    public BroadcastTime getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", channel, time, name);
    }
}