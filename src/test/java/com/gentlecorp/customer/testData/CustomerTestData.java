package com.gentlecorp.customer.testData;

public class CustomerTestData extends CustomerTestQueryData{


  public static final String SCHEMA_HOST = "http://localhost:";
  public static final String GRAPHQL_ENDPOINT = "/graphql";


  public static final String ADMIN_ID = "/00000000-0000-0000-0000-000000000000";

  public static final String ID_CALEB = "00000000-0000-0000-0000-000000000025";
  public static final String ID_ANNA = "/00000000-0000-0000-0000-000000000024";
  public static final String ID_HIROSHI = "00000000-0000-0000-0000-000000000018";
  public static final String ID_ERIK = "00000000-0000-0000-0000-000000000005";
  public static final String ID_LEROY = "00000000-0000-0000-0000-000000000026";

  public final static String CONTACT_R_ID = "/00000000-0000-0000-0000-000000000057";
  public static final String NOT_EXISTING_ID = "20000000-0000-0000-0000-000000000000";
  public static final String CALEB_CONTACT_ID_1 = "/20000000-0000-0000-0000-000000000000";

  public static final String SUPREME = "SUPREME";
  public static final String BASIC = "BASIC";
  public static final String ELITE = "ELITE";
  public static final String ADMIN = "ADMIN";
  public static final String USER = "USER";

  // Testrollen und -zugangsdaten
  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";
  public static final String ROLE_SUPREME = "gentlecg99";
  public static final String ROLE_ELITE = "leroy135";
  public static final String ROLE_BASIC = "erik";
  public static final String ROLE_PASSWORD = "p";

  // HTTP-Header
  public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
  public static final String HEADER_IF_MATCH = "If-Match";

  // Authentifizierungsinformationen
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  // Kundenattribute
  public static final String CUSTOMER = "customer";
  public static final String LAST_NAME = "lastName";
  public static final String FIRST_NAME = "firstName";
  public static final String EMAIL = "email";
  public static final String PHONE_NUMBER = "phoneNumber";
  public static final String TIER_LEVEL = "tierLevel";
  public static final String IS_SUBSCRIBED = "isSubscribed";
  public static final String BIRTHDATE = "birthdate";
  public static final String GENDER = "gender";
  public static final String MARITAL_STATUS = "maritalStatus";
  public static final String INTERESTS = "interests";
  public static final String CONTACT_OPTIONS = "contactOptions";

  //Query Parameter
  public static final String CUSTOMER_STATUS = "customerState";
  //public static final String PREFIX = "prefix";

  // Adressattribute
  public static final String ADDRESS = "address";
  public static final String STREET = "address_street";
  public static final String HOUSE_NUMBER = "houseNumber";
  public static final String ZIP_CODE = "address_zipCode";
  public static final String CITY = "address_city";
  public static final String STATE = "address_state";
  public static final String COUNTRY = "address_country";

  //Kontaktattribute
  public static final String RELATIONSHIP = "relationship";
  public static final String WITHDRAWAL_LIMIT = "withdrawalLimit";
  public static final String IS_EMERGENCY_CONTACT = "isEmergencyContact";

  // Hiroshi's Daten
  public static final String USERNAME_HIROSHI = "hiroshi.tanaka";
  public static final String LAST_NAME_HIROSHI = "Tanaka";
  public static final String FIRST_NAME_HIROSHI = "Hiroshi";
  public static final String EMAIL_HIROSHI = "hiroshi.tanaka@example.com";
  public static final String PHONE_NUMBER_HIROSHI = "+81-3-1234-5678";
  public static final String BIRTH_DATE_HIROSHI = "1988-06-20";
  public static final String STREET_HIROSHI = "Shibuya Crossing";
  public static final String HOUSE_NUMBER_HIROSHI = "1-2-3";
  public static final String ZIP_CODE_HIROSHI = "150-0001";
  public static final String CITY_HIROSHI = "Tokyo";
  public static final String STATE_HIROSHI = "Kanto";
  public static final String COUNTRY_HIROSHI = "Japan";

