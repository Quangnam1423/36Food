package Manager.Restaurant.mai.entity;

public enum AddressLabel {
    HOME("Home"),
    WORK("Work"),
    OTHER("Other");

    private final String label;

    AddressLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
