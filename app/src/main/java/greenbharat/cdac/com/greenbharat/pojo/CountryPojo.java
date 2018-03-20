package greenbharat.cdac.com.greenbharat.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo1 on 9/7/2016.
 */
public class CountryPojo implements Serializable {

  private   String country_id, country_name;
   private ArrayList<StatePojo> statePojoArrayList = new ArrayList<StatePojo>();

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public ArrayList<StatePojo> getStatePojoArrayList() {
        return statePojoArrayList;
    }

    public void setStatePojoArrayList(ArrayList<StatePojo> statePojoArrayList) {
        this.statePojoArrayList = statePojoArrayList;
    }

    @Override
    public String toString() {
        return "CountryPojo{" +
                "country_id='" + country_id + '\'' +
                ", country_name='" + country_name + '\'' +
                ", statePojoArrayList=" + statePojoArrayList +
                '}';
    }
}
