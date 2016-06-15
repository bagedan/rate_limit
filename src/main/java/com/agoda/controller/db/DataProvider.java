package com.agoda.controller.db;

import com.agoda.controller.db.entity.HotelEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by Tkachi on 6/15/2016.
 */
public interface DataProvider {

    public Collection<HotelEntity> getByCityId(String cityName);

    public List<HotelEntity> getByCityId(String cityId, String priceSortOrder);
}
