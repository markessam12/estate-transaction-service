*** Settings ***
Library     RequestsLibrary
Library     JSONLibrary
Library     SoapLibrary
Library     Collections

*** Variables ***
${url}      http://localhost:8080/SampleProject-1.0-SNAPSHOT/api/estate
${setForSale}       1
${setNotForSale}    0

*** Test Cases ***
Do a GET Request and validate the response code
    [documentation]  This test case verifies that the response code of the GET Request should be 200,
    ...  the response body contains the 'title' key with value as 'London',
    ...  and the response body contains the key 'location_type'.
    [tags]  Smoke
    Create Session  mysession  ${url}  verify=true
    ${response}=  GET On Session  mysession  /owners
    Status Should Be  200  ${response}  #Check Status as 200


Do a Patch Request and validate the response code and response body
    [documentation]  This test case verifies that the response code of the GET Request should be 200,
    ...  the response body contains the 'title' key with value as 'London',
    ...  and the response body contains the key 'location_type'.
    [tags]  Smoke
    Create Session  mysession  ${url}  verify=true
    ${response}=  PATCH On Session  mysession  /properties/3      params=forsale=${setNotForSale}
    ${forsale}=    Collections.Get From Dictionary    ${response.json()}    forSale
    Status Should Be  200  ${response}  #Check Status as 200
    Should Be Equal As Integers    ${forsale}    ${setNotForSale}