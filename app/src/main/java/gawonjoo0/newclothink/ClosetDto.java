package gawonjoo0.newclothink;

/**
 * Created by USER on 2016-10-22.
 */
public class ClosetDto {
    private String name,arduino;
    private int fur, leather, silk, knit;

    public void setName(String name) {
        this.name = name;
    }
    public void setFur(int fur) {
        this.fur = fur;
    }
    public void setLeather(int leather) {
        this.leather = leather;
    }
    public void setSilk(int silk) {
        this.silk = silk;
    }
    public void setKnit(int knit) {
        this.knit = knit;
    }
    public void setArduino(String arduino){this.arduino=arduino; }

    public String getName() {
        return name;
    }
    public int getFur() {
        return fur;
    }
    public int getLeather() {
        return leather;
    }
    public int getSilk() {
        return silk;
    }
    public int getKnit() {
        return knit;
    }
    public String getArduino() { return arduino; }

}
