package com.totalit.nbsz_server.business.domain;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.totalit.nbsz_server.business.domain.util.PackType;
import com.totalit.nbsz_server.business.domain.util.PassFail;
import com.totalit.nbsz_server.business.domain.util.ReasonForTesting;
import com.totalit.nbsz_server.business.domain.util.YesNo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tasu on 7/17/17.
 */
@Table(name = "donation_stats")
public class DonationStats extends Model implements Serializable{

    @Expose
    @Column(name = "server_id")
    @SerializedName("id")
    public Long server_id;

    @Expose
    @Column(name = "feeling_well_today")
    public YesNo feelingWellToday;

    @Expose
    @Column(name = "refused_to_donated")
    public YesNo refusedToDonate;

    @Expose
    @Column(name = "been_to_malaria_area")
    public YesNo beenToMalariaArea;

    @Expose
    @Column(name = "meal_or_snack")
    public YesNo mealOrSnack;

    @Expose
    @Column(name = "dangerous_occupation")
    public YesNo dangerousOccupation;

    @Expose
    @Column(name = "rheumatic_fever")
    public YesNo rheumaticFever;

    @Expose
    @Column(name = "lung_disease")
    public YesNo lungDisease;

    @Expose
    @Column(name = "cancer")
    public YesNo cancer;

    @Expose
    @Column(name = "disease")
    public YesNo diabetes;

    @Expose
    @Column(name = "chronic_medical_condition")
    public YesNo chronicMedicalCondition;

    @Expose
    @Column(name = "been_to_dentist")
    public YesNo beenToDentist;

    @Expose
    @Column(name = "taken_antibiotics")
    public YesNo takenAntibiotics;

    @Expose
    @Column(name = "injection")
    public YesNo injection;

    @Expose
    @Column(name = "been_ill")
    public YesNo beenIll;

    @Expose
    @Column(name = "received_blood_transfusion")
    public YesNo receivedBloodTransfusion;

    @Expose
    @Column(name = "hiv_test")
    public YesNo hivTest;

    @Expose
    @Column(name = "been_tested_for_hiv")
    public YesNo beenTestedForHiv;

    @Expose
    @Column(name = "contact_with_person_with_yellow_jaundice")
    public YesNo contactWithPersonWithYellowJaundice;

    @Expose
    @Column(name = "accidental_exposure_to_blood")
    public YesNo accidentalExposureToBlood;

    @Expose
    @Column(name = "been_tattooed_or_pierced")
    public YesNo beenTattooedOrPierced;

    @Expose
    @Column(name = "injected_with_illegal_drugs")
    public YesNo injectedWithIllegalDrugs;

    @Expose
    @Column(name = "sex_with_someone_with_unknown_background")
    public YesNo sexWithSomeoneWithUnknownBackground;

    @Expose
    @Column(name = "exchanged_money_for_sex")
    public YesNo exchangedMoneyForSex;

    @Expose
    @Column(name = "true_for_sex_partner")
    public YesNo trueForSexPartner;

    @Expose
    @Column(name = "suffered_from_STD")
    public YesNo sufferedFromSTD;

    @Expose
    @Column(name = "contact_with_person_with_hepatitisB")
    public YesNo contactWithPersonWithHepatitisB;

    @Expose
    @Column(name = "suffered_from_night_sweats")
    public YesNo sufferedFromNightSweats;

    @Expose
    @Column(name = "copper_sulphate")
    public PassFail copperSulphate;

    @Expose
    @Column(name = "hamocue")
    public PassFail hamocue;

    @Expose
    @Column(name = "pack_type")
    public PackType packType;

    @Expose
    @Column(name = "reason_for_testing")
    public ReasonForTesting reasonForTesting;

    @Expose
    @Column(name = "victim_of_sexual_abuse")
    public YesNo victimOfSexualAbuse;

    @Expose
    @Column
    public YesNo pregnant;

    @Column(name = "breast_feeding")
    @Expose
    public YesNo breastFeeding;

    @Expose
    @Column(name = "weight")
    public Double weight;

    @Expose
    @Column(name = "blood_pressure")
    public String bloodPressure;

    @Expose
    @Column(name = "person")
    public Donor person;

    @Expose
    @Column(name = "entry")
    public String entry;

    public DonationStats(){
        super();
    }

    public static List<DonationStats> findByDonor(Donor donor){
        return new Select()
                .from(DonationStats.class)
                .where("person = ?", donor.getId())
                .execute();
    }

