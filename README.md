# dbTrainingEnroll Application

## REQUIREMENTS

### Application used to manage team training enrollment 
1. This application will be composed from the following modules:
2. A public page where all trainings are displayed
3. Ask for enroll module
4. Send an e-mail to application address with e-mail title: 
	[training name][number of participants][day][time interval]
5. E-mail body will contain list of participants (each one by line)
6. The application will parse this e-mail title and body and display the possible attendees
7. PM will have access to this list and will approve for each member if they will attend selected session
8. A confirmation e-mail and an outlook invitation will be generated for each approved attendee and the list of attendees will be updated with selected students.
#### Statistics module
Display trainings and attendees
#### Technologies:
JAVA, Maven, Spring, Spring security, JPA, H2 / mySQL, Devtools, Angular 5


### Operational documentation
1. Infrastructure (MySQL DB Connection Details) [Wiki Link](https://docs.google.com/document/d/1k7pUnWurTup-SHAWuCbntgvyJzybui2--Ll2jM8ZuCo/edit?usp=sharing)

## HEROKU
1. The application is deployed at: https://db-training-enroll.herokuapp.com
2. Every push to master branch will update the deploy.
3. You can access the backend like on local-host by replacing http://localhost:PORT/ with the URL above.
4. Don't change the port settings in application.properties.
