package com.gentlecorp.customer.testData;

public class CustomerTestQueryData extends CreateCustomerTestData {

 public static final String customerQuery = """
                query Customer($id: ID!) {
                    customer(id: $id) {
                          id
                          version
                          lastName
                          firstName
                          email
                          phoneNumber
                          username
                          tierLevel
                          subscribed
                          birthdate
                          gender
                          maritalStatus
                          customerState
                      }
                }
                """;

  public static final String fullCustomerQuery = """
      query Customer($id: ID!) {
          customer(id: $id) {
              id
              version
              lastName
              firstName
              email
              phoneNumber
              username
              tierLevel
              subscribed
              birthdate
              gender
              maritalStatus
              customerState
              contactOptions
              interests
              address {
                  street
                  houseNumber
                  zipCode
                  city
                  state
                  country
              }
          }
      }
      """;

  public static final String customersQuery = """
      query Customers {
          customers {
              id
              username
          }
      }
      """;

 public static final String customersFilterQuery = """
    query Customers($field: FilterOptions, $operator: Operator, $value: String) {
        customers(filter: { field: $field, operator: $operator, value: $value }) {
            id
            version
            lastName
            firstName
            email
            phoneNumber
            username
            tierLevel
            subscribed
            birthdate
            gender
            maritalStatus
            customerState
            contactOptions
            interests
            address {
                         street
                         houseNumber
                         zipCode
                         city
                         state
                         country
                     }
        }
    }
    """;

 public static final String customersMultipleFilterQuery = """
     query Customers ($and: [FilterInput]) {
         customers(
             filter: { AND: $and }
            ) {
                 username
                 contactOptions
                 interests
                 address {
                     street
                     houseNumber
                     zipCode
                     city
                     state
                     country
                 }
                 customerState
                 maritalStatus
                 gender
                 birthdate
                 subscribed
                 tierLevel
                 phoneNumber
                 email
                 firstName
                 lastName
                 version
                 id
             }
         }
    """;

 public static final String customerCreateQuery = """
     mutation createCustomer($input: CustomerInput!, $password: String!) {
          createCustomer(
              input: $input
              password: $password
          ) {
                     id
                     version
                     lastName
                     firstName
                     email
                     phoneNumber
                     username
                     tierLevel
                     subscribed
                     birthdate
                     gender
                     maritalStatus
                     customerState
                     address {
                         street
                         houseNumber
                         zipCode
                         city
                         state
                         country
                     }
                     contactOptions
                     interests
                 }
      }
    """;

 public static final String customerUpdateQuery = """
     mutation UpdateCustomer($input: CustomerUpdateInput!, $id: ID!, $version: String!) {
         updateCustomer(
             input: $input
             id: $id
             version: $version
         ) {
             message
             result {
                 id
                 version
                 lastName
                 firstName
                 email
                 phoneNumber
                 username
                 tierLevel
                 subscribed
                 birthdate
                 gender
                 maritalStatus
                 customerState
                 contactOptions
                 interests
             }
             affectedCount
             warnings
             success
         }
     }
     """;

 public static final String customersDeleteQuery = """
     mutation DeleteCustomer($id: ID!, $version: Int) {
         deleteCustomer(id: $id, version: $version) {
             message
             affectedCount
             warnings
         }
     }
     """;
}