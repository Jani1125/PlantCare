package hu.nje.plantcare.database.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "own_plants")
public class OwnPlant {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String commonName;
    private String type;
    private String watering;
    private String imgUrl;
    private String description;




//    public OwnPlant(String commonName, String scientificName, String type, String cycle, String watering, String imgUrl,String description) {
//        this.commonName = commonName;
//        this.scientificName = scientificName;
//        this.type = type;
//        this.cycle = cycle;
//        this.watering = watering;
//        this.imgUrl = imgUrl;
//        this.description = description;
//
//
//    }

    public OwnPlant() {

    }


    // Getterek és setterek
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
