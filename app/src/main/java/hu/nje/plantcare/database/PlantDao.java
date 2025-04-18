package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hu.nje.plantcare.database.entity.Plant;

@Dao
public interface PlantDao {
    @Insert
    void insert(Plant plant);

    @Query("SELECT * FROM plants")
    List<Plant> getAllPlants();

    @Query("DELETE FROM plants")
    void deleteAll();




    //void setFavorite(int id, boolean isFavorite);

    //@Query("UPDATE plants SET isFavorite = :isFavorite WHERE id = :id")
    //void setFavorite(int id, boolean isFavorite);  // Módszer a kedvenc beállításához
}

