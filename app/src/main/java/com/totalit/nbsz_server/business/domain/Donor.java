package com.totalit.nbsz_server.business.domain;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.totalit.nbsz_server.business.domain.util.*;
import com.totalit.nbsz_server.business.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by tasu on 6/5/17.
 */
@Table(name = "person")
public class Donor extends Model implements Serializable {

    @Expose
    @Column(name = "server_id")
    @SerializedName("id")
    public Long server_id;

    @Expose
    @Column
    public String localId;

    @Expose
    @Column(name = "profession")
    public Profession profession;
    @Expose
    @Column(name = "marital_status")
    public MaritalStatus maritalStatus;
    @Expose
    @Column(name = "city")
    public Centre city;

    @Expose
    @Column(name = "first_name")
    public String firstName;

    @Expose
    @Column(name = "surname")
    public String surname;

    @Expose
    @Column(name = "id_number")
    public String idNumber;

    @Expose
    @SerializedName("gender")
    public String genderValue;

    @Column(name = "gender")
    public Gender gender;

    @Column(name = "date_of_birth")
    public Date dateOfBirth;

    @Expose
    @SerializedName("dob")
    @Column(name = "dob")
    public String dob;

    @Expose
    @Column(name = "defer_date")
    public String deferDate;

    @Expose
    @Column(name = "home_telephone")
    public String homeTelephone;

    @Expose
    @Column(name = "work_telephone")
    public String workTelephone;

    @Expose
    @Column(name = "cellphoneNumber")
    public String cellphoneNumber;

    @Expose
    @Column(name = "residentialAddress")
    public String residentialAddress;

    @Expose
    @Column(name = "email")
    public String email;

    @Expose
    @Column(name = "donor_number")
    public String donorNumber;

    @Expose
    @Column(name = "bled_by")
    public User bledBy;

    @Expose
    @Column(name = "counsellor")
    public Counsellor counsellor;

    @Column
    @Expose
    public Integer deferPeriod;
    @Expose
    @Column(name = "reason_for_deferring")
    public DeferredReason deferredReason;

    @Expose
    @Column(name = "collect_site")
    public CollectSite collectSite;

    @Expose
    @Column(name = "donation_type")
    public DonationType donationType;

    @Column(name = "entry_date")
    public Date entryDate;

    @Expose
    @Column(name = "entry")
    public String entry;

    @Expose
    @SerializedName("timeEntry")
    @Column(name = "entry_time")
    public String entryTime;

    @Expose
    @Column
    public Integer pushed = 0;

    @Column
    public Integer isNew = 0;

    @Expose
    @Column(name = "defer_notes")
    public String deferNotes;

    @Expose
    @Column(name = "blood_group")
    public String bloodGroup;

    public Integer pulse;

    @Column
    @Expose
    public String accepted;

    public ArrayList<Long> incentives;

    @Column
    @Expose
    public Integer numberOfDonations;

    @Expose
    @Column
    public DonateDefer donateDefer;

    public List<SpecialNotes> specialNotes;

    //@Expose
    public List<Donation> donations;

    @Expose
    public List<DonationStats> donationStats;

    @Expose
    public List<Offer> offers;

    public String requestType;



    public Donor(){
        super();
    }

    public static List<Donor> getAll(){
        return new Select()
                .from(Donor.class)
                .execute();
    }

    public static Donor findById(Long id){
        return new Select()
                .from(Donor.class)
                .where("Id = ?", id)
                .executeSingle();
    }

    public static Donor findByLocalId(String localId){
        return new Select()
                .from(Donor.class)
                .where("localId = ?", localId)
                .executeSingle();
    }

    public static Donor findByServerId(Long id){
        return new Select()
                .from(Donor.class)
                .where("server_id = ?", id)
                .executeSingle();
    }

    public static Donor findByDonorNumber(String donorNumber){
        return new Select()
                .from(Donor.class)
                .where("donor_number = ?", donorNumber)
                .executeSingle();
    }

    public static Donor findByNationalId(String nationalId){
        return new Select()
                .from(Donor.class)
                .where("id_number = ?", nationalId)
                .executeSingle();
    }


    public static List<Donor> findTodayDonations(String entry){
        return new Select()
                .from(Donor.class)
                .where("entry = ?", entry)
                .execute();
    }

    public static List<Donor> findByFirstNameAndLastNameAndDateOfBirth(String firstName, String surname, String dob){
        return new Select()
                .from(Donor.class)
                .where("first_name = ?", firstName)
                .and("surname = ?", surname)
                .and("dob = ?", dob)
                .execute();
    }

    public static List<Donor> findByLastNameAndDateOfBirth(String surname, String dob){
        return new Select()
                .from(Donor.class)
                .and("surname = ?", surname)
                .and("dob = ?", dob)
                .execute();
    }

