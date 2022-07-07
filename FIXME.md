## Bugs found while developing tests

- Fixed `GET /owners/{ownerId}/pets/{petId}/visits` 
and `POST /owners/{ownerId}/pets/{petId}/visits`:
Spring MVC called method loadPetWithVisit(...) which created a new visit 
for the GET method and created an extra visit for the POST method. 
Deleting the method all together fixed the problem.

- Fixed `POST /owners/{ownerId}`: it didn't update an owner in DB.
- Fixed `POST /pets/{petId}`: it didn't update a pet in DB. 
