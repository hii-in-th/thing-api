# Thing api.

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
``` 
