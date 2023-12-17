print('START');

db = db.getSiblingDB('mongodb-post-service');

db.createUser(
    {
        user: 'rootadmin',
        password: 'password',
        roles: [{role: 'readWrite', db: 'mongodb-post-service'}]
    }
);

db.createCollection('posts');

print('END');
