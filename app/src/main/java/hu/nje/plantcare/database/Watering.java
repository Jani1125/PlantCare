package hu.nje.plantcare.database;

public enum Watering {
    FREQUENT("frequent", 1,3),
    AVERAGE("average", 4,6),
    MINIMUM("minimum", 7,14),
    NONE("none", 15,25);

    private final String apiValue;
    private final int minDays;
    private final int maxDays;

    Watering(String apiValue, int minDays, int maxDays) {
        this.apiValue = apiValue;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public String getApiValue() {
        return apiValue;
    }

    public int getMinDays() {
        return minDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public static Watering getWateringInDays(String apiValue) {
        for (Watering freq : values()) {
            if (freq.apiValue.equalsIgnoreCase(apiValue)) {
                return freq;
            }
        }
        throw new IllegalArgumentException("Unknown watering value: " + apiValue);
    }
}
