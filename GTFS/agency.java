public class agency implements writable{
    public String agency_id;
    public String agency_name;
    public String agency_url;
    public String agency_timezone;
    public String agency_lang;
    public String agency_phone;

    public agency(String id, String name, String url, String timezone, String lang, String phone){
        agency_id = id;
        agency_name = name;
        agency_url = url;
        agency_timezone = timezone;
        agency_lang = lang;
        agency_phone = phone;
    }

    public String toString(){
        return agency_id + "," + agency_name + "," + agency_url + "," + agency_timezone + "," + agency_lang + "," + agency_phone;
    }

    public static String getFormat(){
        return "agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone";
    }
}