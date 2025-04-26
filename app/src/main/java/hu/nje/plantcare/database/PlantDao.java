package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.DeleteColumn;
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

    @Query("DELETE FROM plants WHERE plantId = :plantId")
    void deleteOne(int plantId);
    @Query("SELECT * FROM plants WHERE plantId = :plantId")
    Plant getPlant(int plantId);
}

