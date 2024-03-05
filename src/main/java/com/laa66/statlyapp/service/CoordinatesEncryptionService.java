package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.mapbox.Coordinates;
import org.springframework.data.util.Pair;

public interface CoordinatesEncryptionService {
    Pair<String, String> encrypt(Coordinates coordinates);
    Coordinates decrypt(Pair<String, String> coordinates);
}
