package com.slash.collectorapp.Common;

import android.location.Location;

import com.slash.collectorapp.Model.User;
import com.slash.collectorapp.Remote.FCMClient;
import com.slash.collectorapp.Remote.IFCMService;
import com.slash.collectorapp.Remote.IGoogleAPI;
import com.slash.collectorapp.Remote.RetrofitClient;

public class Common {

    public static final String driver_tbl = "TrashCollectors";
    public static final String user_driver_tbl = "CollectorInformation";
    public static final String user_rider_tbl = "ResidentInformation";
    public static final String pickup_request_tbl = "TrashPickupRequest";
    public static final String token_tbl = "Tokens";

    public static User currentUser;

    public static Location mLastLocation=null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";

    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