  // Erik's Daten
  public static final String USERNAME_ERIK = "erik";
  public static final String LAST_NAME_ERIK = "Schmidt";
  public static final String FIRST_NAME_ERIK = "Erik";
  public static final String EMAIL_ERIK = "erik.schmidt@example.com";
  public static final String PHONE_NUMBER_ERIK = "030-2345678";
  public static final String BIRTH_DATE_ERIK = "1982-03-25";
  public static final String STREET_ERIK = "Eichenstraße";
  public static final String HOUSE_NUMBER_ERIK = "8";
  public static final String ZIP_CODE_ERIK = "20255";
  public static final String CITY_ERIK = "Hamburg";
  public static final String STATE_ERIK = "Hamburg";
  public static final String COUNTRY_ERIK = "Deutschland";

  // Leroy's Daten
  public static final String USERNAME_LEROY = "leroy135";
  public static final String LAST_NAME_LEROY = "Jefferson";
  public static final String FIRST_NAME_LEROY = "Leroy";
  public static final String EMAIL_LEROY = "leroy135@icloud.com";
  public static final String PHONE_NUMBER_LEROY = "015111951223";
  public static final String BIRTH_DATE_LEROY = "1999-05-03";
  public static final String STREET_LEROY = "Connell Street";
  public static final String HOUSE_NUMBER_LEROY = "42";
  public static final String ZIP_CODE_LEROY = "D01 C3N0";
  public static final String CITY_LEROY = "Dublin";
  public static final String STATE_LEROY = "Leinster";
  public static final String COUNTRY_LEROY = "Ireland";

  // Caleb's Daten
  public static final String USERNAME_CALEB = "gentlecg99";
  public static final String LAST_NAME_CALEB = "Gyamfi";
  public static final String FIRST_NAME_CALEB = "Caleb";
  public static final String EMAIL_CALEB = "caleb_g@outlook.de";
  public static final String PHONE_NUMBER_CALEB = "015111951223";
  public static final String BIRTH_DATE_CALEB = "1999-05-03";
  public static final String STREET_CALEB = "Namurstraße";
  public static final String HOUSE_NUMBER_CALEB = "4";
  public static final String ZIP_CODE_CALEB = "70374";
  public static final String CITY_CALEB = "Stuttgart";
  public static final String STATE_CALEB = "Baden Württemberg";
  public static final String COUNTRY_CALEB = "Deutschland";

  // E-Tags
  public static final String ETAG_VALUE_MINUS_1 = "\"-1\"";
  public static final String ETAG_VALUE_0 = "\"0\"";
  public static final String ETAG_VALUE_1 = "\"1\"";
  public static final String ETAG_VALUE_2 = "\"2\"";
  public static final String ETAG_VALUE_3 = "\"3\"";
  public static final String INVALID_ETAG_VALUE = "\"3";

  // Problem Detail
  public static final String INVALID_KEY = "Invalid key: ";
  public static final int BAD_REQUEST_STATUS = 400;
  public static final String BAD_REQUEST_TITLE = "Bad Request";
  public static final String BAD_REQUEST_TYPE = "/problem/badRequest";

  public static final int TOTAL_CUSTOMERS = 27;

  //Query Parameter
  public static final String QUERY_SON = "son";
  public static final String QUERY_IVA = "iva";
  public static final String QUERY_G = "g";
  public static final String QUERY_M = "m";
  public static final String QUERY_XYZ = "xyz";
  public static final String QUERY_IVANOV = "ivanov";
  public static final String QUERY_ICLOUD_COM = "icloud.com";
  public static final String QUERY_IS_SUBSCRIBED = "true";
  public static final String QUERY_IS_NOT_SUBSCRIBED = "false";
  public static final String QUERY_BIRTH_DATE_BEFORE = "1991-01-01";
  public static final String QUERY_BIRTH_DATE_AFTER = "1999-01-01";
  public static final String QUERY_BIRTH_DATE_BETWEEN = "1991-01-01,1998-12-31";
  public static final String QUERY_ZIP_CODE_70374 = "70374";
  public static final String QUERY_ZIP_CODE_Y1000 = "Y1000";
  public static final String QUERY_ZIP_CODE_KA = "KA";
  public static final String QUERY_CITY_STUTTGART = "Stuttgart";
  public static final String QUERY_CITY_KUMASI = "kumasi";
  public static final String QUERY_CITY_TOK = "tok";
  public static final String QUERY_STATE_NEW_SOUTH_WALES = "New South Wales";
  public static final String QUERY_STATE_BA = "Ba";
  public static final String QUERY_COUNTRY_USA = "USA";
  public static final String QUERY_COUNTRY_LAND = "land";

