package greenbharat.cdac.com.greenbharat.endpoints;

/**
 * Created by cdac on 8/10/2016.
 */
public class URLHelper {
   // public static final String BASE_URL = "http://greenbharat.netparam.in/services";
    public static final String BASE_URL = "http://test.netparam.in/GreenRaj/services";
    public static final String GETUSERBYID = BASE_URL + "/getUserById";
    public static final String SIGNUP = BASE_URL + "/doSignUp";
    public static final String SIGNUPPlant = BASE_URL + "/doPlantUserSignup";
    public static final String LOGIN = BASE_URL + "/doLogin";
    public static final String GETGEO = BASE_URL + "/getGeo";
    public static final String GETPLANTS = BASE_URL + "/getPlants";
    public static final String DOADDPLANTORDER = BASE_URL + "/doAddPlantOrder";
    public static final String ADDPAYMENTINFO = BASE_URL + "/doAddPayment";
    public static final String ADDDONATIONPAYMENTINFO = BASE_URL + "/doDonationPayment";
    public static final String GETLANDPROPERTY = BASE_URL + "/getLandProperties";
    public static final String GENERATEOTP = BASE_URL + "/generateSignupOTP";
    public static final String GENERATEOTPFORSIGNUP = BASE_URL + "/generatePlantSignupOTP";
    public static final String GENERATEFORGOTPASSOTP = BASE_URL + "/generateForgotPasswordOTP";
    public static final String MATCHOTP = BASE_URL + "/matchOTP";
    public static final String REGISTERLAND = BASE_URL + "/doRegisterLand";
    public static final String GETREGISTEREDLANDS = BASE_URL + "/getRegisteredLands";
    public static final String CHANGEPASS = BASE_URL + "/doChangePassword";
    public static final String UPDATEPROFILE = BASE_URL + "/doUpdateProfile";
    public static final String GETCOUNTRIES = BASE_URL + "/getCountries";
    public static final String GETSTATES = BASE_URL + "/getStates";
    public static final String getCity = BASE_URL + "/getCities";
    public static final String GETPLANTCOST = BASE_URL + "/getPlantCost";
    public static final String GETORGANIZATION = BASE_URL + "/getOrganizations";
    public static final String GETPLANTORDERS = BASE_URL + "/getPlantOrders";
    public static final String GetAllottedLandTrees = BASE_URL + "/getAllottedLandTrees";
    public static final String GetAllottedOrderTrees = BASE_URL + "/getAllottedOrderTrees";
    public static final String verifyQR = BASE_URL + "/verifyQR";
    public static final String UPLOAD_CAPTUREIMAGE = BASE_URL + "/captureImage";
    public static final String GET_PLANT_GROWTH_IMAGES = BASE_URL + "/getPlantGrowthImages";
    public static final String UPDATE_FCM_ID = BASE_URL + "/updateFcmId";
    public static final String PAYMENT = BASE_URL + "/payment";
    public static final String TERMS_AND_CONDITION = BASE_URL + "/getTermsAndServices";
    public static final String GETALLTREEFRIENDS = BASE_URL + "/getAllTreeFriends";
    public static final String GETPROMOTERS = BASE_URL + "/getPromoters";
    public static final String GETPLANTDONORS = BASE_URL + "/getPlantDonors";
    public static final String VERIFY_REFERAL_CODE = BASE_URL + "/verifyReferalCode";

    public static final String BEAN_BASE_PACKAGE = "greenbharat.cdac.com.greenbharat.pojo";
}
