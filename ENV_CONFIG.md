# Environment Variables Specification

The application configuration relies on the following variables.

## Backend Variables (`project/backend/.env`)

| Variable Name      | Description                                                    | Example Value                                       |
|--------------------|----------------------------------------------------------------|-----------------------------------------------------|
| `MONGODB_URI`      | MongoDB Atlas or Local connection URI string                   | `mongodb+srv://user:pass@cluster.mongodb.net/db`   |
| `MONGODB_DATABASE` | Targeted database name                                         | `turfbooking`                                       |
| `JWT_SECRET`       | Cryptographically secure HS512 secret string (min 64 bytes)    | `your_very_long_secure_jwt_secret_key`              |
| `JWT_EXPIRATION`   | Milliseconds until token expiration (default 24h)              | `86400000`                                          |
| `EMAIL_HOST`       | SMTP server address for transactional mailings                | `smtp.gmail.com`                                    |
| `EMAIL_PORT`       | SMTP port                                                      | `587`                                               |
| `EMAIL_USERNAME`   | Login email address for SMTP                                   | `notifications@example.com`                         |
| `EMAIL_PASSWORD`   | App Password or auth password for SMTP                         | `abcd-efgh-ijkl-mnop`                               |

## Frontend Variables (`project/frontend/.env`)

| Variable Name          | Description                                                | Example Value                                   |
|------------------------|------------------------------------------------------------|-------------------------------------------------|
| `REACT_APP_API_URL`    | Fully qualified base URL pointing to target Spring Boot API | `https://turf-booking-pp67.onrender.com/api`    |