  // Invalid Query Parameter
  public static final String INVALID = "invalid";
  public static final int INVALID_TIER_LEVEL_4 = 4;
  public static final String INVALID_BIRTHDATE_FORMAT = "invalid,2000-01-01";

  // Tier  Level
  public static final int TIER_LEVEL_3 = 3;
  public static final int TIER_LEVEL_2 = 2;
  public static final int TIER_LEVEL_1 = 1;

  public static final String GENDER_FEMALE = "FEMALE";
  public static final String GENDER_MALE = "MALE";
  public static final String GENDER_DIVERSE = "DIVERSE";

  // MArital Status
  public static final String MARITAL_STATUS_SINGLE = "SINGLE";
  public static final String MARITAL_STATUS_MARRIED = "MARRIED";
  public static final String MARITAL_STATUS_DIVORCED = "DIVORCED";
  public static final String MARITAL_STATUS_WIDOW = "WIDOWED";

  // Customer Status
  public static final String CUSTOMER_STATUS_ACTIVE = "ACTIVE";
  public static final String CUSTOMER_STATUS_INACTIVE = "INACTIVE";
  public static final String CUSTOMER_STATUS_CLOSED = "CLOSED";
  public static final String CUSTOMER_STATUS_BLOCKED = "BLOCKED";

  // Interessen
  public static final String INTEREST_INVESTMENTS = "INVESTMENTS";
  public static final String INTEREST_SAVINGS_AND_FINANCES = "SAVING_AND_FINANCE";
  public static final String INTEREST_CREDIT_AND_DEBT = "CREDIT_AND_DEBT";
  public static final String INTEREST_BANK_PRODUCTS_AND_SERVICES = "BANK_PRODUCTS_AND_SERVICES";
  public static final String INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING = "FINANCIAL_EDUCATION_AND_COUNSELING";
  public static final String INTEREST_REAL_ESTATE = "REAL_ESTATE";
  public static final String INTEREST_INSURANCE = "INSURANCE";
  public static final String INTEREST_SUSTAINABLE_FINANCE = "SUSTAINABLE_FINANCE";
  public static final String INTEREST_TECHNOLOGY_AND_INNOVATION = "TECHNOLOGY_AND_INNOVATION";
  public static final String INTEREST_TRAVEL = "TRAVEL";

  // Kontakt Optionen
  public static final String CONTACT_OPTION_PHONE = "PHONE";
  public static final String CONTACT_OPTION_EMAIL = "EMAIL";
  public static final String CONTACT_OPTION_LETTER = "LETTER";
  public static final String CONTACT_OPTION_SMS = "SMS";

  // Neue Konstanten für die spezifischen Werte
  public static final String NEW_USER_LAST_NAME = "Gyamfi";
  public static final String NEW_USER_FIRST_NAME = "Caleb";
  public static final String NEW_USER_PHONE_NUMBER = "015111951223";
  public static final String NEW_USER_BIRTH_DATE = "1999-05-03";
  public static final String NEW_USER_STREET = "Namurstraße";
  public static final String NEW_USER_HOUSE_NUMBER = "4";
  public static final String NEW_USER_ZIP_CODE = "70374";
  public static final String NEW_USER_CITY = "Stuttgart";
  public static final String NEW_USER_STATE = "Baden-Württemberg";
  public static final String NEW_USER_COUNTRY = "Germany";
  public static final String NEW_USER_PASSWORD = "Caleb123.";
  public static final String NEW_USER_SUBSCRIPTION = QUERY_IS_SUBSCRIBED;
  public static final String NEW_USER_GENDER = GENDER_MALE;
  public static final String NEW_USER_MARITAL_STATUS = MARITAL_STATUS_SINGLE;
  public static final String NEW_USER_INTERESTS = INTEREST_INVESTMENTS;
  public static final String NEW_USER_CONTACT_OPTIONS = CONTACT_OPTION_PHONE;
  public static final String SUPREME_USERNAME = "gentlecg99_supreme";
  public static final String ELITE_USERNAME = "gentlecg99_elite";
  public static final String BASIC_USERNAME = "gentlecg99_basic";
  public static final String SUPREME_EMAIL = "supreme@ok.de";
  public static final String ELITE_EMAIL = "elite@ok.de";
  public static final String BASIC_EMAIL = "basic@ok.de";

