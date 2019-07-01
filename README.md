# Thing api.
## [Docker hub.](https://cloud.docker.com/u/hiilab/repository/docker/hiilab/thing-api)

## Environment variable
 config for api server or docker env.
```log
    HII_ALONE     ->  หากมีการกำหนดค่าระบบจะทำงานแบบ stand alone
                      ทุกอย่างทำงานบนหน่วยความจำ in memory
    DB_URL        ->  สติง url สำหรับการเขื่อมต่อ database
    DB_USER       ->  database user
    DB_PASSWORD   ->  database password
    
    //Redis
    RE_HOST       ->  ip ของ server redis
    RE_PORT       ->  port ของ server redis
    RE_EXPIRE_SEC ->  ระบบลบอัตโนมัติของ redis หน่วยเป็นวินาที
    
    //Redis rsa key store. ถ้าไม่กำหนดจะใช้ค่าเดียวกับด้านบน
    HII_PRIVATE   ->  rsa private key pkcs8 format
    HII_PUBLIC    ->  rsa public key
```   
- การ gen private and public key

    ```batch
    #!/usr/bin/env bash
    
    openssl genrsa -out private_key.pem 4096
    openssl rsa -pubout -in private_key.pem -out public_key.pem
    
    # convert private key to pkcs8 format in order to import it from Java
    openssl pkcs8 -topk8 -in private_key.pem -inform pem -out private_key_pkcs8.pem -outform pem -nocrypt
    ```
    [Reference gen private and public key](https://gist.github.com/destan/b708d11bd4f403506d6d5bb5fe6a82c5)

## เชิงเทคนิค
การใช้งานจำเป็นต้องมี HTTP header **X-Requested-By** ด้วย ในส่วนของ Endpoint ที่ไม่ได้ใช้ Access token กำหนดค่ามาเป็นอะไรก็ได้

ตัวอย่างการเรียก ```/auth/tokens```
```log
POST /v1/auth/tokens HTTP/1.1
Host: 127.0.0.1:8080
Accept: application/json
Content-Type: application/json
X-Requested-By: kios
Authorization: Bearer acde
User-Agent: PostmanRuntime/7.15.0
Cache-Control: no-cache
Host: 127.0.0.1:8080
accept-encoding: gzip, deflate
content-length: 
Connection: keep-alive
cache-control: no-cache


```
  
ตัวอย่างการเรียก ```/sessions```
```log
POST /v1/sessions HTTP/1.1
Host: 127.0.0.1:8080
Accept: application/json
Content-Type: application/json
X-Requested-By: hii/7
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJzY29wdCI6WyIvdml0YWwiLCIvaGVpZ2h0IiwiL2JtaSJdLCJhdWQiOiJoaWkuaW4udGgiLCJzdWIiOiJoaWkvNyIsInJvbGUiOlsia2lvcyJdLCJuYmYiOjE1NjEwODczNDUsImlzcyI6ImF1dGguaGlpLmluLnRoIiwiZXhwIjoxNTYxMDg3NjY4LCJpYXQiOjE1NjEwODczNDUsImp0aSI6IjZiM2ViYzUxLWRlMWYtNGNkNi1hNzRjLTYyNWZjNTRkMDYwZCJ9.S6gyDpJX7KPm1gq_OMZu1F7R_HJPNs8xKQmApOCh1kuWp4y2b6mptEjpYXPcQHvFe_1pk9pTcJsxD50J0xlvaMIGSmtPD4ecbXeWSU5vlVXQZ-Z02QrA1_MFQRFXw6zpX3GNlZdbROwO8mXqgkxlOZFb4NDZPYLGPFewY5uAZZidX6G-Fvt_dJe47ddCFlonreAeDWnRujLPMSfoyAXBUPXn7aXiJb31DmS8RNwNEjuFsCxHGnIAWIabtF6lQZTLHvrXWOjOiRY86JF0bGlxMcsgTpMpYIhmX17M6ZWdTrIrEaHjC9FiUVsTQNXd3vKoniCxXqkhH5cJ6Gbyb1qTNaTZo1OTTPTRaEHYIcfkWvVN9Mw71sZD5A8kmEBW_8eT5kGXJAfx4LGe56xQ0IUz7pMhcjZwM0fNpa2C3gzdVHkFjCt51mYRuzttyJUOKPa040IU-7Xe5eY0FjDbZGwePlNvqiEOJlHE2IY6zLg72rPrDbeWgSB0oqU2yTwP5_xglWraOXx-8rEJlW6hPPM981tHl8ipco0nptZry3ezJyGwJSJDXFo9u2HbdWzhD5bQccAfJLrJVk8dISQbVhj4RAlHDZG1Ay1f9Bs9orY9FwlNEA_GcV9YCTN2JwZpOByc3tqL6y6wcFqe5aIvgvebjITzcmkQUn7-ZxGHdjMu99s
User-Agent: PostmanRuntime/7.15.0
Cache-Control: no-cache
Host: 127.0.0.1:8080
accept-encoding: gzip, deflate
content-length: 112
Connection: keep-alive
cache-control: no-cache

{
	"deviceId": "hii/70",
	"citizenId": "1101401444873",
	"citizenIdInput": "CARD",
	"birthDate" : "1976-06-05"
}
```
