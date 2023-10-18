print('START');

db = db.getSiblingDB('user-service');

db.createUser(
    {
        user: 'rootadmin',
        password: 'password',
        roles: [{role: 'readWrite', db: 'user-service'}]
    }
);

db.createCollection('user');

print('END');