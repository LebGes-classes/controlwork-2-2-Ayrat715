class BroadcastTime implements Comparable<BroadcastTime> {
    private int hours;
    private int minutes;

    public BroadcastTime(String time) {
        String[] parts = time.split(":");
        this.hours = Integer.parseInt(parts[0]);
        this.minutes = Integer.parseInt(parts[1]);
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean after(BroadcastTime t) {
        return compareTo(t) > 0;
    }

    public boolean before(BroadcastTime t) {
        return compareTo(t) < 0;
    }

    public boolean between(BroadcastTime t1, BroadcastTime t2) {
        return this.after(t1) && this.before(t2);
    }

    @Override
    public int compareTo(BroadcastTime o) {
        if (this.hours != o.hours) {
            return Integer.compare(this.hours, o.hours);
        }
        return Integer.compare(this.minutes, o.minutes);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }
}