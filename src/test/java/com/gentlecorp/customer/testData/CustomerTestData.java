package com.gentlecorp.customer.testData;

public class CustomerTestData extends UpdateCustomerTestData {

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
  public static final String USER_ADMIN = "admin";
  public static final String USER_USER = "user";
  public static final String USER_SUPREME = "gentlecg99";
  public static final String USER_ELITE = "leroy135";
  public static final String USER_BASIC = "erik";
  public static final String USER_PASSWORD = "p";

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
  public static final String SUBSCRIBED = "subscribed";
  public static final String BIRTHDATE = "birthdate";
  public static final String GENDER = "gender";
  public static final String MARITAL_STATUS = "maritalStatus";
  public static final String INTERESTS = "interests";
  public static final String CONTACT_OPTIONS = "contactOptions";
  public static final String CUSTOMER_STATUS = "customerState";

  // Adressattribute
  public static final String ADDRESS = "address";
  public static final String STREET = "street";
  public static final String HOUSE_NUMBER = "houseNumber";
  public static final String ZIP_CODE = "zipCode";
  public static final String CITY = "city";
  public static final String STATE = "state";
  public static final String COUNTRY = "country";

  // Filter Attribute
  public static final String ADDRESS_STREET = String.format("%s_%s",ADDRESS,STREET);
  public static final String ADDRESS_HOUSE_NUMBER = String.format("%s_%s",ADDRESS,HOUSE_NUMBER);
  public static final String ADDRESS_ZIP_CODE = String.format("%s_%s",ADDRESS,ZIP_CODE);
  public static final String ADDRESS_CITY = String.format("%s_%s",ADDRESS,CITY);
  public static final String ADDRESS_STATE = String.format("%s_%s",ADDRESS,STATE);
  public static final String ADDRESS_COUNTRY = String.format("%s_%s",ADDRESS,COUNTRY);

  //Kontaktattribute
  public static final String RELATIONSHIP = "relationship";
  public static final String WITHDRAWAL_LIMIT = "withdrawalLimit";
  public static final String EMERGENCY_CONTACT = "emergencyContact";
  public static final String START_DATE = "startDate";
  public static final String END_DATE = "endDate";

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

  // Tier  Level
  public static final int TIER_LEVEL_3 = 3;
  public static final int TIER_LEVEL_2 = 2;
  public static final int TIER_LEVEL_1 = 1;
  public static final int INVALID_TIER_LEVEL_4 = 4;

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

  public static final String AS = "asc";

}
