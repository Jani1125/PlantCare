package hu.nje.plantcare.database.entity;

public class BasicPlant {
    private final int id;
    private String common_name;
    private String scientific_name;
    private String imgUrl;

    public BasicPlant(int id, String common_name, String scientific_name, String imgUrl) {
        this.id = id;
        this.common_name = common_name;
        this.scientific_name = scientific_name;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }

    public String getScientific_name() {
        return scientific_name;
    }

    public void setScientific_name(String scientific_name) {
        this.scientific_name = scientific_name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
