package com.agoda.controller.db;

import com.agoda.controller.db.entity.HotelEntity;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tkachi on 6/15/2016.
 */
public class DataProviderImpl implements DataProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(DataProviderImpl.class);
    private final static String CITY_HEADER = "CITY";
    private final static String HOTEL_ID_HEADER = "HOTELID";
    private final static String ROOM_TYPE_HEADER = "ROOM";
    private final static String PRICE_HEADER = "PRICE";

    private enum ORDER {ASC, DESC};

    private ArrayListMultimap<String, HotelEntity> hotelsByCity = ArrayListMultimap.create();

    public DataProviderImpl(String csvFileLocation) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(csvFileLocation).getFile());


        Iterable<CSVRecord> records = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.withHeader());
        for (CSVRecord record : records) {
            String cityName = record.get(CITY_HEADER);
            HotelEntity hotelEntity = new HotelEntity();
            hotelEntity.setCityName(cityName);
            hotelEntity.setHotelId(Integer.parseInt(record.get(HOTEL_ID_HEADER)));
            hotelEntity.setRoomPrice(Double.parseDouble(record.get(PRICE_HEADER)));
            hotelEntity.setRoomType(record.get(ROOM_TYPE_HEADER));

            hotelsByCity.put(cityName, hotelEntity);
            LOGGER.debug("New hotel added: [" + hotelEntity + "]");
        }
    }

    public Collection<HotelEntity> getByCityId(String cityId) {
        return hotelsByCity.get(cityId);
    }

    public List<HotelEntity> getByCityId(String cityId, String priceSortOrder) {
        List<HotelEntity> result = hotelsByCity.get(cityId);
        try {
            ORDER sortOrder = ORDER.valueOf(priceSortOrder.toUpperCase());
            return sortResult(result, sortOrder);
        } catch (IllegalArgumentException iae) {
            LOGGER.error("Wrong parameter for sorting [{}]. Supported are [{}]. Return unsorted result", priceSortOrder, ORDER.values());
            return result;
        }
    }

    private List<HotelEntity> sortResult(List<HotelEntity> result, ORDER sortOrder) {
        if (sortOrder == ORDER.ASC) {
            result.sort((o1, o2) -> (int) (o1.getRoomPrice() - o2.getRoomPrice()));
        } else {
            result.sort((o1, o2) -> (int) (o2.getRoomPrice() - o1.getRoomPrice()));
        }
        return result;
    }
}
