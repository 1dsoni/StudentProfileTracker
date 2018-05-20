package com.projects.diwanshusoni.studentprofiletracker.Pojos;

import java.io.Serializable;

/**
 * Created by Diwanshu Soni on 16-09-2017.
 */

public class PojoNotifications implements Serializable {
    private String UID,type, heading, descriptions, usefulLinks, imageUrl;
    private String validUpto;

    public String getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsefulLinks() {
        return usefulLinks;
    }

    public void setUsefulLinks(String usefulLinks) {
        this.usefulLinks = usefulLinks;
    }

    private String postedOn;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
