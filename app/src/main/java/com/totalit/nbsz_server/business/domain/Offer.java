package com.totalit.nbsz_server.business.domain;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.totalit.nbsz_server.business.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tasu on 9/15/17.
 */
@Table(name = "offer")
public class Offer extends Model {

    @Expose
    @SerializedName("id")
    @Column(name = "server_id")
    public Long serverId;

    @Column(name = "offer_date")
    public Date offerDate;

    @Expose
    public String offer;

    @Expose
    @Column(name = "user")
    public User user;

    @Expose
    @Column(name = "person")
    public Donor person;

    @Expose
    @Column(name = "collect_site")
    public CollectSite collectSite;

    @Expose
    @Column(name = "donation_type")
    public DonationType donationType;

    @Expose
    @Column(name = "donation_kind")
    public String donationKind;

    @Expose
    @Column(name = "check_up")
    public String checkUp;

    @Expose
    @Column(name = "phlebotomy")
    public String phlebotomy;

    @Expose
    @Column(name = "donation_number")
    public String donationNumber;

    @Expose
    @Column(name = "directed")
    public String directed;

    @Expose
    @Column(name = "deferred_reason")
    public DeferredReason defferredReason;

    @Expose
    @Column(name = "centre")
    public Centre centre;


    @Column(name = "defer_date")
    public Date deferDate;

    @Expose
    public String defer;

    @Expose
    @Column(name = "donor_age")
    public Integer donorAge;

    @Column(name="offer_time")
    @Expose
    public String offerTime;

    @Column
    @Expose
    public Integer pulse;

    @Expose
    public List<Incentive> incentives;

    public Offer(){
        super();
    }

    public static List<Offer> findByDonor(Donor donor){
        return new Select()
                .from(Offer.class)
                .where("person = ?", donor.getId())
                .execute();
    }

    public static List<Offer> getAll(){
        return new Select()
                .from(Offer.class)
                .execute();
    }

    public static Offer fromJSON(JSONObject object){
        Offer item = new Offer();
        try{
            if( ! object.isNull("id")){
                item.serverId = object.getLong("id");
            }
            item.offerDate = DateUtil.getDateFromString(object.getString("offer"));
            JSONObject user = object.getJSONObject("user");
            item.user = User.findById(user.getLong("id"));
            //JSONObject person = object.getJSONObject("person");
            //item.person = Donor.findByServerId(person.getLong("id"));
            JSONObject site = object.getJSONObject("collectSite");
            item.collectSite = CollectSite.findById(site.getLong("id"));
            if(! object.isNull("donationType")){
                JSONObject donationType = object.getJSONObject("donationType");
                item.donationType = DonationType.findById(donationType.getLong("id"));
            }
            item.donationKind = object.getString("donationKind");
            item.checkUp = object.getString("checkUp");
            item.phlebotomy = object.getString("phlebotomy");
            item.donationNumber = object.getString("donationNumber");
            item.directed = object.getString("directed");
            if( ! object.isNull("defferredReason")){
                JSONObject defferredReason = object.getJSONObject("defferredReason");
                item.defferredReason = DeferredReason.findById(defferredReason.getLong("id"));
                item.deferDate = DateUtil.getDateFromString(object.getString("defer"));
            }
            item.donorAge = object.getInt("donorAge");
            JSONArray incentives = object.getJSONArray("incentives");
            ArrayList<Incentive> incentiveList = new ArrayList<>();
            for(int i = 0; i < incentives.length(); i++){
                JSONObject jsonObject = incentives.getJSONObject(i);
                Incentive incentive = Incentive.fromJSON(jsonObject);
                incentiveList.add(incentive);
            }
            item.incentives = incentiveList;
            if( ! object.isNull("pulse")){
                item.pulse = object.getInt("pulse");
            }
            item.offerTime = object.getString("offerTime");
            JSONObject centre = object.getJSONObject("centre");
            item.centre = Centre.findById(centre.getLong("id"));
        }catch (JSONException ex){
            ex.printStackTrace();
            return null;
        }
        return item;
    }
    public static ArrayList<Offer> fromJSON(JSONArray array){
        ArrayList<Offer> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++){
            JSONObject object = null;
            try{
                object = array.getJSONObject(i);
            }catch (JSONException ex){
                ex.printStackTrace();
                continue;
            }
            Offer item = fromJSON(object);
            if(item != null)
                list.add(item);
        }
        return list;
    }
}