    public static List<Donor> findByPushed(){
        return new Select()
                .from(Donor.class)
                .where("pushed = ?", 1)
                .execute();
    }
    public static Donor fromJSON(JSONObject object){
        Donor item = new Donor();
        try{
            item.firstName = object.getString("firstName").toUpperCase().trim();
            item.surname = object.getString("surname").toUpperCase().trim();
            if( ! object.isNull("idNumber")){
                item.idNumber = object.getString("idNumber");
            }
            if( ! object.isNull("localId")){
                item.localId = object.getString("localId");
            }
            if( ! object.isNull("numberOfDonations")){
                item.numberOfDonations = object.getInt("numberOfDonations");
            }

            if( ! object.isNull("gender")){
                if(object.getString("gender").equals("M") || object.getString("gender").equals("F")){
                    item.gender = Gender.valueOf(object.getString("gender"));
                }
            }

            if( ! object.isNull("dob")){
                String dob = object.getString("dob");
                item.dateOfBirth = DateUtil.getFromString(dob);
                item.dob = dob;
            }

            if( ! object.isNull("deferDate")){
                item.deferDate = DateUtil.getStringFromDate(DateUtil.getDateFromString(object.getString("deferDate")));
            }
            if( ! object.isNull("entry")){
                item.entryDate = DateUtil.getDateFromString(object.getString("entry"));
                item.entry = DateUtil.getStringFromDate(item.entryDate);
            }
            if( ! object.isNull("deferNotes")){
                item.deferNotes = object.getString("deferNotes");
            }
            if( ! object.isNull("deferPeriod")){
                item.deferPeriod = object.getInt("deferPeriod");
            }
            if( ! object.isNull("profession")){
                JSONObject profession = object.getJSONObject("profession");
                item.profession = Profession.findById(profession.getLong("id"));
            }
            if( ! object.isNull("maritalStatus")){
                JSONObject maritalStatus = object.getJSONObject("maritalStatus");
                item.maritalStatus = MaritalStatus.findById(maritalStatus.getLong("id"));
            }
            if(! object.isNull("city")){
                JSONObject city = object.getJSONObject("city");
                item.city = Centre.findById(city.getLong("id"));
            }
            item.residentialAddress = object.getString("residentialAddress");
            item.homeTelephone = object.getString("homeTelephone");
            item.workTelephone = object.getString("workTelephone");
            item.cellphoneNumber = object.getString("cellphoneNumber");
            item.email = object.getString("email");
            if( ! object.isNull("counsellor")){
                JSONObject counsellor = object.getJSONObject("counsellor");
                Counsellor c = new Counsellor();
                c.name = counsellor.getString("name");
                c.address = counsellor.getString("address");
                c.phoneNumber = counsellor.getString("phoneNumber");
                //c.code = counsellor.getString("code");
                c.serverId = counsellor.getLong("id");
                Counsellor duplicate = Counsellor.findById(c.serverId);
                if(duplicate == null){
                    c.save();
                }
                Log.d("Saved counsellor", c.name);
                item.counsellor = c;
            }
            if( ! object.isNull("deferredReason")){
                JSONObject deferredReason = object.getJSONObject("deferredReason");
                item.deferredReason = DeferredReason.findById(deferredReason.getLong("id"));
            }
            if( ! object.isNull("collectSite")){
                JSONObject collectSite = object.getJSONObject("collectSite");
                item.collectSite = CollectSite.findById(collectSite.getLong("id"));
            }
            if( ! object.isNull("donationType")){
                JSONObject donationType = object.getJSONObject("donationType");
                item.donationType = DonationType.findById(donationType.getLong("id"));
            }
            if( ! object.isNull("id")){
                item.server_id = object.getLong("id");
            }
            if( ! object.isNull("donorNumber")){
                item.donorNumber = object.getString("donorNumber");
            }
            if( ! object.isNull("donateDefer")){
                item.donateDefer = DonateDefer.valueOf(object.getString("donateDefer"));
            }
            if( ! object.isNull("pushed")){
                item.pushed = object.getInt("pushed");
            }
            if( ! object.isNull("localId")){
                item.localId = object.getString("localId");
            }
            if( ! object.isNull("bledBy")){
                JSONObject user = object.getJSONObject("bledBy");
                item.bledBy = User.findById(user.getLong("id"));
            }
            if( ! object.isNull("accepted")){
                item.accepted = object.getString("accepted");
            }
            if( ! object.isNull("pushed")){
                item.pushed = object.getInt("pushed");
            }

        }catch (JSONException ex){
            ex.printStackTrace();
            return null;
        }
        return item;
    }



    public static ArrayList<Donor> fromJSON(JSONArray array){
        ArrayList<Donor> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++){
            JSONObject object = null;
            try{
                object = array.getJSONObject(i);
            }catch (JSONException ex){
                ex.printStackTrace();
                continue;
            }
            Donor item = fromJSON(object);
            if(item != null)
                list.add(item);
        }
        return list;
    }

    public String toString(){
        return firstName + " " + surname;
    }

}
