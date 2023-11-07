print('START');

db = db.getSiblingDB('mongodb-friendship-service');

db.createUser(
    {
        user: 'rootadmin',
        password: 'password',
        roles: [{role: 'readWrite', db: 'mongodb-friendship-service'}]
    }
);

db.createCollection('friendships');

print('END');
