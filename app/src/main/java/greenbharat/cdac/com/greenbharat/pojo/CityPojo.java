package greenbharat.cdac.com.greenbharat.pojo;

import java.io.Serializable;

/**
 * Created by CDAC on 9/7/2016.
 */
public class CityPojo implements Serializable {

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    private String city_id, city_name;

    @Override
    public String toString() {
        return "CityPojo{" +
                "city_id='" + city_id + '\'' +
                ", city_name='" + city_name + '\'' +
                '}';
    }
}
