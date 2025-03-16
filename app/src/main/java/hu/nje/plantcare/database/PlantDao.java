package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlantDao {
    @Insert
    void insert(Plant plant);

    @Query("SELECT * FROM plants")
    List<Plant> getAllPlants();

    @Query("DELETE FROM plants")
    void deleteAll();
}

