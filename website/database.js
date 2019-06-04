const fs = require('fs');
const path = require('path');

const Sequelize = require('sequelize');

var async = require('asyncawait/async');
var await = require('asyncawait/await');

// Initialize Sequelize
const sequelize = new Sequelize("bedrockconnect", "root", "", {
    host: "localhost",
    dialect: 'mysql',
    logging: false,

    pool: {
        max: 5,
        min: 0,
        acquire: 30000,
        idle: 10000
    },

    operatorsAliases: Sequelize.Op,
});

const database = {
    db: sequelize,
    Sequelize: Sequelize
};

/**
 * Tries to connect to the database and syncronise the schema. Will continue retrying if unable to connect.
 * @returns {Promise}
 */
database.connect = async (function() {
    return new Promise((resolve, reject) => {
        const tryConnect = () => {
            sequelize.sync().then(() => {
                console.log("Connected and synced to database.");
                resolve();
            }, (err) => {
                console.error("Unable to connect to database, retrying...", err.message);
                setTimeout(tryConnect, 5000);
            });
        };
        tryConnect();
    });
});

/**
 * Loads database definition models in /models
 * @returns {Promise}
 */
database.load = async (function() {
    console.log("Loading models...");

    return new Promise((resolve, reject) => {
        const dir = __dirname + "/models";
        fs.readdir(dir, (err, files) => {
            if (err) return reject(err);

            // Import the model files
            files
                .filter(file => (file.indexOf(".") !== 0))
                .forEach(file => {
                    const model = sequelize.import(path.join(dir, file));
                    database[model.name] = model;
                });

            // Call the associate method on each model file (if it exists), this is where relationships between the
            // loaded models can be defined.
            Object.keys(database).forEach(modelName => {
                if ("associate" in database[modelName]) {
                    database[modelName].associate(database);
                }
            });

            resolve();
        });
    });
});

module.exports = database;