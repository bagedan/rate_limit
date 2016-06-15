package com.agoda.controller;

import com.agoda.controller.db.DataProvider;
import com.agoda.controller.db.entity.HotelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by Tkachi on 6/15/2016.
 */

@RestController
public class AgodaController {
    private final static Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private final DataProvider dataProvider;

    @Autowired
    public AgodaController(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @RequestMapping("/hotels")
    public Collection<HotelEntity> hotels(@RequestParam(value = "cityId") String cityId,
                                          @RequestParam(value = "order", required = false) String priceSortOrder) {
        LOGGER.debug("Request with cityId [{}], order [{}]", cityId, priceSortOrder);

        if (priceSortOrder == null) {
            return dataProvider.getByCityId(cityId);
        } else {
            return dataProvider.getByCityId(cityId, priceSortOrder);
        }

    }

    //returning 404 for security reasons
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private class ControllerException extends RuntimeException {
        public ControllerException(String message) {
            super(message);
        }
    }
}
