# Air polution and registered vehicles integration

This app is a simple tool for importing, exporting and visualizing data about air pollution and registered vehicles in Poland using the following technologies:
- Frontend: React.js
- Backend: Spring Boot
- Database: MySQL

## Source of data
- [Air pollution data](https://powietrze.gios.gov.pl/pjp/archives)
- [Registered vehicles data](https://api.cepik.gov.pl/doc)

## How to run the app

### Prerequisites
1. Clone the repository
2. `docker-compose up -d` to run the mysql database and phpmyadmin (will be available at http://localhost:5500)
3. Have Node.js and npm installed
4. Have Java 22 installed

### Run the frontend app
1. `cd frontend` to navigate to the frontend directory
2. `npm install` to install the dependencies
3. `npm run dev` to run the app
4. The frontend app will be available at http://localhost:5173

### Run the backend app
1. `cd backend` to navigate to the backend directory
2. Add `JAVA_HOME` environment variable to your system and set it to the path of your JDK
3. Create `application.yml` file in `src/main/resources` directory and add the following content: 
```yaml
is:
  datasource:
    url: jdbc:mysql://localhost:3369/IS_database
    user: IS_user
    password: pass
  security:
    jwt:
      secret: "secret"
      expiration: 86400000 # 1 day in milliseconds
```
4. `./mvnw spring-boot:run` to run the app
5. The backend app will be available at http://localhost:5000

