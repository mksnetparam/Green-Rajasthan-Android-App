package greenbharat.cdac.com.greenbharat.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo1 on 9/7/2016.
 */
public class StatePojo implements Serializable {

    private String state_id, state_name;

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public ArrayList<CityPojo> getCityPojoArrayList() {
        return cityPojoArrayList;
    }

    public void setCityPojoArrayList(ArrayList<CityPojo> cityPojoArrayList) {
        this.cityPojoArrayList = cityPojoArrayList;
    }

    ArrayList<CityPojo> cityPojoArrayList = new ArrayList<CityPojo>();

    @Override
    public String toString() {
        return "StatePojo{" +
                "state_id='" + state_id + '\'' +
                ", state_name='" + state_name + '\'' +
                ", cityPojoArrayList=" + cityPojoArrayList +
                '}';
    }
}
