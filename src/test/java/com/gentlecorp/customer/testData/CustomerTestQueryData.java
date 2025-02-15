package com.gentlecorp.customer.testData;

public class CustomerTestQueryData {

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
}






