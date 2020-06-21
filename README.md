# hr-management-api

Application helps HR manager Personia get a grasp of her ever changing company’s hierarchy!

## Getting Started 

### Prerequisites

**Things need to install:**
```
1. Docker: Build and run application 
2. Postman: API testing tool
```

### Installing

**At current project directory we run command to build a docker image file:**
```
$ docker build -t hr-management-app .
```

### Running

**After build successful we will run our application by cmd:**
```
$ docker run -d -p 8282:8282 hr-management-app:latest
```

### Testing

**We will use Postman to test our API. Import my collection API to test at here:**

```
https://www.getpostman.com/collections/a3706d00abae398833d4
```

**Please set Basic Authentication type with user/pass when request to API as the following**

```
Username: admin
Password: admin 
```

- Example:

![](https://i.imgur.com/eCFwLtO.png)