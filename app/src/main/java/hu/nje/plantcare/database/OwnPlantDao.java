package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.nje.plantcare.database.entity.OwnPlant;

@Dao
public interface OwnPlantDao {
    @Insert
    long insertOwnPlant(OwnPlant ownPlant); // Visszatérési típus long-ra változtatva

    @Query("SELECT * FROM own_plants")
    List<OwnPlant> getAllOwnPlants();

    @Query("SELECT * FROM own_plants WHERE id = :selectedPlantId")
    OwnPlant getOwnPlant(int selectedPlantId);

    @Query("DELETE FROM own_plants")
    void deleteAllOwnPlants();

    @Query("DELETE FROM own_plants WHERE id = :id")
    void deletePlant(int id);

    // Új metódus a növény ID-k lekérdezéséhez:
    @Query("SELECT id FROM own_plants")
    List<Integer> getAllOwnPlantIds();
}