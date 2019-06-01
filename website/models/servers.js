module.exports = function(sequelize, DataTypes) {
    const Server = sequelize.define("Server", {
        index: {
            type: DataTypes.INTEGER,
            unique: true,
            autoIncrement: true,
            primaryKey: true
        },
        ip: DataTypes.TEXT,
        join: DataTypes.TEXT,
        serverList: DataTypes.TEXT,
    }, {
        tableName: 'servers',
        timestamps: false,
    });

    return Server;
};