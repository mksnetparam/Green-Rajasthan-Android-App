package greenbharat.cdac.com.greenbharat.pojo;

import java.io.Serializable;

/**
 * Created by CDAC on 4/25/2017.
 */

public class PlantGrowthImagePojo implements Serializable {
    private String plantation_id="",image="", capture_date="", due_date= "", is_expired="", plantation_date = "";

    public String getPlantation_date() {
        return plantation_date;
    }

    public void setPlantation_date(String plantation_date) {
        this.plantation_date = plantation_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getIs_expired() {
        return is_expired;
    }

    public void setIs_expired(String is_expired) {
        this.is_expired = is_expired;
    }

    public String getPlantation_id() {
        return plantation_id;
    }

    public void setPlantation_id(String plantation_id) {
        this.plantation_id = plantation_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCapture_date() {
        return capture_date;
    }

    public void setCapture_date(String capture_date) {
        this.capture_date = capture_date;
    }
}
