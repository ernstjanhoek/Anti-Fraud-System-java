package antifraud;

public enum LockState {
    LOCK, UNLOCK;
    public boolean isState(LockState value) {
        return this.equals(value);
    }

    @Override
    public String toString() {
        if (this == LockState.LOCK) {
            return "locked";
        } else {
            return "unlocked";
        }
    }
}