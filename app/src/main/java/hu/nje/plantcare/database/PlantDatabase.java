package hu.nje.plantcare.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import hu.nje.plantcare.database.entity.OwnPlant;
import hu.nje.plantcare.database.entity.Plant;

// **Room adatbázis osztály deklarálása**
@Database(entities = {Plant.class, OwnPlant.class}, version = 2)
public abstract class PlantDatabase extends RoomDatabase {
    // **DAO (Data Access Object) elérése**
    // Ezekkel az absztrakt metódussokkal kérhető le a PlantDao és az OwnPlantDao példánya.
    public abstract PlantDao plantDao();
    public abstract  OwnPlantDao ownPlantDao();


    // **Singleton példány tárolása**
    // Az INSTANCE változó az adatbázis egyetlen példányát tárolja.
    // `volatile` biztosítja, hogy a változás azonnal látható legyen más szálakon is.
    private static volatile PlantDatabase INSTANCE;

    // **Singleton példány létrehozása és elérése**
    // Ez a metódus biztosítja, hogy az adatbázisnak csak egy példánya legyen (Singleton).
    public static PlantDatabase getDatabase(final Context context) {
        // Ha az INSTANCE még nem létezik, létrehozzuk.
        if (INSTANCE == null) {
            synchronized (PlantDatabase.class) {  // Szinkronizáció a többszálúság miatt.
                if (INSTANCE == null) {  // Újra ellenőrizzük, hogy nincs-e már példány.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PlantDatabase.class, "plant_database")  // Adatbázis építő létrehozása.
                            .fallbackToDestructiveMigration()  // Ha a séma változik, törli és újraépíti az adatbázist.
                            .build();  // Adatbázis példány létrehozása.
                }
            }
        }
        return INSTANCE;  // Visszaadja az adatbázis példányt.
    }
}

