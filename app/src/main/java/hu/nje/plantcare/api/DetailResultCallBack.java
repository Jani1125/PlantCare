package hu.nje.plantcare.api;

import hu.nje.plantcare.database.entity.Plant;

public interface DetailResultCallBack {
    void onResult(Plant result);
}
