# competitions-manager(RestAPI)
This application plays a role of backend server. Via REST interface client can get data 
provided by HTTP endpoints. 

## Preferred tool and JDK to run program manually
During the project development I used Intellij Idea.
with openjdk-19.0.2

## Set up
<b>Configuration should be set up in your IDE (example: Intellij idea -> Environment Variables)</b>
In Intellij Idea Select Run/Debug Configuration -> Edit Configurations -> Environment Variables

### Database change(REQUIRED)
Before you start your project you have to configure you database
- SPRING_DATASOURCE_URL= "database url with connection prefix -> jdbc:postgresql://"
- SPRING_DATASOURCE_USERNAME= "database username"
- SPRING_DATASOURCE_PASSWORD= "database password"

### Security JWT configuration(REQUIRED)
###### Generate your Custom token's keys
You can use any string for you refresh and access token key, but
it must be 512 bit long.

For this purpose you can also use to generate keys for you:
https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx

Access and refresh tokens keys
- AUTH_ACCESS-TOKEN-KEY= ${new_access_JWT_token_key}
- AUTH_REFRESH-TOKEN-KEY= ${new_refresh_JWT_token_key}

### Scorecard images store path(REQUIRED)
In the same configuration file please adjust directory for storing scorecard images
- MEDIA_IMAGES-STORAGE-PATH= "path to directory where uploaded images will be stored"

### Frontend application(REQUIRED if you run WEB client)
If your default frontend application works under different domain or port.
CORDS_ACCEPTED_ORIGIN= "URL for client application to allow CORS"

## Production WARNING
This code should not be used in any production environment due to security risks.
Deployed server does not have any TLS configuration for HTTPS connections.
(__For local use only.__)

### Docker build and run
#### Build
```PowerShell
docker build -t backend:0.1 .
```
#### Docker container run(container have custom Authorization keys but still don't use TLS)

```PowerShell
docker run --name ${run_container_name} -d -p ${host_post}:8080 
-e SPRING_DATASOURCE_URL=${database url}
-e SPRING_DATASOURCE_USERNAME=${database username}
-e SPRING_DATASOURCE_PASSWORD=${database password}
-e AUTH_ACCESS-TOKEN-KEY=${new_access_JWT_token} \
-e AUTH_REFRESH-TOKEN-KEY=${new_refresh_JWT_token} \
-e CORS_ALLOW_ORIGIN=${base_url_to_front_end_app} \
-e MEDIA_IMAGES-STORAGE-PATH=${new_path_to_media_files} \
backend:0.1
```