  public static final String INVALID_EMAIL = "kwame.owusuexample.com";
  public static final String EXISTING_EMAIL = "kwame.owusu@example.com";
  public static final String DUPLICATE_USERNAME = "gentlecg99";
  public static final String DUPLICATE_INTERESTS = NEW_USER_INTERESTS;
  public static final String DUPLICATE_CONTACT_OPTIONS = NEW_USER_CONTACT_OPTIONS;
  public static final String INVALID_LAST_NAME = "123Invalid";
  public static final String INVALID_FIRST_NAME = "Invalid123";
  public static final String INVALID_PHONE_NUMBER = "123";
  public static final String INVALID_USERNAME = "a";
  public static final int INVALID_TIER_LEVEL = 5;
  public static final String INVALID_GENDER = "INVALID";
  public static final String INVALID_MARITAL_STATUS = "INVALID";
  public static final String FUTURE_BIRTHDATE =  "A Date in the Future";

  public static final String NEW_CONTACT_LAST_NAME = "Rolly";
  public static final String NEW_CONTACT_FIRST_NAME = "Hola";
  public static final String NEW_CONTACT_RELATIONSHIP = "S";
  public static final int NEW_CONTACT_WITHDRAWAL_LIMIT = 50;
  public static final boolean NEW_CONTACT_IS_EMERGENCY = false;

  public static final String EXISTING_CONTACT_LAST_NAME = "Andersson";
  public static final String EXISTING_CONTACT_FIRST_NAME = "Eric";
  public static final String EXISTING_CONTACT_RELATIONSHIP = "S";
  public static final int EXISTING_CONTACT_WITHDRAWAL_LIMIT = 50;
  public static final boolean EXISTING_CONTACT_IS_EMERGENCY = false;

  public static final String INVALID_CONTACT_FIRST_NAME = "";
  public static final String INVALID_CONTACT_LAST_NAME = "";
  public static final String INVALID_CONTACT_RELATIONSHIP = "";
  public static final int INVALID_CONTACT_WITHDRAWAL_LIMIT = -1;
  public static final Boolean INVALID_CONTACT_IS_EMERGENCY = null;


  public static final String UPDATED_LAST_NAME = "Updatedastame";
  public static final String UPDATED_FIRST_NAME = "Updatedirstame";
  public static final String UPDATED_USERNAME = "Updatedirstadme";
  public static final String UPDATED_EMAIL = "updated.email@example.com";
  public static final String UPDATED_PHONE_NUMBER = "+49 987 654321";
  public static final int UPDATED_TIER_LEVEL = 2;
  public static final boolean UPDATED_IS_SUBSCRIBED = false;
  public static final String UPDATED_BIRTH_DATE = "1990-01-01";
  public static final String UPDATED_GENDER = "F";
  public static final String UPDATED_MARITAL_STATUS = "M";
  public static final String UPDATED_INTEREST = "IT";
  public static final String UPDATED_CONTACT_OPTION = "S";
  public static final String UPDATED_STREET = "Updated Street";
  public static final String UPDATED_HOUSE_NUMBER = "10B";
  public static final String UPDATED_ZIP_CODE = "54321";
  public static final String UPDATED_CITY = "Updated City";
  public static final String UPDATED_STATE = "Updated State";
  public static final String UPDATED_COUNTRY = "Updated Country";

  public static final String PASSWORD_PATH = "/password";
  public static final String NEW_PASSWORD = "123.Caleb";
  public static final String NEW_INVALID_PASSWORD = "p";

  public static final String AS = "asc";

}
