public interface interfaceCallbackUI {
    public abstract void markText(int selectedStart, int selectedEnd);

    public abstract void setCurrentText();

    public abstract void setSearchHits(int currHit, int numHits);

    public abstract void undoAvailable(boolean available);

    public abstract void redoAvailable(boolean available);

    public abstract void fileHandlingAvailable(boolean available);

    public abstract void setPanelInfo();

    public abstract void setPanelRows();

    public abstract void actualizeWritable();
}
