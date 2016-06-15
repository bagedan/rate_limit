package com.agoda.controller.db;

import com.agoda.controller.db.entity.HotelEntity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Tkachi on 6/15/2016.
 */
public class DataProviderImplIntegrationTest {

    private final String fileLocation = "hoteldb.csv";

    @Test
    public void should_load_data_from_file() throws IOException {
        DataProvider dataProvider = new DataProviderImpl(fileLocation);
        assertThat(dataProvider.getByCityId("Bangkok").size(), is(7));
    }

    @Test
    public void should_sort_result_asc() throws IOException {
        DataProvider dataProvider = new DataProviderImpl(fileLocation);
        List<HotelEntity> result = dataProvider.getByCityId("Bangkok", "asc");
        assertThat(result, sortedByPriceInProperOrder(priceAscPredicate));
    }

    @Test
    public void should_sort_result_desc() throws IOException {
        DataProvider dataProvider = new DataProviderImpl(fileLocation);
        List<HotelEntity> result = dataProvider.getByCityId("Bangkok", "desc");
        assertThat(result, sortedByPriceInProperOrder(priceDescPredicate));
    }

    private Matcher<? super List<HotelEntity>> sortedByPriceInProperOrder(BiPredicate sortingPredicate) {
        return new BaseMatcher<Collection<HotelEntity>>() {
            @Override
            public boolean matches(Object o) {
                List<HotelEntity> hotels = (List<HotelEntity>) o;
                for (int i = 1; i < hotels.size(); i++) {
                    return sortingPredicate.test(hotels.get(i - 1).getRoomPrice(), hotels.get(i).getRoomPrice());
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Result should contain hotels sort by price in ascending order");
            }
        };
    }

    private BiPredicate<Double, Double> priceAscPredicate = (o, o2) -> (Double) o < (Double) o2;
    private BiPredicate<Double, Double> priceDescPredicate = (o, o2) -> (Double) o > (Double) o2;

}