
## Preface
  
The entry class is com.example.springboot.SpringbootApplication  

The program runs at localhost:8088   

"@" means type in the browser.  
"$" means type in the command line.  
"->" means the responses.   
  
@ http://localhost:8088/user  
-> we will see the memory-DB is empty, there is no user.  


## Body

### 1、create user by name and password, fail if exist  
  
$ curl -X POST -H "Content-Type: application/json" -d '{"name": "czb123","password": "Hello Việt Nam"}' "http://localhost:8088/user/"  
  
@ http://localhost:8088/user   
 
-> we will see the user "czb123" has the encrypted password "SGVsbG8gVmnhu4d0IE5hbQ=="  

try again  
$ curl -X POST -H "Content-Type: application/json" -d '{"name": "czb123","password": "Hello Việt Nam"}' "http://localhost:8088/user/" 
-> {"timestamp":1649077095650,"status":400,"error":"Bad Request","exception":"java.lang.IllegalArgumentException","message":"user czb123 already exist","path":"/user/"}   
  
it fails because the user already exists

### 2、delete user by name and should fail if not exist

$ curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/user/czb123"  
-> {"status":true,"result":null}  
  
try again  
$ curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/user/czb123"  
-> {"timestamp":1649077838510,"status":404,"error":"Not Found","exception":"com.example.springboot.exception.UserNotFoundException","message":"could not find user name 'czb123'.","path":"/user/czb123"}  
  
### 3、create role by role name and fail if exist  

$ curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/role/"  
-> -> we will see the role "role123"  

try again  
$ curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/role/"  
-> {"timestamp":1649079503103,"status":400,"error":"Bad Request","exception":"java.lang.IllegalArgumentException","message":"role role123 already exist","path":"/role/"}  
  
### 4、delete role by role name and fail if not exist   

$ curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/role/role123"  
-> {"status":true,"result":null}  

try again  
$ curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/role/role123"  
-> {"timestamp":1649079919956,"status":404,"error":"Not Found","exception":"com.example.springboot.exception.RoleNotFoundException","message":"could not find role name 'role123'.","path":"/role/role123"}  

### 5、add role to user by user name and role name, nothing happen if role already associate with user  
$ curl -X PUT -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/czb123/addrole"  
-> {"id":1,"name":"czb123","password":"SGVsbG8gVmnhu4d0IE5hbQ==","roles":["role123"]}  

try again  
$ curl -X PUT -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/czb123/addrole"  
-> {"id":1,"name":"czb123","password":"SGVsbG8gVmnhu4d0IE5hbQ==","roles":["role123"]}  


### 6、authenticate by user name and password, return 2-hour token or error if not found
$ curl -X POST -H "Content" -d '{"name": "czb123","password": "Hello Việt Nam"}' "http://localhost:8088/user/authenticate"  
-> {"status":true,"result":{"id":2,"name":"Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ3ODU5MDEy"}}  

### 7、invalidate by token, return nothing and the token is no longer valid after the call  
$ curl -X PUT -H "Content-Type: application/json" "http://localhost:8088/user/invalidate/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx"
-> {"status":true,"result":"Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx"}  

try again
$ curl -X PUT -H "Content-Type: application/json" "http://localhost:8088/user/invalidate/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx"
-> {"timestamp":1649149019811,"status":400,"error":"Bad Request","exception":"java.lang.IllegalArgumentException","message":"token Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx does not exist","path":"/user/invalidate/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx"}  

### 8、check role by token and role, return true if user identified by token belong to role, false otherwise, error if token expired   
$ curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/checkrole/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNDM3NDM1"  
-> {"status":true,"result":"success! user identified by token is belong to role. userRoles: [role123]input role name: role123"}  

try another one  
$ curl -X POST -H "Content-Type: application/json" -d '{"name": "role122"}' "http://localhost:8088/user/checkrole/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNDM3NDM1"  
-> {"status":false,"result":"user identified by token is NOT belong to role. userRoles: [role123]input role name: role122"  


### 9、all roles by token, return all roles for user, error if the token is invalid
$ curl -X GET -H "Content-Type: application/json" "http://localhost:8088/user/allroles/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNzY0Nzcy"  
-> {"status":true,"result":["role123"]}(basePOST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/checkrole/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNDM3NDM"  

try an invalid token
$ curl -X GET "http://localhost:8088/user/allroles/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNzY0Nzc"   
-> {"status":false,"result":"token is beyond 2 hours, inputTimestamp: 164916376477 currentTimestamp: 1649163935344"}  


## End 
  