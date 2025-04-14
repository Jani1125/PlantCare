package hu.nje.plantcare.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plants")
public class Plant {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String commonName;
    public String scientificName;
    public String type;
    public String cycle;
    public String watering;
    public String imgUrl;
    public String description;
    public boolean isFavorite;



    public Plant(String commonName, String scientificName, String type, String cycle, String watering, String imgUrl,String description, boolean isFavorite) {
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.type = type;
        this.cycle = cycle;
        this.watering = watering;
        this.imgUrl = imgUrl;
        this.description = description;
        this.isFavorite=isFavorite;

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

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
