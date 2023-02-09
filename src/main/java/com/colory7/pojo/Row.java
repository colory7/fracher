package com.colory7.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Row {
    @JsonProperty(value = "person_id")
    private String personId;

    private String score;
    @JsonProperty(value = "location_info")

    private String locationInfo;

    private Location location;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