    public static List<DonationStats> getAll(){
        return new Select()
                .from(DonationStats.class)
                .execute();
    }

    public static DonationStats fromJSON(JSONObject object){
        DonationStats item = new DonationStats();
        try{
            if( ! object.isNull("id")){
                item.server_id = object.getLong("id");
            }
            item.feelingWellToday = YesNo.valueOf(object.getString("feelingWellToday"));
            item.refusedToDonate = YesNo.valueOf(object.getString("refusedToDonate"));
            item.beenToMalariaArea = YesNo.valueOf(object.getString("beenToMalariaArea"));
            item.mealOrSnack = YesNo.valueOf(object.getString("mealOrSnack"));
            item.dangerousOccupation = YesNo.valueOf(object.getString("dangerousOccupation"));
            item.rheumaticFever = YesNo.valueOf(object.getString("rheumaticFever"));
            item.lungDisease = YesNo.valueOf(object.getString("lungDisease"));
            item.cancer = YesNo.valueOf(object.getString("cancer"));
            item.diabetes = YesNo.valueOf(object.getString("diabetes"));
            item.chronicMedicalCondition = YesNo.valueOf(object.getString("chronicMedicalCondition"));
            item.beenToDentist = YesNo.valueOf(object.getString("beenToDentist"));
            item.takenAntibiotics = YesNo.valueOf(object.getString("takenAntibiotics"));
            item.injection = YesNo.valueOf(object.getString("injection"));
            item.beenIll = YesNo.valueOf(object.getString("beenIll"));
            item.receivedBloodTransfusion = YesNo.valueOf(object.getString("receivedBloodTransfusion"));
            item.hivTest = YesNo.valueOf(object.getString("hivTest"));
            item.beenTestedForHiv = YesNo.valueOf(object.getString("beenTestedForHiv"));
            item.contactWithPersonWithYellowJaundice = YesNo.valueOf(object.getString("contactWithPersonWithYellowJaundice"));
            item.accidentalExposureToBlood = YesNo.valueOf(object.getString("accidentalExposureToBlood"));
            item.beenTattooedOrPierced = YesNo.valueOf(object.getString("beenTattooedOrPierced"));
            item.injectedWithIllegalDrugs = YesNo.valueOf(object.getString("injectedWithIllegalDrugs"));
            item.sexWithSomeoneWithUnknownBackground = YesNo.valueOf(object.getString("sexWithSomeoneWithUnknownBackground"));
            item.exchangedMoneyForSex = YesNo.valueOf(object.getString("exchangedMoneyForSex"));
            item.trueForSexPartner = YesNo.valueOf(object.getString("trueForSexPartner"));
            item.sufferedFromSTD = YesNo.valueOf(object.getString("sufferedFromSTD"));
            item.contactWithPersonWithHepatitisB = YesNo.valueOf(object.getString("contactWithPersonWithHepatitisB"));
            item.sufferedFromNightSweats = YesNo.valueOf(object.getString("sufferedFromNightSweats"));
            item.victimOfSexualAbuse = YesNo.valueOf(object.getString("victimOfSexualAbuse"));
            if( ! object.isNull("pregnant")){
                item.pregnant = YesNo.valueOf(object.getString("pregnant"));
            }
            if( ! object.isNull("breastFeeding")){
                item.breastFeeding = YesNo.valueOf(object.getString("breastFeeding"));
            }
            item.copperSulphate = PassFail.valueOf(object.getString("copperSulphate"));
            if( ! object.isNull("hamocue")){
                item.hamocue = PassFail.valueOf(object.getString("hamocue"));
            }
            item.packType = PackType.valueOf(object.getString("packType"));
            item.weight = object.getDouble("weight");
            item.bloodPressure = object.getString("bloodPressure");
            item.entry = object.getString("entry");
            if( ! object.isNull("reasonForTesting")){
                item.reasonForTesting = ReasonForTesting.valueOf(object.getString("reasonForTesting"));
            }
        }catch (JSONException ex){
            ex.printStackTrace();
            return null;
        }
        return item;
    }

    public static ArrayList<DonationStats> fromJSON(JSONArray array){
        ArrayList<DonationStats> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++){
            JSONObject object = null;
            try{
                object = array.getJSONObject(i);
            }catch (JSONException ex){
                ex.printStackTrace();
                continue;
            }
            DonationStats item = fromJSON(object);
            if(item != null)
                list.add(item);
        }
        return list;
    }
}
