package hu.nje.plantcare.api;

import java.util.List;

import hu.nje.plantcare.database.entity.BasicPlant;

public interface SearchResultCallBack {
    void onResult(List<BasicPlant> results);
}
