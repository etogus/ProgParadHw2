public class Vars {
    private String name;
    private String type;
    private int index;

    public Vars(String name, String type, int index) {
        this.name = name;
        this.type = type;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Vars{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", index=" + index +
                '}';
    }
}
