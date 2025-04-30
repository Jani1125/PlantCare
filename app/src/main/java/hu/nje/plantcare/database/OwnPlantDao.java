package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hu.nje.plantcare.database.entity.OwnPlant;

@Dao
public interface OwnPlantDao {
    @Insert
    void insertOwnPlant(OwnPlant ownPlant);

    @Query("SELECT * FROM own_plants")
    List<OwnPlant> getAllOwnPlants();

    @Query("DELETE FROM own_plants")
    void deleteAllOwnPlants();
}
